package com.voltweg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.voltweg.ui.VoltwegTab
import com.voltweg.ui.theme.VoltwegOnSecondaryContainer
import com.voltweg.ui.theme.VoltwegSecondaryContainer
import com.voltweg.ui.theme.VoltwegSurfaceContainer

@Composable
fun VoltwegBottomBar(
    currentTab: VoltwegTab,
    onTabSelected: (VoltwegTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(VoltwegSurfaceContainer)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("voltweg_bottom_navigation_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VoltwegNavItem(
                tab = VoltwegTab.EXPLORE,
                selected = currentTab == VoltwegTab.EXPLORE,
                selectedIcon = Icons.Filled.Explore,
                unselectedIcon = Icons.Outlined.Explore,
                onTabSelected = onTabSelected
            )
            VoltwegNavItem(
                tab = VoltwegTab.MAP,
                selected = currentTab == VoltwegTab.MAP,
                selectedIcon = Icons.Filled.Map,
                unselectedIcon = Icons.Outlined.Map,
                onTabSelected = onTabSelected
            )
            VoltwegNavItem(
                tab = VoltwegTab.FAVORITES,
                selected = currentTab == VoltwegTab.FAVORITES,
                selectedIcon = Icons.Filled.Favorite,
                unselectedIcon = Icons.Outlined.FavoriteBorder,
                onTabSelected = onTabSelected
            )
            VoltwegNavItem(
                tab = VoltwegTab.SETTINGS,
                selected = currentTab == VoltwegTab.SETTINGS,
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
private fun VoltwegNavItem(
    tab: VoltwegTab,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onTabSelected: (VoltwegTab) -> Unit
) {
    val bgModifier = if (selected) {
        Modifier
            .clip(CircleShape)
            .background(VoltwegSecondaryContainer)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    } else {
        Modifier
            .clip(CircleShape)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    }

    Column(
        modifier = Modifier
            .clickable { onTabSelected(tab) }
            .then(bgModifier)
            .testTag("nav_item_${tab.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = tab.title,
            tint = if (selected) VoltwegOnSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = tab.title,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) VoltwegOnSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
