package com.oussama_chatri.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.oussama_chatri.AppState
import com.oussama_chatri.feature.simulation.presentation.SimulationScreen
import com.oussama_chatri.feature.wellinput.presentation.screen.WellInputScreen

@Composable
fun AppNavigation(appState: AppState) {
    when (appState.currentRoute) {
        Route.Dashboard  -> ScreenPlaceholder("Dashboard")
        Route.WellInput  -> WellInputScreen(
            // When "Run Simulation →" is pressed in the validation sidebar,
            // we save the current profile into AppState and switch screens
            onNavigateToSimulation = { profile ->
                appState.navigateToSimulation(profile)
            },
            modifier = Modifier.fillMaxSize()
        )
        Route.Simulation -> SimulationScreen(
            // Hand the active profile straight to the simulation screen so
            // the ViewModel can load it without any extra user interaction
            profileToLoad = appState.activeProfile,
            modifier      = Modifier.fillMaxSize()
        )
        Route.Charts2D   -> ScreenPlaceholder("2D Charts")
        Route.Viewer3D   -> ScreenPlaceholder("3D Viewer")
        Route.Reports    -> ScreenPlaceholder("Reports")
        Route.Settings   -> ScreenPlaceholder("Settings")
    }
}

@Composable
private fun ScreenPlaceholder(name: String) {
    Box(
        modifier          = Modifier.fillMaxSize(),
        contentAlignment  = Alignment.Center
    ) {
        Text(
            text  = "$name — coming soon",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}