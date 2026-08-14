package com.voltweg.ui.screens.explore

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.voltweg.core.database.AppDatabase
import com.voltweg.core.network.NetworkModule
import com.voltweg.core.network.NominatimApi
import com.voltweg.core.network.OpenChargeMapApi
import com.voltweg.core.network.model.GeocodingItemDto
import com.voltweg.core.location.GeocoderHelper
import com.voltweg.core.location.LocationTracker
import com.voltweg.data.ConnectorType
import com.voltweg.data.repository.StationMapper
import com.voltweg.data.repository.StationRepository
import com.voltweg.data.repository.StationRepositoryImpl
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
class ExploreViewModel(
    private val repository: StationRepository,
    private val locationTracker: LocationTracker,
    private val geocoderHelper: GeocoderHelper,
    private val api: OpenChargeMapApi = NetworkModule.openChargeMapApi,
    private val nominatimApi: NominatimApi = NetworkModule.nominatimApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private val _sideEffects = Channel<ExploreSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    private val _filters = MutableStateFlow(ExploreFilters())
    private var activeLocation: Location? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _suggestions = MutableStateFlow<List<GeocodingItemDto>>(emptyList())
    val suggestions: StateFlow<List<GeocodingItemDto>> = _suggestions.asStateFlow()

    private val _queryFlow = MutableStateFlow("")

    private var searchJob: Job? = null

    private var locationJob: Job? = null

    init {
        _queryFlow
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query -> searchLocations(query) }
            .launchIn(viewModelScope)
        loadInitialLocation()
    }

    fun onEvent(event: ExploreUiEvent) {
        when (event) {
            ExploreUiEvent.OnRefreshTriggered -> refresh()
            is ExploreUiEvent.OnStationClicked ->
                _sideEffects.trySend(ExploreSideEffect.NavigateToDetails(event.id))
            ExploreUiEvent.OnSearchClicked -> Unit
        }
    }

    fun searchLocations(query: String) {
        _searchQuery.value = query
        val trimmed = query.trim()
        searchJob?.cancel()
        if (trimmed.isEmpty()) {
            _suggestions.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            try {
                val results = nominatimApi.searchLocation(query = trimmed)
                _suggestions.value = results
            } catch (e: Exception) {
                Log.e(TAG, "Nominatim search failed for query: '$trimmed'", e)
                _suggestions.value = emptyList()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _queryFlow.value = query
    }

    fun onSearchSubmit(query: String) {
        _searchQuery.value = query
        searchLocations(query)
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchQuery.value = ""
        _queryFlow.value = ""
        _suggestions.value = emptyList()
    }

    fun onLocationSelected(latitude: Double, longitude: Double, cityName: String) {
        activeLocation = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        _uiState.update {
            it.copy(
                locationName = cityName,
                isLoading = true
            )
        }
        viewModelScope.launch {
            loadStationsFor(latitude, longitude)
        }
    }

    fun applyFilters(
        connectors: Set<ConnectorType>,
        minPowerKw: Int,
        distanceKm: Double
    ) {
        _filters.update {
            it.copy(
                connectors = connectors,
                minPowerKw = minPowerKw,
                distanceKm = distanceKm
            )
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val location = activeLocation ?: retryGetLocation() ?: return@launch
            fetchFilteredStations(location.latitude, location.longitude)
        }
    }

    private fun refresh() {
        loadInitialLocation()
    }

    private suspend fun loadStationsFor(latitude: Double, longitude: Double) {
        repository.getStations(latitude, longitude).collect { result ->
            _uiState.update {
                it.copy(
                    stations = result.stations,
                    isLoading = false,
                    isOffline = result.isOffline
                )
            }
        }
    }

    private suspend fun fetchFilteredStations(latitude: Double, longitude: Double) {
        try {
            val filters = _filters.value
            val remote = api.getNearbyStations(
                latitude = latitude,
                longitude = longitude,
                distance = filters.distanceKm,
                distanceUnit = "KM",
                connectionTypeId = filters.connectionTypeIdString(),
                minPowerKw = filters.minPowerKw.takeIf { it > 0 }
            )
            val stations = StationMapper.toDomain(remote.map(StationMapper::toEntity))
            _uiState.update {
                it.copy(
                    stations = stations,
                    isLoading = false,
                    isOffline = false
                )
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(isLoading = false, isOffline = true) }
        }
    }

    private fun loadInitialLocation() {
        if (locationJob?.isActive == true) return
        _uiState.update { it.copy(isLoading = true) }
        locationJob = viewModelScope.launch {
            val location = retryGetLocation()
            if (location == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            activeLocation = location

            val cityName = geocoderHelper.getCityName(location.latitude, location.longitude)
            if (cityName != null) {
                _uiState.update { it.copy(locationName = cityName) }
            }

            loadStationsFor(location.latitude, location.longitude)
        }
    }

    private suspend fun retryGetLocation(): Location? {
        var attempt = 0
        while (attempt < LOCATION_MAX_ATTEMPTS) {
            try {
                val location = locationTracker.getCurrentLocation()
                if (location != null) {
                    return location
                }
            } catch (e: Exception) {
                Log.e(TAG, "Location retrieval failed", e)
                return null
            }
            delay(LOCATION_RETRY_DELAY_MS)
            attempt++
        }
        return null
    }

    companion object {
        private const val LOCATION_RETRY_DELAY_MS = 2_000L
        private const val LOCATION_MAX_ATTEMPTS = 5
        private const val TAG = "ExploreViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                val database = AppDatabase.get(context)
                ExploreViewModel(
                    repository = StationRepositoryImpl(
                        dao = database.stationDao(),
                        api = NetworkModule.openChargeMapApi
                    ),
                    locationTracker = LocationTracker(context),
                    geocoderHelper = GeocoderHelper(context),
                    api = NetworkModule.openChargeMapApi,
                    nominatimApi = NetworkModule.nominatimApi
                )
            }
        }
    }
}

data class ExploreFilters(
    val connectors: Set<ConnectorType> = setOf(ConnectorType.CCS2, ConnectorType.TYPE2),
    val minPowerKw: Int = 22,
    val distanceKm: Double = 5.0
) {
    fun connectionTypeIdString(): String? {
        if (connectors.isEmpty()) return null
        return connectors.mapNotNull { connector ->
            when (connector) {
                ConnectorType.CCS2 -> CCS_CONNECTION_TYPE_ID
                ConnectorType.TYPE2 -> TYPE2_CONNECTION_TYPE_ID
                ConnectorType.CHADEMO -> CHADEMO_CONNECTION_TYPE_ID
            }
        }.joinToString(",")
    }

    companion object {
        private const val CCS_CONNECTION_TYPE_ID = "33"
        private const val TYPE2_CONNECTION_TYPE_ID = "25"
        private const val CHADEMO_CONNECTION_TYPE_ID = "2"
    }
}
