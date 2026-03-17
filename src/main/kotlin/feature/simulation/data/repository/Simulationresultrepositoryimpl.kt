package com.oussama_chatri.feature.simulation.data.repository

import com.oussama_chatri.core.util.withIO
import com.oussama_chatri.feature.simulation.data.local.SimulationProtoStore
import com.oussama_chatri.feature.simulation.data.mapper.SimulationResultMapper
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.simulation.domain.repository.SimulationResultRepository

class SimulationResultRepositoryImpl(
    private val store: SimulationProtoStore
) : SimulationResultRepository {

    override suspend fun save(result: SimulationResult) = withIO {
        store.write(SimulationResultMapper.toProto(result))
    }

    override suspend fun loadLatest(wellId: String): SimulationResult? = withIO {
        store.readLatest(wellId)?.let { SimulationResultMapper.toDomain(it) }
    }

    override suspend fun loadAll(wellId: String): List<SimulationResult> = withIO {
        store.readAll(wellId).map { SimulationResultMapper.toDomain(it) }
    }

    override suspend fun deleteAll(wellId: String) = withIO {
        store.deleteAll(wellId)
    }
}