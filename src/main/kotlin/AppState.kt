package com.oussama_chatri

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.oussama_chatri.core.navigation.Route
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import com.oussama_chatri.feature.dashboard.domain.usecase.SaveProjectSummaryUseCase
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppState : KoinComponent {

    var currentRoute: Route by mutableStateOf(Route.Dashboard)
        private set

    var isDarkTheme: Boolean by mutableStateOf(true)
        private set

    // ── Active session data ───────────────────────────────────────────────
    var activeProfile: WellProfile? by mutableStateOf(null)
        private set

    var lastSimulationResult: SimulationResult? by mutableStateOf(null)
        private set

    // ── Background scope for fire-and-forget persistence ─────────────────
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val saveProject: SaveProjectSummaryUseCase by inject()

    // ── Navigation actions ────────────────────────────────────────────────
    fun navigate(route: Route) {
        currentRoute = route
    }

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

    fun navigateToSimulation(profile: WellProfile) {
        activeProfile = profile
        currentRoute  = Route.Simulation

        // Ensure this profile has an entry on the dashboard
        scope.launch { saveProject.execute(profile.toSummary()) }
    }

    // Called by SimulationScreen once a run completes successfully
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

    // Called by ReportViewModel after a successful export
    fun onExportComplete(format: String) {
        val profile = activeProfile ?: return
        scope.launch {
            val current = profile.toSummary()
            saveProject.execute(
                current.copy(
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