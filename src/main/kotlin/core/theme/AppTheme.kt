package com.oussama_chatri.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary          = AmberGold,
    onPrimary        = NavyDeep,
    primaryContainer = AmberDim,
    onPrimaryContainer = AmberGold,

    secondary        = TealSafe,
    onSecondary      = NavyDeep,
    secondaryContainer = TealDim,
    onSecondaryContainer = TealSafe,

    tertiary         = ChartBlue,
    onTertiary       = NavyDeep,

    background       = NavyDeep,
    onBackground     = TextPrimary,

    surface          = SlateDark,
    onSurface        = TextPrimary,
    surfaceVariant   = CardSurface,
    onSurfaceVariant = TextSecondary,

    error            = CoralDanger,
    onError          = Color.White,
    errorContainer   = CoralDim,
    onErrorContainer = CoralDanger,

    outline          = DividerColor,
    outlineVariant   = DividerColor.copy(alpha = 0.5f),

    inverseSurface   = TextPrimary,
    inverseOnSurface = NavyDeep,
)

val LightColorScheme: ColorScheme = lightColorScheme(
    primary          = Color(0xFFB57A10),   // Darker amber for light bg contrast
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFFFF0CC),
    onPrimaryContainer = Color(0xFF3D2600),

    secondary        = Color(0xFF1A8C82),
    onSecondary      = Color.White,
    secondaryContainer = Color(0xFFCCF5F2),
    onSecondaryContainer = Color(0xFF00302C),

    background       = Color(0xFFF5F7FA),
    onBackground     = Color(0xFF0D1B2A),

    surface          = Color(0xFFFFFFFF),
    onSurface        = Color(0xFF0D1B2A),
    surfaceVariant   = Color(0xFFEBEFF5),
    onSurfaceVariant = Color(0xFF3A4A5C),

    error            = Color(0xFFB3262E),
    onError          = Color.White,

    outline          = Color(0xFFCCD3DC),
)

data class ThemeState(val isDark: Boolean)

val LocalThemeState = compositionLocalOf { ThemeState(isDark = true) }

@Composable
fun AppTheme(
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalThemeState provides ThemeState(isDark = isDark)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = WellLogTypography,
            shapes      = WellLogShapes,
            content     = content
        )
    }
}