package com.voltweg.ui.screens.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.voltweg.core.network.model.GeocodingItemDto
import com.voltweg.data.ChargerSpeedCategory
import com.voltweg.data.ChargerStatus
import com.voltweg.data.ChargingStation
import com.voltweg.data.ConnectorType
import com.voltweg.ui.components.VoltwegMapCanvas
import com.voltweg.ui.theme.VoltwegOnPrimary
import com.voltweg.ui.theme.VoltwegOnSecondaryContainer
import com.voltweg.ui.theme.VoltwegOnSurfaceVariant
import com.voltweg.ui.theme.VoltwegPrimary
import com.voltweg.ui.theme.VoltwegSecondaryContainer
import com.voltweg.ui.theme.VoltwegStatusAvailableBg
import com.voltweg.ui.theme.VoltwegStatusAvailableText
import com.voltweg.ui.theme.VoltwegStatusOccupiedBg
import com.voltweg.ui.theme.VoltwegStatusOccupiedText
import com.voltweg.ui.theme.VoltwegSurfaceContainer
import com.voltweg.ui.theme.VoltwegSurfaceContainerLow
import com.voltweg.ui.theme.VoltwegTheme
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = viewModel(factory = ExploreViewModel.Factory),
    onSearchClick: () -> Unit = {},
    onStationClick: (String) -> Unit = {},
    onViewMapClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.onEvent(ExploreUiEvent.OnRefreshTriggered)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is ExploreSideEffect.NavigateToDetails -> onStationClick(effect.id)
                ExploreSideEffect.NavigateToMap -> onViewMapClick()
            }
        }
    }

    ExploreScreenContent(
        state = uiState,
        suggestions = suggestions,
        locationPermissionsState = locationPermissionsState,
        onSearchClick = onSearchClick,
        onStationClick = { viewModel.onEvent(ExploreUiEvent.OnStationClicked(it)) },
        onViewMapClick = onViewMapClick,
        onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) },
        onSearchSubmit = { query -> viewModel.onSearchSubmit(query) },
        onClearSearch = { viewModel.clearSearch() },
        onSuggestionSelected = { lat, lng, name ->
            viewModel.onLocationSelected(lat, lng, name)
        },
        onApplyFilters = { connectors, minPowerKw, distanceKm ->
            viewModel.applyFilters(connectors, minPowerKw, distanceKm)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ExploreScreenContent(
    state: ExploreUiState,
    suggestions: List<GeocodingItemDto>,
    locationPermissionsState: MultiplePermissionsState?,
    onSearchClick: () -> Unit,
    onStationClick: (String) -> Unit,
    onViewMapClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit = {},
    onSearchSubmit: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    onSuggestionSelected: (Double, Double, String) -> Unit = { _, _, _ -> },
    onApplyFilters: (Set<ConnectorType>, Int, Double) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var showFilterSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedConnectors by remember { mutableStateOf(setOf(ConnectorType.CCS2, ConnectorType.TYPE2)) }
    var selectedSpeed by remember { mutableStateOf(ChargerSpeedCategory.FAST) }
    var selectedDistanceKm by remember { mutableStateOf(5.0) }

    Box(modifier = modifier.fillMaxSize().statusBarsPadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
        // Brand Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Voltweg",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = VoltwegPrimary,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(VoltwegSurfaceContainerLow)
                    .widthIn(max = 180.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = VoltwegPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = state.locationName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Title Section
        Text(
            text = "Charging near you",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location Permission Request Card
        if (locationPermissionsState != null && !locationPermissionsState.allPermissionsGranted) {
            LocationPermissionCard(
                onRequestPermission = { locationPermissionsState.launchMultiplePermissionRequest() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Search Bar + Suggestions Overlay
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    onSearchQueryChanged(query)
                },
                placeholder = {
                    Text(
                        text = "Search city or location",
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    onClearSearch()
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier.testTag("explore_filter_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = "Filters",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        onSearchSubmit(searchQuery)
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = VoltwegSurfaceContainerLow,
                    unfocusedContainerColor = VoltwegSurfaceContainerLow,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_bar")
            )

            if (searchQuery.isNotEmpty() && suggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .padding(top = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VoltwegSurfaceContainerLow)
                        .zIndex(2f)
                        .testTag("explore_search_suggestions"),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(suggestions) { suggestion ->
                        val title = suggestion.name
                            ?: suggestion.displayName?.split(",")?.firstOrNull()
                            ?: "Location"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    keyboardController?.hide()
                                    val lat = suggestion.lat?.toDoubleOrNull() ?: return@clickable
                                    val lng = suggestion.lon?.toDoubleOrNull() ?: return@clickable
                                    searchQuery = ""
                                    onClearSearch()
                                    onSuggestionSelected(lat, lng, title)
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Offline Banner
        if (state.isOffline) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(VoltwegSurfaceContainerLow)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("offline_banner"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudOff,
                    contentDescription = "Offline",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You're offline | Showing previously loaded stations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Skeleton Loading State
        if (state.isLoading && state.stations.isEmpty()) {
            LoadingSkeleton()
        } else {
            // NEARBY Section Title
            Text(
                text = "NEARBY",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stations List (fills remaining vertical space)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("explore_screen_list"),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                userScrollEnabled = true
            ) {
                items(state.stations, key = { it.id }) { station ->
                    ExploreStationRow(
                        station = station,
                        isOffline = state.isOffline,
                        onClick = { onStationClick(station.id) }
                    )
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showFilterSheet = false
                    }
                },
                sheetState = sheetState,
                containerColor = VoltwegSurfaceContainer
            ) {
                ExploreFilterSheet(
                    selectedConnectors = selectedConnectors,
                    selectedSpeed = selectedSpeed,
                    selectedDistanceKm = selectedDistanceKm,
                    onConnectorToggle = { connector ->
                        selectedConnectors = if (connector in selectedConnectors) {
                            selectedConnectors - connector
                        } else {
                            selectedConnectors + connector
                        }
                    },
                    onSpeedSelect = { selectedSpeed = it },
                    onDistanceSelect = { selectedDistanceKm = it },
                    onClearAll = {
                        selectedConnectors = setOf(ConnectorType.CCS2, ConnectorType.TYPE2)
                        selectedSpeed = ChargerSpeedCategory.FAST
                        selectedDistanceKm = 5.0
                    },
                    onApply = {
                        scope.launch {
                            sheetState.hide()
                            showFilterSheet = false
                            onApplyFilters(selectedConnectors, minPowerKwFor(selectedSpeed), selectedDistanceKm)
                        }
                    },
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showFilterSheet = false
                        }
                    }
                )
            }
        }
    }
}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExploreFilterSheet(
    selectedConnectors: Set<ConnectorType>,
    selectedSpeed: ChargerSpeedCategory,
    selectedDistanceKm: Double,
    onConnectorToggle: (ConnectorType) -> Unit,
    onSpeedSelect: (ChargerSpeedCategory) -> Unit,
    onDistanceSelect: (Double) -> Unit,
    onClearAll: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(vertical = 16.dp),
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
            IconButton(onClick = onDismiss) {
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
                        val isSelected = connector in selectedConnectors
                        FilterChip(
                            selected = isSelected,
                            onClick = { onConnectorToggle(connector) },
                            label = { Text(connector.displayName) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else {
                                null
                            },
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
                        val isSelected = selectedSpeed == speed
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { onSpeedSelect(speed) }
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
                        val isSelected = selectedDistanceKm == dist
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onDistanceSelect(dist) }
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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onClearAll,
                modifier = Modifier.testTag("clear_filters_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = VoltwegPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Clear all",
                    style = MaterialTheme.typography.labelLarge,
                    color = VoltwegPrimary
                )
            }

            Button(
                onClick = onApply,
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

private fun minPowerKwFor(speed: ChargerSpeedCategory): Int = when (speed) {
    ChargerSpeedCategory.AC -> 1
    ChargerSpeedCategory.FAST -> 22
    ChargerSpeedCategory.ULTRA_FAST -> 100
}

@Composable
private fun LocationPermissionCard(onRequestPermission: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = VoltwegSurfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = VoltwegPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enable Location",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "To find charging stations near you, Voltweg needs access to your device location.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRequestPermission,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VoltwegPrimary)
            ) {
                Text(text = "Grant Permission", color = VoltwegOnPrimary)
            }
        }
    }
}

@Composable
private fun LoadingSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(4) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VoltwegSurfaceContainerLow)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(16.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(12.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true)
@Composable
fun ExploreScreenPreview() {
    VoltwegTheme {
        ExploreScreenContent(
            state = ExploreUiState(stations = emptyList()),
            suggestions = emptyList(),
            locationPermissionsState = null,
            onSearchClick = {},
            onStationClick = {},
            onViewMapClick = {}
        )
    }
}

@Composable
private fun ExploreStationRow(
    station: ChargingStation,
    isOffline: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
            .testTag("station_row_${station.id}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VoltwegSurfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.EvStation,
                    contentDescription = station.name,
                    tint = VoltwegOnSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Navigation,
                        contentDescription = "Distance",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = station.specText(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.wrapContentWidth(),
            horizontalAlignment = Alignment.End
        ) {
            val (badgeBg, badgeText, label) = when (station.status) {
                ChargerStatus.AVAILABLE, ChargerStatus.OPERATIONAL -> Triple(
                    VoltwegStatusAvailableBg,
                    VoltwegStatusAvailableText,
                    if (station.status == ChargerStatus.OPERATIONAL) "Operational" else "Available"
                )
                ChargerStatus.OCCUPIED, ChargerStatus.FULL -> Triple(
                    VoltwegStatusOccupiedBg,
                    VoltwegStatusOccupiedText,
                    if (station.status == ChargerStatus.FULL) "Full" else "Occupied"
                )
                ChargerStatus.NOT_OPERATIONAL -> Triple(
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.error,
                    "Not operational"
                )
                ChargerStatus.UNKNOWN -> Triple(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    "Unknown"
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                    color = badgeText,
                    maxLines = 1
                )
            }

            if (isOffline) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Last updated ${station.lastUpdated}",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}

private fun ChargingStation.specText(): String {
    val connectorLabel = when (points.firstOrNull()?.connectorType) {
        ConnectorType.CHADEMO -> "CHAdeMO"
        ConnectorType.TYPE2 -> "Type 2"
        else -> "CCS"
    }
    return "${formatDistance(distanceKm)} km • $totalConnectors $connectorLabel • up to $maxPowerKw kW"
}

private fun formatDistance(km: Double): String = when {
    km >= 100 -> km.roundToInt().toString()
    else -> {
        val oneDecimal = (km * 10).roundToInt() / 10.0
        if (oneDecimal % 1.0 == 0.0) oneDecimal.toInt().toString() else oneDecimal.toString()
    }
}
