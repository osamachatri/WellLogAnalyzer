package com.oussama_chatri.feature.simulation.domain.engine

import com.oussama_chatri.feature.simulation.domain.model.PressurePoint
import com.oussama_chatri.feature.simulation.domain.model.SimulationInput
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import kotlinx.coroutines.flow.Flow

/**
 * Contract for the hydraulics simulation engine.
 *
 * [run] is the main entry point. It returns a [Flow] that emits one [PressurePoint]
 * per depth step so the UI can display a live progress bar and partial results.
 * After the flow completes, call [buildResult] to assemble the full [SimulationResult].
 */
interface HydraulicsEngine {

    /**
     * Runs the depth-step simulation and emits a [PressurePoint] at each step.
     * The flow is cold — it starts when collected.
     */
    fun run(input: SimulationInput): Flow<PressurePoint>

    /**
     * Assembles the final [SimulationResult] from the completed profile.
     * Must be called after [run]'s flow has been fully collected.
     */
    fun buildResult(
        input: SimulationInput,
        pressureProfile: List<PressurePoint>
    ): SimulationResult
}