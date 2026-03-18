package com.oussama_chatri.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oussama_chatri.AppState
import com.oussama_chatri.feature.charts2d.presentation.screen.Charts2DScreen
import com.oussama_chatri.feature.dashboard.presentation.screen.DashboardScreen
import com.oussama_chatri.feature.reports.presentation.screen.ReportScreen
import com.oussama_chatri.feature.settings.presentation.screen.SettingsScreen
import com.oussama_chatri.feature.simulation.presentation.SimulationScreen
import com.oussama_chatri.feature.viewer3d.presentation.screen.Viewer3DScreen
import com.oussama_chatri.feature.wellinput.presentation.screen.WellInputScreen

@Composable
fun AppNavigation(appState: AppState) {
    when (appState.currentRoute) {

        Route.Dashboard -> DashboardScreen(
            onNavigateTo = appState::navigate,
            modifier     = Modifier.fillMaxSize()
        )

        Route.WellInput -> WellInputScreen(
            onNavigateToSimulation = { profile -> appState.navigateToSimulation(profile) },
            modifier               = Modifier.fillMaxSize()
        )

        Route.Simulation -> SimulationScreen(
            profileToLoad        = appState.activeProfile,
            onSimulationComplete = appState::onSimulationComplete,
            modifier             = Modifier.fillMaxSize()
        )

        Route.Charts2D -> Charts2DScreen(
            simulationResult = appState.lastSimulationResult,
            modifier         = Modifier.fillMaxSize()
        )

        Route.Viewer3D -> Viewer3DScreen(
            profile          = appState.activeProfile,
            simulationResult = appState.lastSimulationResult,
            modifier         = Modifier.fillMaxSize()
        )

        Route.Reports -> ReportScreen(
            profile  = appState.activeProfile,
            result   = appState.lastSimulationResult,
            modifier = Modifier.fillMaxSize()
        )

        Route.Settings -> SettingsScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}