package com.oussama_chatri.feature.wellinput.domain.repository

import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

interface WellProfileRepository {

    /**
     * Persist [profile] to local storage.
     * If a profile with the same [WellProfile.id] already exists it is overwritten.
     */
    suspend fun save(profile: WellProfile)

    suspend fun load(id: String): WellProfile?

    suspend fun loadAll(): List<WellProfile>

    /**
     * Permanently delete the profile with [id].
     * No-ops silently if the id does not exist.
     */
    suspend fun delete(id: String)
}