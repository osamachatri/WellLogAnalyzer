package com.oussama_chatri.feature.settings.presentation.viewmodel

import com.oussama_chatri.core.base.BaseViewModel
import com.oussama_chatri.core.theme.AppThemeId
import com.oussama_chatri.feature.settings.domain.model.AppSettings
import com.oussama_chatri.feature.settings.domain.model.UnitSystem
import com.oussama_chatri.feature.settings.domain.usecase.LoadSettingsUseCase
import com.oussama_chatri.feature.settings.domain.usecase.SaveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val loadSettings: LoadSettingsUseCase,
    private val saveSettings: SaveSettingsUseCase
) : BaseViewModel() {

    private val _settings = MutableStateFlow(loadSettings.execute())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun setTheme(themeId: AppThemeId) = update { copy(themeId = themeId) }

    fun setUnitSystem(system: UnitSystem) = update { copy(unitSystem = system) }

    fun setDecimalPrecision(value: Int) = update { copy(decimalPrecision = value.coerceIn(0, 6)) }

    fun setDefaultExportPath(path: String) = update { copy(defaultExportPath = path) }

    fun setDefaultProjectPath(path: String) = update { copy(defaultProjectPath = path) }

    fun setAutoSaveInterval(minutes: Int) = update { copy(autoSaveIntervalMinutes = minutes) }

    fun resetToDefaults() = update { AppSettings() }

    private fun update(block: AppSettings.() -> AppSettings) {
        val updated = _settings.value.block()
        _settings.value = updated
        saveSettings.execute(updated)
    }
}