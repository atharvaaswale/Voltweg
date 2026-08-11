package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VoltwegPrimary,
    onPrimary = VoltwegOnPrimary,
    primaryContainer = VoltwegPrimaryContainer,
    onPrimaryContainer = VoltwegOnPrimaryContainer,
    secondary = VoltwegSecondary,
    onSecondary = VoltwegOnSecondary,
    secondaryContainer = VoltwegSecondaryContainer,
    onSecondaryContainer = VoltwegOnSecondaryContainer,
    background = VoltwegBackground,
    onBackground = VoltwegOnBackground,
    surface = VoltwegSurface,
    onSurface = VoltwegOnSurface,
    surfaceVariant = VoltwegSurfaceContainerHighest,
    onSurfaceVariant = VoltwegOnSurfaceVariant,
    outline = VoltwegOutline,
    outlineVariant = VoltwegOutlineVariant,
    error = VoltwegError,
    errorContainer = VoltwegErrorContainer,
    onErrorContainer = VoltwegOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = VoltwegPrimaryFixedDim,
    onPrimary = Color(0xFF00382D),
    primaryContainer = VoltwegPrimaryContainer,
    onPrimaryContainer = VoltwegOnPrimaryContainer,
    secondary = VoltwegSecondaryFixedDim,
    onSecondary = Color(0xFF23342E),
    secondaryContainer = Color(0xFF394A44),
    onSecondaryContainer = VoltwegSecondaryContainer,
    background = Color(0xFF101413),
    onBackground = Color(0xFFE0E3E1),
    surface = Color(0xFF101413),
    onSurface = Color(0xFFE0E3E1),
    surfaceVariant = Color(0xFF414846),
    onSurfaceVariant = Color(0xFFC1C8C4),
    outline = VoltwegOutline,
    outlineVariant = VoltwegOutlineVariant
)

@Composable
fun VoltwegTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

