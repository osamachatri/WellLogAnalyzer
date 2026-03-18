package com.oussama_chatri.feature.charts2d.di

import com.oussama_chatri.feature.charts2d.domain.usecase.BuildChartDataUseCase
import com.oussama_chatri.feature.charts2d.presentation.viewmodel.Charts2DViewModel
import org.koin.dsl.module

val Charts2DModule = module {

    factory { BuildChartDataUseCase() }

    factory { Charts2DViewModel() }
}