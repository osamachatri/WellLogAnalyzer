package com.oussama_chatri.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oussama_chatri.feature.wellinput.presentation.WellInputScreen

@Composable
fun AppNavigation(currentRoute: Route) {
    when (currentRoute) {
        Route.Dashboard   -> DashboardScreenPlaceholder()
        Route.WellInput   -> WellInputScreen(modifier = Modifier.fillMaxSize())
        Route.Simulation  -> SimulationScreenPlaceholder()
        Route.Charts2D    -> Charts2DScreenPlaceholder()
        Route.Viewer3D    -> Viewer3DScreenPlaceholder()
        Route.Reports     -> ReportsScreenPlaceholder()
        Route.Settings    -> SettingsScreenPlaceholder()
    }
}

@Composable
private fun DashboardScreenPlaceholder()  = ScreenPlaceholder("Dashboard")

@Composable
private fun SimulationScreenPlaceholder() = ScreenPlaceholder("Simulation")

@Composable
private fun Charts2DScreenPlaceholder()   = ScreenPlaceholder("2D Charts")

@Composable
private fun Viewer3DScreenPlaceholder()   = ScreenPlaceholder("3D Viewer")

@Composable
private fun ReportsScreenPlaceholder()    = ScreenPlaceholder("Reports")

@Composable
private fun SettingsScreenPlaceholder()   = ScreenPlaceholder("Settings")

@Composable
private fun ScreenPlaceholder(name: String) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text  = "$name — coming soon",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}