package com.oussama_chatri.feature.dashboard.domain.usecase

import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import com.oussama_chatri.feature.dashboard.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

/**
 * Emits the project list sorted by most-recently-modified first.
 */
class GetRecentProjectsUseCase(private val repository: ProjectRepository) {
    fun execute(): Flow<List<ProjectSummary>> = repository.observeProjects()
}