package com.oussama_chatri.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.navigation.Route
import com.oussama_chatri.core.navigation.bottomNavRoutes
import com.oussama_chatri.core.navigation.mainNavRoutes
import com.oussama_chatri.core.theme.WellLogTheme

private val SidebarWidth  = 240.dp
private val NavItemHeight = 44.dp

@Composable
fun SideNavBar(
    currentRoute:    Route,
    onRouteSelected: (Route) -> Unit,
    modifier:        Modifier = Modifier
) {
    val c = WellLogTheme.colors

    Column(
        modifier = modifier
            .width(SidebarWidth)
            .fillMaxHeight()
            .background(c.sidebarBg)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Logo header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(c.accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⛽", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("WellLog",  style = MaterialTheme.typography.titleMedium, color = c.accent)
                    Text("Analyzer", style = MaterialTheme.typography.labelSmall,  color = c.textMuted)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp)
                    .background(c.divider)
            )
            Spacer(Modifier.height(8.dp))

            mainNavRoutes.forEach { route ->
                NavItem(
                    route      = route,
                    isSelected = currentRoute == route,
                    onClick    = { onRouteSelected(route) }
                )
            }
        }

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp)
                    .background(c.divider)
            )
            Spacer(Modifier.height(4.dp))
            bottomNavRoutes.forEach { route ->
                NavItem(
                    route      = route,
                    isSelected = currentRoute == route,
                    onClick    = { onRouteSelected(route) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    route:      Route,
    isSelected: Boolean,
    onClick:    () -> Unit
) {
    val c = WellLogTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(NavItemHeight)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) c.accentDim else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isSelected) c.accent else Color.Transparent)
        )
        Spacer(Modifier.width(10.dp))
        Icon(
            imageVector        = route.icon,
            contentDescription = route.label,
            tint               = if (isSelected) c.accent else c.textMuted,
            modifier           = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text  = route.label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) c.accent else c.textSecondary
        )
    }
}