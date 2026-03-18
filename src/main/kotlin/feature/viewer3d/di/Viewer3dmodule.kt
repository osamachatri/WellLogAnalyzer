package com.oussama_chatri.feature.viewer3d.di

import com.oussama_chatri.feature.viewer3d.domain.usecase.BuildGeologyModelUseCase
import com.oussama_chatri.feature.viewer3d.domain.usecase.ComputeWellTrajectoryUseCase
import com.oussama_chatri.feature.viewer3d.presentation.viewmodel.Viewer3DViewModel
import org.koin.dsl.module

val Viewer3DModule = module {

    factory { ComputeWellTrajectoryUseCase() }
    factory { BuildGeologyModelUseCase() }

    factory {
        Viewer3DViewModel(
            computeTrajectory = get(),
            buildGeology = get()
        )
    }
}