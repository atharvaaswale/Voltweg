package com.voltweg.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voltweg.ui.components.VoltwegBottomBar
import com.voltweg.ui.components.VoltwegTopAppBar
import com.voltweg.ui.screens.explore.ExploreScreen
import com.voltweg.ui.screens.FavoritesScreen
import com.voltweg.ui.screens.LocationPermissionScreen
import com.voltweg.ui.screens.MapScreen
import com.voltweg.ui.screens.StationDetailsScreen

@Composable
fun VoltwegApp(
    viewModel: VoltwegViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (uiState.currentScreen) {
        ActiveScreen.STATION_DETAILS -> {
            val station = uiState.selectedStation
            if (station != null) {
                StationDetailsScreen(
                    station = station,
                    onBackClick = { viewModel.navigateBack() },
                    onFavoriteClick = { viewModel.toggleFavorite(station.id) },
                    onNavigateClick = {
                        Toast.makeText(context, "Starting navigation to ${station.name}", Toast.LENGTH_SHORT).show()
                    },
                    onOpenMapsClick = {
                        Toast.makeText(context, "Opening maps for ${station.address}", Toast.LENGTH_SHORT).show()
                    },
                    modifier = modifier
                )
            } else {
                viewModel.navigateBack()
            }
        }

        ActiveScreen.SEARCH_FILTERS -> {
            viewModel.navigateBack()
        }

        ActiveScreen.LOCATION_PERMISSION -> {
            LocationPermissionScreen(
                onAllowClick = {
                    viewModel.setLocationPermissionGranted(true)
                    Toast.makeText(context, "Location permission granted", Toast.LENGTH_SHORT).show()
                },
                onNotNowClick = {
                    viewModel.setLocationPermissionGranted(false)
                },
                modifier = modifier
            )
        }

        ActiveScreen.MAIN_TABS -> {
            Scaffold(
                /*topBar = {
                    VoltwegTopAppBar(title = "Voltweg")
                },*/
                bottomBar = {
                    VoltwegBottomBar(
                        currentTab = uiState.currentTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                },
                modifier = modifier.testTag("voltweg_main_screen")
            ) { innerPadding ->
                AnimatedContent(
                    targetState = uiState.currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition",
                    modifier = Modifier.padding(innerPadding)
                ) { tab ->
                    when (tab) {
                        VoltwegTab.EXPLORE -> {
                            ExploreScreen(
                                onStationClick = { stationId ->
                                    viewModel.openStationDetails(stationId)
                                },
                                onViewMapClick = {
                                    viewModel.selectTab(VoltwegTab.MAP)
                                }
                            )
                        }

                        VoltwegTab.MAP -> {
                            MapScreen(
                                stations = uiState.filteredStations,
                                selectedStation = uiState.selectedStation,
                                onStationSelected = { stationId ->
                                    viewModel.selectStation(stationId)
                                },
                                onViewDetailsClick = { stationId ->
                                    viewModel.openStationDetails(stationId)
                                },
                                onFilterClick = {
                                    viewModel.openSearchAndFilters()
                                },
                                onLocateMeClick = {
                                    Toast.makeText(context, "Centered map on your current location", Toast.LENGTH_SHORT).show()
                                },
                                onCloseSheetClick = {
                                    // Keep screen clean
                                }
                            )
                        }

                        VoltwegTab.FAVORITES -> {
                            FavoritesScreen(
                                favoriteStations = uiState.favoriteStations,
                                isOffline = uiState.isOffline,
                                isLoading = uiState.isLoading,
                                onStationClick = { stationId ->
                                    viewModel.openStationDetails(stationId)
                                },
                                onFavoriteToggle = { stationId ->
                                    viewModel.toggleFavorite(stationId)
                                },
                                onExploreClick = {
                                    viewModel.selectTab(VoltwegTab.EXPLORE)
                                },
                                onToggleOffline = { viewModel.toggleOfflineMode() },
                                onToggleLoading = { viewModel.toggleLoadingState() },
                                onRequestLocationClick = { viewModel.openLocationPermissionScreen() }
                            )
                        }

                        VoltwegTab.SETTINGS -> {
                            FavoritesScreen(
                                favoriteStations = uiState.favoriteStations,
                                isOffline = uiState.isOffline,
                                isLoading = uiState.isLoading,
                                onStationClick = { stationId ->
                                    viewModel.openStationDetails(stationId)
                                },
                                onFavoriteToggle = { stationId ->
                                    viewModel.toggleFavorite(stationId)
                                },
                                onExploreClick = {
                                    viewModel.selectTab(VoltwegTab.EXPLORE)
                                },
                                onToggleOffline = { viewModel.toggleOfflineMode() },
                                onToggleLoading = { viewModel.toggleLoadingState() },
                                onRequestLocationClick = { viewModel.openLocationPermissionScreen() }
                            )
                        }
                    }
                }
            }
        }
    }
}
