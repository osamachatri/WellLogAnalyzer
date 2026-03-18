package com.oussama_chatri.feature.dashboard.di

import com.oussama_chatri.feature.dashboard.data.local.ProjectProtoStore
import com.oussama_chatri.feature.dashboard.data.repository.ProjectRepositoryImpl
import com.oussama_chatri.feature.dashboard.domain.repository.ProjectRepository
import com.oussama_chatri.feature.dashboard.domain.usecase.DeleteProjectUseCase
import com.oussama_chatri.feature.dashboard.domain.usecase.GetRecentProjectsUseCase
import com.oussama_chatri.feature.dashboard.domain.usecase.SaveProjectSummaryUseCase
import com.oussama_chatri.feature.dashboard.presentation.viewmodel.DashboardViewModel
import org.koin.dsl.module

val DashboardModule = module {

    single { ProjectProtoStore() }

    single<ProjectRepository> { ProjectRepositoryImpl(store = get()) }

    factory { GetRecentProjectsUseCase(repository = get()) }
    factory { SaveProjectSummaryUseCase(repository = get()) }
    factory { DeleteProjectUseCase(repository = get()) }

    factory {
        DashboardViewModel(
            getRecentProjects = get(),
            deleteProject = get()
        )
    }
}