package com.oussama_chatri.feature.simulation.domain.repository

import com.oussama_chatri.feature.simulation.domain.model.SimulationResult

interface SimulationResultRepository {

    /** Persist a completed [SimulationResult] to local storage. */
    suspend fun save(result: SimulationResult)

    suspend fun loadLatest(wellId: String): SimulationResult?

    suspend fun loadAll(wellId: String): List<SimulationResult>

    suspend fun deleteAll(wellId: String)
}