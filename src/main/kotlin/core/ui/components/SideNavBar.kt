package com.oussama_chatri.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.oussama_chatri.core.theme.AmberDim
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.NavyDeep
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary

private val SidebarWidth = 240.dp
private val NavItemHeight = 44.dp

@Composable
fun SideNavBar(
    currentRoute: Route,
    onRouteSelected: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(SidebarWidth)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
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
                        .background(AmberGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "⛽",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text  = "WellLog",
                        style = MaterialTheme.typography.titleMedium,
                        color = AmberGold
                    )
                    Text(
                        text  = "Analyzer",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp)
                    .background(DividerColor)
            )

            Spacer(Modifier.height(8.dp))

            mainNavRoutes.forEach { route ->
                NavItem(
                    route       = route,
                    isSelected  = currentRoute == route,
                    onClick     = { onRouteSelected(route) }
                )
            }
        }

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(1.dp)
                    .background(DividerColor)
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
    route: Route,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) AmberDim else Color.Transparent
    val iconTint        = if (isSelected) AmberGold else TextMuted
    val textColor       = if (isSelected) AmberGold else TextSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(NavItemHeight)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isSelected) AmberGold else Color.Transparent)
        )

        Spacer(Modifier.width(10.dp))

        Icon(
            imageVector        = route.icon,
            contentDescription = route.label,
            tint               = iconTint,
            modifier           = Modifier.size(18.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text  = route.label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}