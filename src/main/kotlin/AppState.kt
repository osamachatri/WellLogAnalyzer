package com.oussama_chatri

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.oussama_chatri.core.navigation.Route
import com.oussama_chatri.core.theme.AppThemeId
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import com.oussama_chatri.feature.dashboard.domain.usecase.SaveProjectSummaryUseCase
import com.oussama_chatri.feature.settings.data.local.SettingsProtoStore
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppState : KoinComponent {

    private val settingsStore: SettingsProtoStore by inject()

    // Loaded from disk — this drives AppTheme in App.kt
    var currentThemeId: AppThemeId by mutableStateOf(settingsStore.read().themeId)
        private set

    var currentRoute: Route by mutableStateOf(Route.Dashboard)
        private set

    var activeProfile: WellProfile? by mutableStateOf(null)
        private set

    var lastSimulationResult: SimulationResult? by mutableStateOf(null)
        private set

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val saveProject: SaveProjectSummaryUseCase by inject()

    fun setTheme(themeId: AppThemeId) {
        currentThemeId = themeId
    }

    fun navigate(route: Route) {
        currentRoute = route
    }

    fun navigateToSimulation(profile: WellProfile) {
        activeProfile = profile
        currentRoute  = Route.Simulation
        scope.launch { saveProject.execute(profile.toSummary()) }
    }

    fun onSimulationComplete(result: SimulationResult) {
        lastSimulationResult = result
        val profile = activeProfile ?: return
        scope.launch {
            saveProject.execute(
                profile.toSummary().copy(
                    lastRunTimestamp = System.currentTimeMillis(),
                    maxEcd           = result.maxEcd,
                    isEcdSafe        = result.isEcdSafe
                )
            )
        }
    }

    fun onExportComplete(format: String) {
        val profile = activeProfile ?: return
        scope.launch {
            saveProject.execute(
                profile.toSummary().copy(
                    lastExportTimestamp = System.currentTimeMillis(),
                    lastExportFormat    = format
                )
            )
        }
    }
}

private fun WellProfile.toSummary() = ProjectSummary(
    id                  = id,
    wellName            = wellName,
    totalDepth          = totalDepth,
    simulationCount     = 0,
    lastRunTimestamp    = null,
    lastExportTimestamp = null,
    lastExportFormat    = null,
    maxEcd              = null,
    isEcdSafe           = null,
    createdAt           = lastModified
)