package com.voltweg.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.voltweg.core.network.model.GeocodingItemDto
import com.voltweg.data.ChargerSpeedCategory
import com.voltweg.data.ChargerStatus
import com.voltweg.data.ChargingStation
import com.voltweg.data.ConnectorType
import com.voltweg.data.MockData
import com.voltweg.ui.theme.VoltwegOnPrimary
import com.voltweg.ui.theme.VoltwegOnSecondaryContainer
import com.voltweg.ui.theme.VoltwegPrimary
import com.voltweg.ui.theme.VoltwegSecondaryContainer
import com.voltweg.ui.theme.VoltwegSurfaceContainer
import com.voltweg.ui.theme.VoltwegSurfaceContainerLow

@Composable
fun SearchFilterScreen(
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onBackClick: () -> Unit,
    onStationSelect: (String) -> Unit,
    onLocationSelect: (Double, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is SearchSideEffect.NavigateToExploreWithLocation -> {
                    onLocationSelect(effect.lat, effect.lng, effect.name)
                }
            }
        }
    }

    SearchFilterScreenContent(
        state = uiState,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick,
        onStationSelect = onStationSelect,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchFilterScreenContent(
    state: SearchUiState,
    onEvent: (SearchUiEvent) -> Unit,
    onBackClick: () -> Unit,
    onStationSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("search_filter_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Search Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("search_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = state.query,
                    onValueChange = { onEvent(SearchUiEvent.OnQueryChanged(it)) },
                    placeholder = {
                        Text(
                            text = "Search location...",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (state.isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(20.dp)
                                    .testTag("search_inline_loading"),
                                strokeWidth = 2.dp,
                                color = VoltwegPrimary
                            )
                        } else if (state.query.isNotEmpty()) {
                            IconButton(onClick = { onEvent(SearchUiEvent.OnQueryChanged("")) }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Voice",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    singleLine = true,
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = VoltwegSurfaceContainerLow,
                        unfocusedContainerColor = VoltwegSurfaceContainerLow,
                        focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .testTag("search_text_input")
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (state.query.isNotBlank()) {
                    // Search Mode: loading / suggestions / empty state
                    if (state.isSearching) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("search_progress_indicator"),
                                    strokeWidth = 3.dp,
                                    color = VoltwegPrimary
                                )
                            }
                        }
                    } else if (state.suggestions.isNotEmpty()) {
                        item {
                            Text(
                                text = "SUGGESTIONS",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        items(state.suggestions) { suggestion ->
                            LocationSuggestionItem(
                                suggestion = suggestion,
                                onClick = {
                                    val lat = suggestion.lat?.toDoubleOrNull() ?: SearchViewModel.DEFAULT_LAT
                                    val lng = suggestion.lon?.toDoubleOrNull() ?: SearchViewModel.DEFAULT_LNG
                                    val name = suggestion.name
                                        ?: suggestion.displayName?.split(",")?.firstOrNull()
                                        ?: "Selected Location"
                                    onEvent(
                                        SearchUiEvent.OnLocationSelected(
                                            lat = lat,
                                            lng = lng,
                                            name = name
                                        )
                                    )
                                }
                            )
                        }
                    } else {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No locations found for \"${state.query}\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try checking the spelling or searching for a city name",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                } else {
                    // Browse Mode (query empty): recent searches, popular stations, nearby list
                    // Recent Searches
                    if (state.recentSearches.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RECENT SEARCHES",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(onClick = { onEvent(SearchUiEvent.OnClearRecentSearches) }) {
                                    Text("Clear all", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                state.recentSearches.forEach { search ->
                                    RecentSearchItem(
                                        query = search,
                                        onClick = { onEvent(SearchUiEvent.OnQueryChanged(search)) }
                                    )
                                }
                            }
                        }
                    }

                    // Popular Stations
                    item {
                        val popularStations = if (state.searchResults.isNotEmpty()) {
                            state.searchResults.take(4)
                        } else {
                            MockData.sampleStations.take(4)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "POPULAR STATIONS",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(popularStations, key = { it.id }) { station ->
                                    PopularStationCard(
                                        station = station,
                                        onClick = { onStationSelect(station.id) }
                                    )
                                }
                            }
                        }
                    }

                    // Charging Stations Near You header + Filter Chip
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CHARGING STATIONS NEAR YOU",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${state.searchResults.size} stations found",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            FilterChip(
                                selected = state.isFilterSheetOpen,
                                onClick = { onEvent(SearchUiEvent.OnToggleFilterSheet(true)) },
                                label = { Text("Filter") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Tune,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = state.isFilterSheetOpen,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("open_filters_button")
                            )
                        }
                    }

                    // Initial Station List
                    if (state.searchResults.isNotEmpty()) {
                        items(state.searchResults) { station ->
                            SearchResultStationCard(
                                station = station,
                                onClick = { onStationSelect(station.id) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Filters Bottom Sheet
        AnimatedVisibility(
            visible = state.isFilterSheetOpen,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = VoltwegSurfaceContainer,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("filters_bottom_sheet")
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            .align(Alignment.CenterHorizontally)
                    )

                    // Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filters",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { onEvent(SearchUiEvent.OnToggleFilterSheet(false)) }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Connectors
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "CONNECTORS",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ConnectorType.entries.forEach { connector ->
                                    val isSelected = connector in state.selectedConnectors
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onEvent(SearchUiEvent.OnToggleConnector(connector)) },
                                        label = { Text(connector.displayName) },
                                        shape = CircleShape,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = VoltwegSecondaryContainer,
                                            selectedLabelColor = VoltwegOnSecondaryContainer
                                        )
                                    )
                                }
                            }
                        }

                        // Speed Segmented Control
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "SPEED",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VoltwegSurfaceContainerLow)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ChargerSpeedCategory.entries.forEach { speed ->
                                    val isSelected = state.selectedSpeed == speed
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                                            .clickable { onEvent(SearchUiEvent.OnSelectSpeed(speed)) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = speed.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Distance Control
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "DISTANCE",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf(1.0, 5.0, 10.0).forEach { dist ->
                                    val isSelected = state.selectedMaxDistanceKm == dist
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onEvent(SearchUiEvent.OnSelectDistance(dist)) }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${dist.toInt()} km",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isSelected) VoltwegPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onEvent(SearchUiEvent.OnClearAllFilters) },
                            modifier = Modifier.testTag("clear_filters_button")
                        ) {
                            Text(
                                text = "Clear all",
                                style = MaterialTheme.typography.labelLarge,
                                color = VoltwegPrimary
                            )
                        }

                        Button(
                            onClick = { onEvent(SearchUiEvent.OnToggleFilterSheet(false)) },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VoltwegPrimary,
                                contentColor = VoltwegOnPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("apply_filters_button")
                        ) {
                            Text(
                                text = "Apply filters",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationSuggestionItem(
    suggestion: GeocodingItemDto,
    onClick: () -> Unit
) {
    val title = suggestion.name
        ?: suggestion.displayName?.split(",")?.firstOrNull()
        ?: "Location"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            suggestion.displayName?.let { display ->
                Text(
                    text = display,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RecentSearchItem(
    query: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = query,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = Icons.Filled.NorthWest,
            contentDescription = "Select",
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun PopularStationCard(
    station: ChargingStation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = VoltwegSurfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .width(220.dp)
            .height(130.dp)
            .testTag("popular_card_${station.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (station.imageUrl != null) {
                    AsyncImage(
                        model = station.imageUrl,
                        contentDescription = station.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.EvStation,
                        contentDescription = station.name,
                        tint = VoltwegPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = station.speedCategory.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${station.availableConnectors}/${station.totalConnectors} connectors available",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SearchResultStationCard(
    station: ChargingStation,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            VoltwegSurfaceContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("search_result_card_${station.id}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (station.imageUrl != null) {
                    AsyncImage(
                        model = station.imageUrl,
                        contentDescription = station.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EvStation,
                            contentDescription = station.name,
                            tint = VoltwegPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                val isAvailable = station.status == ChargerStatus.AVAILABLE
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isAvailable) "AVAILABLE" else "OCCUPIED",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 9.sp),
                        color = if (isAvailable) VoltwegPrimary else MaterialTheme.colorScheme.error
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 16.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Address",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${station.address.split(",").firstOrNull()} • ${station.distanceKm} km",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VoltwegSurfaceContainerLow)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CCS2 • ${station.maxPowerKw} kW",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "${station.availableConnectors}/${station.totalConnectors} Available",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
