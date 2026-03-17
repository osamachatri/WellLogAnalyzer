package com.oussama_chatri.feature.simulation.domain.usecase

import com.oussama_chatri.feature.simulation.domain.engine.HydraulicsEngine
import com.oussama_chatri.feature.simulation.domain.model.PressurePoint
import com.oussama_chatri.feature.simulation.domain.model.SimulationInput
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.simulation.domain.repository.SimulationResultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

/**
 * Orchestrates a full simulation run.
 *
 * Returns the engine's [Flow<PressurePoint>] unmodified so the caller
 * can observe live depth progress. After fully collecting the flow,
 * call [buildAndSave] to assemble and persist the [SimulationResult].
 */
class RunSimulationUseCase(
    private val engine: HydraulicsEngine,
    private val repository: SimulationResultRepository
) {
    /** Returns the live pressure-point stream from the solver. */
    fun execute(input: SimulationInput): Flow<PressurePoint> = engine.run(input)

    /**
     * Assembles the final [SimulationResult] from collected points and saves it to disk.
     * Call this once the flow returned by [execute] has completed.
     */
    suspend fun buildAndSave(
        input: SimulationInput,
        pressureProfile: List<PressurePoint>
    ): SimulationResult {
        val result = engine.buildResult(input, pressureProfile)
        repository.save(result)
        return result
    }
}