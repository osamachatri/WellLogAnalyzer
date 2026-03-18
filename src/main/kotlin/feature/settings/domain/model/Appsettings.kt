package com.oussama_chatri.feature.settings.domain.model

import com.oussama_chatri.core.theme.AppThemeId

enum class UnitSystem(val displayName: String) {
    API   ("API (ppg / ft / psi)"),
    METRIC("Metric (kg/m³ / m / kPa)"),
    MIXED ("Mixed")
}

data class AppSettings(
    val themeId:          AppThemeId = AppThemeId.PETROLEUM_DARK,
    val unitSystem:       UnitSystem = UnitSystem.API,
    val decimalPrecision: Int        = 2,
    val defaultExportPath:String     = "",
    val defaultProjectPath:String    = "",
    val autoSaveIntervalMinutes: Int = 0
)