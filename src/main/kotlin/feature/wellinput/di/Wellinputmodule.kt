package com.oussama_chatri.feature.wellinput.di

import com.oussama_chatri.feature.wellinput.data.local.WellProfileProtoStore
import com.oussama_chatri.feature.wellinput.data.repository.WellProfileRepositoryImpl
import com.oussama_chatri.feature.wellinput.domain.repository.WellProfileRepository
import com.oussama_chatri.feature.wellinput.domain.usecase.LoadWellProfileUseCase
import com.oussama_chatri.feature.wellinput.domain.usecase.SaveWellProfileUseCase
import com.oussama_chatri.feature.wellinput.domain.usecase.ValidateWellProfileUseCase
import com.oussama_chatri.feature.wellinput.presentation.viewmodel.WellInputViewModel
import org.koin.dsl.module

val WellInputModule = module {

    // ── Data layer ───────────────────────────────────────────────────────────
    single { WellProfileProtoStore() }

    single<WellProfileRepository> {
        WellProfileRepositoryImpl(store = get())
    }

    // ── Domain use cases ─────────────────────────────────────────────────────
    factory { SaveWellProfileUseCase(repository = get()) }
    factory { LoadWellProfileUseCase(repository = get()) }
    factory { ValidateWellProfileUseCase() }

    // ── Presentation ─────────────────────────────────────────────────────────
    factory {
        WellInputViewModel(
            saveWellProfileUseCase = get(),
            loadWellProfileUseCase = get(),
            validateWellProfileUseCase = get()
        )
    }
}