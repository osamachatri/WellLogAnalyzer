package com.oussama_chatri.feature.simulation.domain.usecase

import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.simulation.domain.repository.SimulationResultRepository

/**
 * Retrieves past simulation results for a given well.
 * Used by the Dashboard to show last-run date and by Reports to prefill data.
 */
class GetSimulationHistoryUseCase(
    private val repository: SimulationResultRepository
) {
    suspend fun latest(wellId: String): SimulationResult? = repository.loadLatest(wellId)

    suspend fun all(wellId: String): List<SimulationResult> = repository.loadAll(wellId)
}