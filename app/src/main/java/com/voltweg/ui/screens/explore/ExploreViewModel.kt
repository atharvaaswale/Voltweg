package com.voltweg.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.voltweg.core.database.AppDatabase
import com.voltweg.core.network.NetworkModule
import com.voltweg.core.location.GeocoderHelper
import com.voltweg.core.location.LocationTracker
import com.voltweg.data.repository.StationRepository
import com.voltweg.data.repository.StationRepositoryImpl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val repository: StationRepository,
    private val locationTracker: LocationTracker,
    private val geocoderHelper: GeocoderHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private val _sideEffects = Channel<ExploreSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        onEvent(ExploreUiEvent.OnRefreshTriggered)
    }

    fun onEvent(event: ExploreUiEvent) {
        when (event) {
            ExploreUiEvent.OnRefreshTriggered -> refresh()
            is ExploreUiEvent.OnStationClicked ->
                _sideEffects.trySend(ExploreSideEffect.NavigateToDetails(event.id))
            ExploreUiEvent.OnSearchClicked -> Unit
        }
    }

    private fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val location = locationTracker.getCurrentLocation()
            val lat = location?.latitude ?: BERLIN_LATITUDE
            val lng = location?.longitude ?: BERLIN_LONGITUDE

            if (location != null) {
                val cityName = geocoderHelper.getCityName(lat, lng)
                if (cityName != null) {
                    _uiState.update { it.copy(locationName = cityName) }
                }
            }

            repository.getStations(lat, lng)
                .collect { result ->
                    _uiState.update {
                        it.copy(
                            stations = result.stations,
                            isLoading = false,
                            isOffline = result.isOffline
                        )
                    }
                }
        }
    }

    companion object {
        private const val BERLIN_LATITUDE = 52.5200
        private const val BERLIN_LONGITUDE = 13.4050

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
                    geocoderHelper = GeocoderHelper(context)
                )
            }
        }
    }
}
