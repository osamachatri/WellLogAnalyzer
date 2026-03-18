package com.oussama_chatri.feature.dashboard.domain.repository

import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    /** Emits the full list whenever it changes on disk. */
    fun observeProjects(): Flow<List<ProjectSummary>>

    suspend fun getProjects(): List<ProjectSummary>

    suspend fun saveProject(summary: ProjectSummary)

    suspend fun deleteProject(id: String)
}