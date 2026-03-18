package com.oussama_chatri.feature.dashboard.presentation.viewmodel

import com.oussama_chatri.core.base.BaseViewModel
import com.oussama_chatri.core.util.launchIO
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import com.oussama_chatri.feature.dashboard.domain.usecase.DeleteProjectUseCase
import com.oussama_chatri.feature.dashboard.domain.usecase.GetRecentProjectsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DashboardViewModel(
    private val getRecentProjects: GetRecentProjectsUseCase,
    private val deleteProject: DeleteProjectUseCase
) : BaseViewModel() {

    private val _projects = MutableStateFlow<List<ProjectSummary>>(emptyList())
    val projects: StateFlow<List<ProjectSummary>> = _projects.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _confirmDeleteId = MutableStateFlow<String?>(null)
    val confirmDeleteId: StateFlow<String?> = _confirmDeleteId.asStateFlow()

    init {
        observeProjects()
    }

    private fun observeProjects() {
        getRecentProjects.execute()
            .onEach { list ->
                _projects.value = list
                _isLoading.value = false
            }
            .catch { e ->
                logger.error("Failed to observe projects", e)
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    fun requestDelete(id: String) {
        _confirmDeleteId.value = id
    }

    fun cancelDelete() {
        _confirmDeleteId.value = null
    }

    fun confirmDelete() {
        val id = _confirmDeleteId.value ?: return
        _confirmDeleteId.value = null
        viewModelScope.launchIO {
            deleteProject.execute(id)
        }
    }
}