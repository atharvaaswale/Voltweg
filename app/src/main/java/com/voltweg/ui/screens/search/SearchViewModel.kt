package com.voltweg.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.voltweg.core.database.AppDatabase
import com.voltweg.core.database.dao.SearchHistoryDao
import com.voltweg.core.database.entity.SearchHistoryEntity
import com.voltweg.core.network.NetworkModule
import com.voltweg.core.network.NominatimApi
import com.voltweg.data.ChargerSpeedCategory
import com.voltweg.data.ConnectorType
import com.voltweg.data.repository.StationRepository
import com.voltweg.data.repository.StationRepositoryImpl
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val nominatimApi: NominatimApi,
    private val stationRepository: StationRepository,
    private val searchHistoryDao: SearchHistoryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _sideEffects = Channel<SearchSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        _queryFlow
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                val trimmed = query.trim()
                if (trimmed.isNotEmpty()) {
                    fetchSuggestions(trimmed)
                } else {
                    _uiState.update { it.copy(suggestions = emptyList(), isSearching = false) }
                }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            searchHistoryDao.getRecentSearches().collect { history ->
                _uiState.update { it.copy(recentSearches = history.map { it.query }) }
            }
        }

        // Load the initial list of stations near a default location (Berlin).
        fetchStations(DEFAULT_LAT, DEFAULT_LNG)
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnQueryChanged -> {
                val newQuery = event.query
                _uiState.update {
                    it.copy(
                        query = newQuery,
                        isSearching = newQuery.isNotBlank(),
                        suggestions = if (newQuery.isBlank()) emptyList() else it.suggestions
                    )
                }
                _queryFlow.value = newQuery
            }
            is SearchUiEvent.OnLocationSelected -> {
                saveToHistory(event.name)
                fetchStations(event.lat, event.lng)
                _sideEffects.trySend(SearchSideEffect.NavigateToExploreWithLocation(event.lat, event.lng, event.name))
            }
            SearchUiEvent.OnClearRecentSearches -> {
                viewModelScope.launch {
                    searchHistoryDao.clearHistory()
                }
            }
            is SearchUiEvent.OnToggleFilterSheet -> {
                _uiState.update { it.copy(isFilterSheetOpen = event.isOpen) }
            }
            is SearchUiEvent.OnToggleConnector -> {
                _uiState.update { state ->
                    val newSet = state.selectedConnectors.toMutableSet()
                    if (newSet.contains(event.connector)) {
                        newSet.remove(event.connector)
                    } else {
                        newSet.add(event.connector)
                    }
                    state.copy(selectedConnectors = newSet)
                }
            }
            is SearchUiEvent.OnSelectSpeed -> {
                _uiState.update { it.copy(selectedSpeed = event.speed) }
            }
            is SearchUiEvent.OnSelectDistance -> {
                _uiState.update { it.copy(selectedMaxDistanceKm = event.distance) }
            }
            SearchUiEvent.OnClearAllFilters -> {
                _uiState.update {
                    it.copy(
                        selectedConnectors = emptySet(),
                        selectedSpeed = ChargerSpeedCategory.AC,
                        selectedMaxDistanceKm = 10.0
                    )
                }
            }
        }
    }

    private fun fetchSuggestions(query: String) {
        _uiState.update { it.copy(isSearching = true) }
        viewModelScope.launch {
            try {
                val response = nominatimApi.searchLocation(query = query)
                _uiState.update { it.copy(suggestions = response, isSearching = false) }
            } catch (e: Exception) {
                Log.e("SearchDebug", "Geocoding error", e)
                _uiState.update { it.copy(suggestions = emptyList(), isSearching = false) }
            }
        }
    }

    private fun fetchStations(lat: Double, lng: Double) {
        viewModelScope.launch {
            stationRepository.getStations(lat, lng).collect { result ->
                _uiState.update { it.copy(searchResults = result.stations) }
            }
        }
    }

    private fun saveToHistory(name: String) {
        viewModelScope.launch {
            searchHistoryDao.insertSearch(SearchHistoryEntity(name))
        }
    }

    companion object {
        const val DEFAULT_LAT = 52.5200
        const val DEFAULT_LNG = 13.4050

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val database = AppDatabase.get(context)
                SearchViewModel(
                    nominatimApi = NetworkModule.nominatimApi,
                    stationRepository = StationRepositoryImpl(
                        dao = database.stationDao(),
                        api = NetworkModule.openChargeMapApi
                    ),
                    searchHistoryDao = database.searchHistoryDao()
                )
            }
        }
    }
}

typealias SearchFilterViewModel = SearchViewModel
