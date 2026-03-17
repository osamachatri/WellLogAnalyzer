package com.oussama_chatri.feature.wellinput.domain.usecase

import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import com.oussama_chatri.feature.wellinput.domain.repository.WellProfileRepository

/**
 * Loads a [WellProfile] by its unique identifier.
 *
 * Returns null if the profile does not exist in storage, allowing the caller
 * to decide whether to create a new empty profile or show an error.
 */
class LoadWellProfileUseCase(
    private val repository: WellProfileRepository
) {
    suspend operator fun invoke(id: String): WellProfile? {
        return repository.load(id)
    }
}