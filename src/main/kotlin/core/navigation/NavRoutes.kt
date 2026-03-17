package com.oussama_chatri.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val pinnedToBottom: Boolean = false
) {
    data object Dashboard : Route(
        route = "dashboard",
        label = "Dashboard",
        icon = Icons.Default.Dashboard
    )

    data object WellInput : Route(
        route = "well_input",
        label = "Well Input",
        icon = Icons.Default.Edit
    )

    data object Simulation : Route(
        route = "simulation",
        label = "Simulation",
        icon = Icons.Default.PlayCircle
    )

    data object Charts2D : Route(
        route = "charts_2d",
        label = "2D Charts",
        icon = Icons.Default.Analytics
    )

    data object Viewer3D : Route(
        route = "viewer_3d",
        label = "3D Viewer",
        icon = Icons.Default.Animation
    )

    data object Reports : Route(
        route = "reports",
        label = "Reports",
        icon = Icons.Default.Assessment
    )

    data object Settings : Route(
        route = "settings",
        label = "Settings",
        icon = Icons.Default.Settings,
        pinnedToBottom = true
    )
}

val allRoutes: List<Route> = listOf(
    Route.Dashboard,
    Route.WellInput,
    Route.Simulation,
    Route.Charts2D,
    Route.Viewer3D,
    Route.Reports,
    Route.Settings
)

val mainNavRoutes: List<Route> = allRoutes.filter { !it.pinnedToBottom }

val bottomNavRoutes: List<Route> = allRoutes.filter { it.pinnedToBottom }