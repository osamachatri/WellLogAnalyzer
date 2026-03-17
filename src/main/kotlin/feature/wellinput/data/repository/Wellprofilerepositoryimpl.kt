package com.oussama_chatri.feature.wellinput.data.repository

import com.oussama_chatri.core.util.withIO
import com.oussama_chatri.feature.wellinput.data.local.WellProfileProtoStore
import com.oussama_chatri.feature.wellinput.data.mapper.WellProfileMapper
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import com.oussama_chatri.feature.wellinput.domain.repository.WellProfileRepository

/**
 * Concrete implementation of [WellProfileRepository].
 * Delegates file I/O to [WellProfileProtoStore] and mapping to [WellProfileMapper].
 * All operations are dispatched on Dispatchers.IO via [withIO].
 */
class WellProfileRepositoryImpl(
    private val store: WellProfileProtoStore
) : WellProfileRepository {

    override suspend fun save(profile: WellProfile) = withIO {
        val proto = WellProfileMapper.toProto(profile)
        store.write(proto)
    }

    override suspend fun load(id: String): WellProfile? = withIO {
        store.read(id)?.let { WellProfileMapper.toDomain(it) }
    }

    override suspend fun loadAll(): List<WellProfile> = withIO {
        store.readAll()
            .map { WellProfileMapper.toDomain(it) }
            .sortedByDescending { it.lastModified }
    }

    override suspend fun delete(id: String) = withIO {
        store.delete(id)
    }
}