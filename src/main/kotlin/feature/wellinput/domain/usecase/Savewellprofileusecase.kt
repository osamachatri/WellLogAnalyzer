package com.oussama_chatri.feature.wellinput.domain.usecase

import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import com.oussama_chatri.feature.wellinput.domain.repository.WellProfileRepository

/**
 * Saves a [WellProfile] to persistent storage.
 *
 * Stamps [WellProfile.lastModified] with the current system time before delegating
 * to the repository so callers never need to manage timestamps manually.
 */
class SaveWellProfileUseCase(
    private val repository: WellProfileRepository
) {
    suspend operator fun invoke(profile: WellProfile) {
        val stamped = profile.copy(lastModified = System.currentTimeMillis())
        repository.save(stamped)
    }
}