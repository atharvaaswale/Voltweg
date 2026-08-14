package com.voltweg.ui

import androidx.lifecycle.ViewModel
import com.voltweg.data.ChargerSpeedCategory
import com.voltweg.data.ChargingStation
import com.voltweg.data.ConnectorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class VoltwegTab(val title: String) {
    EXPLORE("Explore"),
    MAP("Map"),
    FAVORITES("Favorites"),
    SETTINGS("Settings")
}

enum class ActiveScreen {
    MAIN_TABS,
    STATION_DETAILS,
    SEARCH_FILTERS,
    LOCATION_PERMISSION
}

data class VoltwegUiState(
    val currentTab: VoltwegTab = VoltwegTab.EXPLORE,
    val currentScreen: ActiveScreen = ActiveScreen.MAIN_TABS,
    val stations: List<ChargingStation> = emptyList(),
    val selectedStationId: String = "",
    val searchQuery: String = "",
    val recentSearches: List<String> = emptyList(),
    val isFilterSheetOpen: Boolean = false,
    val selectedConnectors: Set<ConnectorType> = setOf(ConnectorType.CCS2, ConnectorType.TYPE2),
    val selectedSpeed: ChargerSpeedCategory = ChargerSpeedCategory.FAST,
    val selectedMaxDistanceKm: Double = 5.0,
    val isOffline: Boolean = false,
    val isLoading: Boolean = true,
    val isLocationGranted: Boolean = true
) {
    val selectedStation: ChargingStation?
        get() = stations.find { it.id == selectedStationId } ?: stations.firstOrNull()

    val filteredStations: List<ChargingStation>
        get() {
            return stations.filter { station ->
                val matchesQuery = searchQuery.isBlank() ||
                        station.name.contains(searchQuery, ignoreCase = true) ||
                        station.city.contains(searchQuery, ignoreCase = true) ||
                        station.district.contains(searchQuery, ignoreCase = true) ||
                        station.address.contains(searchQuery, ignoreCase = true)

                val matchesDistance = station.distanceKm <= selectedMaxDistanceKm

                val matchesConnector = selectedConnectors.isEmpty() || station.points.any {
                    it.connectorType in selectedConnectors
                } || station.availableConnectorsText().contains("CCS", ignoreCase = true)

                matchesQuery && matchesDistance && matchesConnector
            }
        }

    val favoriteStations: List<ChargingStation>
        get() = stations.filter { it.isFavorite }
}

fun ChargingStation.availableConnectorsText(): String {
    return "$totalConnectors Connectors available"
}

class VoltwegViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VoltwegUiState())
    val uiState: StateFlow<VoltwegUiState> = _uiState.asStateFlow()

    fun selectTab(tab: VoltwegTab) {
        _uiState.update {
            it.copy(
                currentTab = tab,
                currentScreen = ActiveScreen.MAIN_TABS
            )
        }
    }

    fun selectStation(stationId: String) {
        _uiState.update {
            it.copy(selectedStationId = stationId)
        }
    }

    fun openStationDetails(stationId: String) {
        _uiState.update {
            it.copy(
                selectedStationId = stationId,
                currentScreen = ActiveScreen.STATION_DETAILS
            )
        }
    }

    fun openSearchAndFilters() {
        _uiState.update {
            it.copy(currentScreen = ActiveScreen.SEARCH_FILTERS)
        }
    }

    fun openLocationPermissionScreen() {
        _uiState.update {
            it.copy(currentScreen = ActiveScreen.LOCATION_PERMISSION)
        }
    }

    fun navigateBack() {
        _uiState.update {
            it.copy(
                currentScreen = ActiveScreen.MAIN_TABS,
                isFilterSheetOpen = false
            )
        }
    }

    fun toggleFavorite(stationId: String) {
        _uiState.update { state ->
            val updated = state.stations.map {
                if (it.id == stationId) it.copy(isFavorite = !it.isFavorite) else it
            }
            state.copy(stations = updated)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFilterSheet(open: Boolean) {
        _uiState.update { it.copy(isFilterSheetOpen = open) }
    }

    fun toggleConnectorFilter(connector: ConnectorType) {
        _uiState.update { state ->
            val newSet = state.selectedConnectors.toMutableSet()
            if (newSet.contains(connector)) {
                newSet.remove(connector)
            } else {
                newSet.add(connector)
            }
            state.copy(selectedConnectors = newSet)
        }
    }

    fun setSpeedFilter(speed: ChargerSpeedCategory) {
        _uiState.update { it.copy(selectedSpeed = speed) }
    }

    fun setDistanceFilter(distanceKm: Double) {
        _uiState.update { it.copy(selectedMaxDistanceKm = distanceKm) }
    }

    fun clearAllFilters() {
        _uiState.update {
            it.copy(
                selectedConnectors = emptySet(),
                selectedSpeed = ChargerSpeedCategory.AC,
                selectedMaxDistanceKm = 10.0
            )
        }
    }

    fun toggleOfflineMode() {
        _uiState.update { it.copy(isOffline = !it.isOffline) }
    }

    fun toggleLoadingState() {
        _uiState.update { it.copy(isLoading = !it.isLoading) }
    }

    fun setLocationPermissionGranted(granted: Boolean) {
        _uiState.update {
            it.copy(
                isLocationGranted = granted,
                currentScreen = ActiveScreen.MAIN_TABS
            )
        }
    }
}
