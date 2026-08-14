package com.voltweg.ui.screens.explore

import com.voltweg.data.ChargingStation

data class ExploreUiState(
    val stations: List<ChargingStation> = emptyList(),
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val locationName: String = ""
)

sealed interface ExploreUiEvent {
    data object OnRefreshTriggered : ExploreUiEvent
    data class OnStationClicked(val id: String) : ExploreUiEvent
    data object OnSearchClicked : ExploreUiEvent
}

sealed interface ExploreSideEffect {
    data class NavigateToDetails(val id: String) : ExploreSideEffect
    data object NavigateToMap : ExploreSideEffect
}
