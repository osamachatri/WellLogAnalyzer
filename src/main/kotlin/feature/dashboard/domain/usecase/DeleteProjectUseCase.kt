package com.oussama_chatri.feature.dashboard.domain.usecase

import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import com.oussama_chatri.feature.dashboard.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

/**
 * Deletes a project by [id].
 */
class DeleteProjectUseCase(private val repository: ProjectRepository) {
    suspend fun execute(id: String) = repository.deleteProject(id)
}