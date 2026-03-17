package com.oussama_chatri.feature.simulation.presentation.viewmodel

import com.oussama_chatri.core.base.BaseViewModel
import com.oussama_chatri.core.util.launchDefault
import com.oussama_chatri.feature.simulation.domain.model.PressurePoint
import com.oussama_chatri.feature.simulation.domain.model.SimulationInput
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.simulation.domain.model.SimulationStatus
import com.oussama_chatri.feature.simulation.domain.usecase.GetSimulationHistoryUseCase
import com.oussama_chatri.feature.simulation.domain.usecase.RunSimulationUseCase
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SimulationViewModel(
    private val runSimulationUseCase: RunSimulationUseCase,
    private val getSimulationHistoryUseCase: GetSimulationHistoryUseCase
) : BaseViewModel() {

    private val _status = MutableStateFlow<SimulationStatus>(SimulationStatus.Idle)
    val status: StateFlow<SimulationStatus> = _status.asStateFlow()

    private val _logLines = MutableStateFlow<List<String>>(emptyList())
    val logLines: StateFlow<List<String>> = _logLines.asStateFlow()

    // Partial profile collected while the flow is running — drives the live mini chart
    private val _liveProfile = MutableStateFlow<List<PressurePoint>>(emptyList())
    val liveProfile: StateFlow<List<PressurePoint>> = _liveProfile.asStateFlow()

    // Depth step override from the control panel
    private val _depthStep = MutableStateFlow(100.0)
    val depthStep: StateFlow<Double> = _depthStep.asStateFlow()

    // Flow rate override (null = use profile value)
    private val _flowRateOverride = MutableStateFlow<Double?>(null)
    val flowRateOverride: StateFlow<Double?> = _flowRateOverride.asStateFlow()

    // The WellProfile loaded into this session
    private val _activeProfile = MutableStateFlow<WellProfile?>(null)
    val activeProfile: StateFlow<WellProfile?> = _activeProfile.asStateFlow()

    private var simulationJob: Job? = null

    fun loadProfile(profile: WellProfile) {
        _activeProfile.value = profile
        reset()
    }

    fun updateDepthStep(value: String) {
        _depthStep.value = value.toDoubleOrNull()?.coerceIn(10.0, 500.0) ?: 100.0
    }

    fun updateFlowRateOverride(value: String) {
        _flowRateOverride.value = value.toDoubleOrNull()
    }

    fun runSimulation() {
        val profile = _activeProfile.value ?: run {
            appendLog("ERROR: No well profile loaded.")
            return
        }

        if (_status.value is SimulationStatus.Running) return

        val input = buildInput(profile)
        val collectedPoints = mutableListOf<PressurePoint>()

        simulationJob = viewModelScope.launchDefault {
            _liveProfile.value = emptyList()
            _status.value = SimulationStatus.Running(0.0, input.totalDepth)
            appendLog("Initializing engine...")
            appendLog("Loading well profile ${profile.wellName}")

            runSimulationUseCase.execute(input)
                .catch { e ->
                    appendLog("FAILED: ${e.message}")
                    _status.value = SimulationStatus.Failed(e.message ?: "Unknown error", e)
                }
                .collect { point ->
                    collectedPoints.add(point)
                    _liveProfile.value = collectedPoints.toList()
                    _status.value = SimulationStatus.Running(point.depth, input.totalDepth)

                    val ecdStatus = if (point.isEcdSafe) "✓" else "⚠"
                    appendLog(
                        "ECD at ${point.depth.toInt()} ft: " +
                                "${String.format("%.2f", point.ecd)} ppg $ecdStatus"
                    )
                }

            if (_status.value !is SimulationStatus.Failed) {
                appendLog("Assembling results...")
                val result = runSimulationUseCase.buildAndSave(input, collectedPoints)
                _status.value = SimulationStatus.Done(result)
                appendLog("Simulation complete — Max ECD: ${String.format("%.2f", result.maxEcd)} ppg")
            }
        }
    }

    fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
        if (_status.value is SimulationStatus.Running) {
            _status.value = SimulationStatus.Idle
            appendLog("Simulation stopped by user.")
        }
    }

    fun reset() {
        stopSimulation()
        _status.value     = SimulationStatus.Idle
        _liveProfile.value = emptyList()
        _logLines.value    = emptyList()
    }

    private fun buildInput(profile: WellProfile): SimulationInput = SimulationInput(
        wellId             = profile.id,
        wellName           = profile.wellName,
        totalDepth         = profile.totalDepth,
        casingOd           = profile.casingOd,
        casingId           = profile.casingId,
        drillString        = profile.drillString,
        bitParameters      = profile.bitParameters,
        fluidProperties    = profile.fluidProperties,
        formationZones     = profile.formationZones,
        deviationSurvey    = profile.deviationSurvey,
        depthStep          = _depthStep.value,
        flowRateOverride   = _flowRateOverride.value
    )

    private fun appendLog(message: String) {
        val timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        _logLines.value = _logLines.value + "[$timestamp] $message"
    }
}