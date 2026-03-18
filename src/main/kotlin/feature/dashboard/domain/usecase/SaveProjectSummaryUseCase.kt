package com.oussama_chatri.feature.dashboard.domain.usecase

import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import com.oussama_chatri.feature.dashboard.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

/**
 * Persists a [ProjectSummary] (create or update).
 */
class SaveProjectSummaryUseCase(private val repository: ProjectRepository) {
    suspend fun execute(summary: ProjectSummary) = repository.saveProject(summary)
}