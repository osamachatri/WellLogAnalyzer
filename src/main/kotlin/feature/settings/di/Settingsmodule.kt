package com.oussama_chatri.feature.settings.di

import com.oussama_chatri.feature.settings.data.local.SettingsProtoStore
import com.oussama_chatri.feature.settings.domain.usecase.LoadSettingsUseCase
import com.oussama_chatri.feature.settings.domain.usecase.SaveSettingsUseCase
import com.oussama_chatri.feature.settings.presentation.viewmodel.SettingsViewModel
import org.koin.dsl.module

val SettingsModule = module {
    single  { SettingsProtoStore() }
    factory { LoadSettingsUseCase(store = get()) }
    factory { SaveSettingsUseCase(store = get()) }
    factory { SettingsViewModel(loadSettings = get(), saveSettings = get()) }
}