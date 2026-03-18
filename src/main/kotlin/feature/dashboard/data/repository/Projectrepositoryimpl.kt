package com.oussama_chatri.feature.dashboard.data.repository

import com.oussama_chatri.feature.dashboard.data.local.ProjectListProto
import com.oussama_chatri.feature.dashboard.data.local.ProjectProtoStore
import com.oussama_chatri.feature.dashboard.data.mapper.ProjectMapper
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import com.oussama_chatri.feature.dashboard.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepositoryImpl(
    private val store: ProjectProtoStore
) : ProjectRepository {

    override fun observeProjects(): Flow<List<ProjectSummary>> =
        store.observe().map { proto ->
            proto.projects
                .map(ProjectMapper::toDomain)
                .sortedByDescending { it.lastRunTimestamp ?: it.createdAt }
        }

    override suspend fun getProjects(): List<ProjectSummary> =
        store.read().projects
            .map(ProjectMapper::toDomain)
            .sortedByDescending { it.lastRunTimestamp ?: it.createdAt }

    override suspend fun saveProject(summary: ProjectSummary) {
        val current = store.read().projects.toMutableList()
        val idx = current.indexOfFirst { it.id == summary.id }
        val proto = ProjectMapper.toProto(summary)
        if (idx >= 0) current[idx] = proto else current.add(proto)
        store.write(ProjectListProto(projects = current))
    }

    override suspend fun deleteProject(id: String) {
        val updated = store.read().projects.filter { it.id != id }
        store.write(ProjectListProto(projects = updated))
    }
}