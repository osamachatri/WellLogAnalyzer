package com.oussama_chatri.feature.simulation.di

import com.oussama_chatri.feature.simulation.data.engine.HydraulicsEngineImpl
import com.oussama_chatri.feature.simulation.data.local.SimulationProtoStore
import com.oussama_chatri.feature.simulation.data.repository.SimulationResultRepositoryImpl
import com.oussama_chatri.feature.simulation.domain.engine.HydraulicsEngine
import com.oussama_chatri.feature.simulation.domain.repository.SimulationResultRepository
import com.oussama_chatri.feature.simulation.domain.usecase.GetSimulationHistoryUseCase
import com.oussama_chatri.feature.simulation.domain.usecase.RunSimulationUseCase
import com.oussama_chatri.feature.simulation.presentation.viewmodel.SimulationViewModel
import org.koin.dsl.module

val SimulationModule = module {

    // Data layer
    single { SimulationProtoStore() }

    single<SimulationResultRepository> {
        SimulationResultRepositoryImpl(store = get())
    }

    single<HydraulicsEngine> { HydraulicsEngineImpl() }

    // Domain use cases
    factory { RunSimulationUseCase(engine = get(), repository = get()) }
    factory { GetSimulationHistoryUseCase(repository = get()) }

    // Presentation
    factory {
        SimulationViewModel(
            runSimulationUseCase       = get(),
            getSimulationHistoryUseCase = get()
        )
    }
}