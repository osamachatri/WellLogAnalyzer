package com.oussama_chatri.feature.settings.domain.usecase

import com.oussama_chatri.feature.settings.domain.model.AppSettings

class LoadSettingsUseCase(private val store: com.oussama_chatri.feature.settings.data.local.SettingsProtoStore) {
    fun execute(): AppSettings = store.read()
}

class SaveSettingsUseCase(private val store: com.oussama_chatri.feature.settings.data.local.SettingsProtoStore) {
    fun execute(settings: AppSettings) = store.write(settings)
}