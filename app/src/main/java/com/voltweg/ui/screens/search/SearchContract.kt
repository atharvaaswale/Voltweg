package com.voltweg.ui.screens.search

import com.voltweg.core.network.model.GeocodingItemDto
import com.voltweg.data.ChargerSpeedCategory
import com.voltweg.data.ChargingStation
import com.voltweg.data.ConnectorType

data class SearchUiState(
    val query: String = "",
    val suggestions: List<GeocodingItemDto> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isSearching: Boolean = false,
    val searchResults: List<ChargingStation> = emptyList(),
    val isFilterSheetOpen: Boolean = false,
    val selectedConnectors: Set<ConnectorType> = emptySet(),
    val selectedSpeed: ChargerSpeedCategory = ChargerSpeedCategory.AC,
    val selectedMaxDistanceKm: Double = 10.0
)

sealed interface SearchUiEvent {
    data class OnQueryChanged(val query: String) : SearchUiEvent
    data class OnLocationSelected(val lat: Double, val lng: Double, val name: String) : SearchUiEvent
    data object OnClearRecentSearches : SearchUiEvent
    data class OnToggleFilterSheet(val isOpen: Boolean) : SearchUiEvent
    data class OnToggleConnector(val connector: ConnectorType) : SearchUiEvent
    data class OnSelectSpeed(val speed: ChargerSpeedCategory) : SearchUiEvent
    data class OnSelectDistance(val distance: Double) : SearchUiEvent
    data object OnClearAllFilters : SearchUiEvent
}

sealed interface SearchSideEffect {
    data class NavigateToExploreWithLocation(val lat: Double, val lng: Double, val name: String) : SearchSideEffect
}
