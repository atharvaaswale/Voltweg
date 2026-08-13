package com.voltweg.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.voltweg.data.ChargerStatus
import com.voltweg.data.ChargingStation
import com.voltweg.ui.theme.VoltwegOnPrimary
import com.voltweg.ui.theme.VoltwegOnSecondaryFixed
import com.voltweg.ui.theme.VoltwegOnSurfaceVariant
import com.voltweg.ui.theme.VoltwegPrimary
import com.voltweg.ui.theme.VoltwegSecondaryFixed
import com.voltweg.ui.theme.VoltwegSurfaceContainer
import com.voltweg.ui.theme.VoltwegSurfaceContainerHigh

@Composable
fun VoltwegMapCanvas(
    stations: List<ChargingStation>,
    selectedStationId: String?,
    onStationSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(VoltwegSurfaceContainer)
            .testTag("voltweg_map_canvas")
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Drawn vector map canvas simulating Berlin street layout
        Canvas(modifier = Modifier.fillMaxSize()) {
            val riverColor = Color(0xFFC5E0DC)
            val majorRoadColor = Color(0xFFE2E2E2)
            val secondaryRoadColor = Color(0xFFEEEEED)

            // Draw Spree river path
            val riverPath = Path().apply {
                moveTo(0f, heightPx * 0.30f)
                cubicTo(
                    widthPx * 0.3f, heightPx * 0.28f,
                    widthPx * 0.5f, heightPx * 0.45f,
                    widthPx, heightPx * 0.38f
                )
            }
            drawPath(
                path = riverPath,
                color = riverColor,
                style = Stroke(width = 18.dp.toPx())
            )

            // Major arterial roads
            drawLine(
                color = majorRoadColor,
                start = Offset(0f, heightPx * 0.15f),
                end = Offset(widthPx, heightPx * 0.85f),
                strokeWidth = 10.dp.toPx()
            )
            drawLine(
                color = majorRoadColor,
                start = Offset(widthPx * 0.1f, heightPx),
                end = Offset(widthPx * 0.9f, 0f),
                strokeWidth = 8.dp.toPx()
            )
            drawLine(
                color = majorRoadColor,
                start = Offset(0f, heightPx * 0.5f),
                end = Offset(widthPx, heightPx * 0.52f),
                strokeWidth = 12.dp.toPx()
            )

            // Secondary grid lines
            val steps = 8
            for (i in 1..steps) {
                val x = widthPx * (i / (steps + 1f))
                drawLine(
                    color = secondaryRoadColor,
                    start = Offset(x, 0f),
                    end = Offset(x, heightPx),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }

        // Map Station Markers overlay
        stations.forEach { station ->
            val isSelected = station.id == selectedStationId
            val markerX = (station.mapXRatio * widthPx).dp
            val markerY = (station.mapYRatio * heightPx).dp

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.25f else 1.0f,
                label = "marker_scale"
            )

            Box(
                modifier = Modifier
                    .offset(x = markerX, y = markerY)
                    .scale(scale)
                    .testTag("map_marker_${station.id}")
            ) {
                when {
                    isSelected -> {
                        // Active Selected Marker (Dark forest green pill with bolt)
                        Surface(
                            onClick = { onStationSelected(station.id) },
                            shape = RoundedCornerShape(16.dp),
                            color = VoltwegPrimary,
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .border(2.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bolt,
                                    contentDescription = "Selected Station ${station.name}",
                                    tint = VoltwegOnPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    station.status == ChargerStatus.AVAILABLE || station.availableConnectors > 0 -> {
                        // Available Marker (Sage green circle with dark bolt)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(VoltwegSecondaryFixed)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .clickable { onStationSelected(station.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = "Available Station ${station.name}",
                                tint = VoltwegOnSecondaryFixed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    else -> {
                        // Occupied / Muted Marker (Dim gray circle with outlined bolt)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .shadow(1.dp, CircleShape)
                                .clip(CircleShape)
                                .background(VoltwegSurfaceContainerHigh)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .clickable { onStationSelected(station.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Bolt,
                                contentDescription = "Occupied Station ${station.name}",
                                tint = VoltwegOnSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
