package com.oussama_chatri.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeId(val displayName: String) {
    PETROLEUM_DARK("Petroleum Dark"),
    OCEAN_DEEP    ("Ocean Deep"),
    SLATE_LIGHT   ("Slate Light"),
    AMBER_DAY     ("Amber Day")
}

data class WellLogColors(
    val isDark: Boolean,
    val background:    Color,
    val surface:       Color,
    val cardSurface:   Color,
    val cardElevated:  Color,
    val divider:       Color,
    val sidebarBg:     Color,
    val accent:        Color,
    val accentDark:    Color,
    val accentDim:     Color,
    val safe:          Color,
    val safeDim:       Color,
    val danger:        Color,
    val dangerDim:     Color,
    val warning:       Color,
    val textPrimary:   Color,
    val textSecondary: Color,
    val textMuted:     Color,
    val chartAmber:    Color,
    val chartTeal:     Color,
    val chartBlue:     Color,
    val chartCoral:    Color,
    val chartSafeZone: Color,
    val lithShale:     Color,
    val lithSandstone: Color,
    val lithLimestone: Color,
    val lithSalt:      Color,
    val lithTargetSand:Color,
)

val petroleumDark = WellLogColors(
    isDark        = true,
    background    = Color(0xFF0D1B2A),
    surface       = Color(0xFF1A2535),
    cardSurface   = Color(0xFF212E42),
    cardElevated  = Color(0xFF2A3A54),
    divider       = Color(0xFF2E3E58),
    sidebarBg     = Color(0xFF0D1B2A),
    accent        = Color(0xFFF4A917),
    accentDark    = Color(0xFFD4920F),
    accentDim     = Color(0x1AF4A917),
    safe          = Color(0xFF2EC4B6),
    safeDim       = Color(0x1A2EC4B6),
    danger        = Color(0xFFE63946),
    dangerDim     = Color(0x1AE63946),
    warning       = Color(0xFFF4A917),
    textPrimary   = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF94A3B8),
    textMuted     = Color(0xFF4A5568),
    chartAmber    = Color(0xFFF4A917),
    chartTeal     = Color(0xFF2EC4B6),
    chartBlue     = Color(0xFF4FC3F7),
    chartCoral    = Color(0xFFE63946),
    chartSafeZone = Color(0x1A2EC4B6),
    lithShale     = Color(0xFF607080),
    lithSandstone = Color(0xFFD4A843),
    lithLimestone = Color(0xFFD9CDB4),
    lithSalt      = Color(0xFFB0C8D8),
    lithTargetSand= Color(0xFFF4A917),
)

val oceanDeep = WellLogColors(
    isDark        = true,
    background    = Color(0xFF0A1628),
    surface       = Color(0xFF112240),
    cardSurface   = Color(0xFF172D52),
    cardElevated  = Color(0xFF1E3A66),
    divider       = Color(0xFF1E3A5F),
    sidebarBg     = Color(0xFF071020),
    accent        = Color(0xFF00D4FF),
    accentDark    = Color(0xFF00A8CC),
    accentDim     = Color(0x1A00D4FF),
    safe          = Color(0xFF00E676),
    safeDim       = Color(0x1A00E676),
    danger        = Color(0xFFFF5252),
    dangerDim     = Color(0x1AFF5252),
    warning       = Color(0xFFFFD740),
    textPrimary   = Color(0xFFE8F4FD),
    textSecondary = Color(0xFF90CAE8),
    textMuted     = Color(0xFF3D6080),
    chartAmber    = Color(0xFFFFD740),
    chartTeal     = Color(0xFF00E676),
    chartBlue     = Color(0xFF00D4FF),
    chartCoral    = Color(0xFFFF5252),
    chartSafeZone = Color(0x1A00E676),
    lithShale     = Color(0xFF4A6478),
    lithSandstone = Color(0xFFB8934A),
    lithLimestone = Color(0xFFBDB5A0),
    lithSalt      = Color(0xFF8AAEC4),
    lithTargetSand= Color(0xFFFFD740),
)

val slateLight = WellLogColors(
    isDark        = false,
    background    = Color(0xFFF5F7FA),
    surface       = Color(0xFFFFFFFF),
    cardSurface   = Color(0xFFFFFFFF),
    cardElevated  = Color(0xFFEEF2F7),
    divider       = Color(0xFFDDE3EC),
    sidebarBg     = Color(0xFFEEF2F7),
    accent        = Color(0xFFB57A10),
    accentDark    = Color(0xFF8F5E08),
    accentDim     = Color(0x1AB57A10),
    safe          = Color(0xFF1A8C82),
    safeDim       = Color(0x1A1A8C82),
    danger        = Color(0xFFB3262E),
    dangerDim     = Color(0x1AB3262E),
    warning       = Color(0xFFC87800),
    textPrimary   = Color(0xFF0D1B2A),
    textSecondary = Color(0xFF3A4A5C),
    textMuted     = Color(0xFF8A9BB0),
    chartAmber    = Color(0xFFB57A10),
    chartTeal     = Color(0xFF1A8C82),
    chartBlue     = Color(0xFF1A6EA8),
    chartCoral    = Color(0xFFB3262E),
    chartSafeZone = Color(0x1A1A8C82),
    lithShale     = Color(0xFF607080),
    lithSandstone = Color(0xFFD4A843),
    lithLimestone = Color(0xFFD9CDB4),
    lithSalt      = Color(0xFFB0C8D8),
    lithTargetSand= Color(0xFFB57A10),
)

val amberDay = WellLogColors(
    isDark        = false,
    background    = Color(0xFFFFF8EE),
    surface       = Color(0xFFFFFFFF),
    cardSurface   = Color(0xFFFFFFFF),
    cardElevated  = Color(0xFFFFF3DC),
    divider       = Color(0xFFE8D8B8),
    sidebarBg     = Color(0xFF3D2600),
    accent        = Color(0xFFF4A917),
    accentDark    = Color(0xFFD4920F),
    accentDim     = Color(0x1AF4A917),
    safe          = Color(0xFF1A8C82),
    safeDim       = Color(0x1A1A8C82),
    danger        = Color(0xFFCC2828),
    dangerDim     = Color(0x1ACC2828),
    warning       = Color(0xFFF4A917),
    textPrimary   = Color(0xFF1A0A00),
    textSecondary = Color(0xFF5C3A10),
    textMuted     = Color(0xFFAA8050),
    chartAmber    = Color(0xFFF4A917),
    chartTeal     = Color(0xFF1A8C82),
    chartBlue     = Color(0xFF1A6EA8),
    chartCoral    = Color(0xFFCC2828),
    chartSafeZone = Color(0x1A1A8C82),
    lithShale     = Color(0xFF607080),
    lithSandstone = Color(0xFFD4A843),
    lithLimestone = Color(0xFFD9CDB4),
    lithSalt      = Color(0xFFB0C8D8),
    lithTargetSand= Color(0xFFF4A917),
)

fun themeById(id: AppThemeId): WellLogColors = when (id) {
    AppThemeId.PETROLEUM_DARK -> petroleumDark
    AppThemeId.OCEAN_DEEP     -> oceanDeep
    AppThemeId.SLATE_LIGHT    -> slateLight
    AppThemeId.AMBER_DAY      -> amberDay
}

val LocalWellLogColors = staticCompositionLocalOf<WellLogColors> { petroleumDark }

object WellLogTheme {
    val colors: WellLogColors
        @androidx.compose.runtime.Composable
        get() = LocalWellLogColors.current
}