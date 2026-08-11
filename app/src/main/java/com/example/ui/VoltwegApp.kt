package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.VoltwegBottomBar
import com.example.ui.components.VoltwegTopAppBar
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.LocationPermissionScreen
import com.example.ui.screens.MapScreen
import com.example.ui.screens.SearchFilterScreen
import com.example.ui.screens.StationDetailsScreen
import com.example.ui.theme.VoltwegOnSecondaryContainer
import com.example.ui.theme.VoltwegPrimary
import com.example.ui.theme.VoltwegSecondaryContainer

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
            SearchFilterScreen(
                searchQuery = uiState.searchQuery,
                recentSearches = uiState.recentSearches,
                stations = uiState.filteredStations,
                selectedConnectors = uiState.selectedConnectors,
                selectedSpeed = uiState.selectedSpeed,
                selectedMaxDistanceKm = uiState.selectedMaxDistanceKm,
                isFilterSheetOpen = uiState.isFilterSheetOpen,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onRecentSearchSelect = {
                    viewModel.updateSearchQuery(it)
                },
                onStationSelect = { stationId ->
                    viewModel.openStationDetails(stationId)
                },
                onToggleFilterSheet = { viewModel.toggleFilterSheet(it) },
                onToggleConnector = { viewModel.toggleConnectorFilter(it) },
                onSelectSpeed = { viewModel.setSpeedFilter(it) },
                onSelectDistance = { viewModel.setDistanceFilter(it) },
                onClearAllFilters = { viewModel.clearAllFilters() },
                onBackClick = { viewModel.navigateBack() },
                modifier = modifier
            )
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
                topBar = {
                    val activeStation = uiState.selectedStation
                    VoltwegTopAppBar(
                        title = "Voltweg",
                        showMenuButton = true,
                        isFavorite = activeStation?.isFavorite ?: false,
                        onMenuClick = {
                            viewModel.openLocationPermissionScreen()
                        },
                        onFavoriteClick = {
                            activeStation?.let { viewModel.toggleFavorite(it.id) }
                        }
                    )
                },
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
                                stations = uiState.filteredStations,
                                searchQuery = uiState.searchQuery,
                                isOffline = uiState.isOffline,
                                isLoading = uiState.isLoading,
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onSearchClick = { viewModel.openSearchAndFilters() },
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
