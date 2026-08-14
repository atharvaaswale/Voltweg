package com.voltweg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voltweg.data.ChargerStatus
import com.voltweg.data.ChargingPoint
import com.voltweg.data.ChargingStation
import com.voltweg.ui.components.VoltwegMapCanvas
import com.voltweg.ui.components.VoltwegTopAppBar
import com.voltweg.ui.theme.VoltwegOnPrimary
import com.voltweg.ui.theme.VoltwegPrimary
import com.voltweg.ui.theme.VoltwegSurfaceContainer
import com.voltweg.ui.theme.VoltwegSurfaceContainerHigh
import com.voltweg.ui.theme.VoltwegSurfaceContainerLow

@Composable
fun StationDetailsScreen(
    station: ChargingStation,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onNavigateClick: () -> Unit,
    onOpenMapsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            VoltwegTopAppBar(
                title = station.name,
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                    )
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onNavigateClick,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VoltwegPrimary,
                        contentColor = VoltwegOnPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("station_navigate_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Navigation,
                        contentDescription = "Navigate",
                        tint = VoltwegOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NAVIGATE",
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp)
                    )
                }
            }
        },
        modifier = modifier.testTag("station_details_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Status Header
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${station.availableConnectors} / ${station.totalConnectors} Available",
                        style = MaterialTheme.typography.headlineLarge,
                        color = VoltwegPrimary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = "Ultra Fast",
                            tint = VoltwegPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = station.speedCategory.displayName.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = VoltwegPrimary
                        )
                    }
                }

                // Fluid progress line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val progress = if (station.totalConnectors > 0) {
                        station.availableConnectors.toFloat() / station.totalConnectors
                    } else 0.5f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(3.dp)
                            .background(VoltwegPrimary)
                    )
                }

                Text(
                    text = "Last updated ${station.lastUpdated}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Charging Points Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "CHARGING POINTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (station.points.isNotEmpty()) {
                        station.points.forEach { point ->
                            ChargingPointRow(point = point)
                        }
                    } else {
                        // Fallback default points
                        ChargingPointRow(
                            ChargingPoint("1", com.voltweg.data.ConnectorType.CCS2, 150, ChargerStatus.AVAILABLE, 3, 3)
                        )
                        ChargingPointRow(
                            ChargingPoint("2", com.voltweg.data.ConnectorType.TYPE2, 22, ChargerStatus.AVAILABLE, 1, 1)
                        )
                        ChargingPointRow(
                            ChargingPoint("3", com.voltweg.data.ConnectorType.CCS2, 150, ChargerStatus.OCCUPIED, 2, 0)
                        )
                    }
                }
            }

            // Location Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "LOCATION",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            VoltwegMapCanvas(
                                stations = listOf(station),
                                selectedStationId = station.id,
                                onStationSelected = {},
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = station.address.split(",").firstOrNull() ?: station.address,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = station.city,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = onOpenMapsClick,
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier.testTag("open_maps_button")
                            ) {
                                Text(
                                    text = "OPEN MAPS",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = VoltwegPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Operator Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "OPERATOR",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VoltwegSurfaceContainerHigh)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Business,
                            contentDescription = "Operator",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = station.operatorName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Support: ${station.operatorSupport}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ChargingPointRow(point: ChargingPoint) {
    val isAvailable = point.status == ChargerStatus.AVAILABLE || point.availablePoints > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isAvailable) MaterialTheme.colorScheme.surface else VoltwegSurfaceContainerLow)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isAvailable) 0.3f else 0.1f),
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isAvailable) VoltwegSurfaceContainer else VoltwegSurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (point.connectorType == com.voltweg.data.ConnectorType.TYPE2) Icons.Filled.ElectricalServices else Icons.Filled.EvStation,
                    contentDescription = point.connectorType.displayName,
                    tint = if (isAvailable) VoltwegPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column {
                Text(
                    text = point.connectorType.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Up to ${point.powerKw} kW",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (isAvailable) "Available" else "Occupied",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isAvailable) VoltwegPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${point.availablePoints} ${if (point.availablePoints == 1) "point" else "points"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
