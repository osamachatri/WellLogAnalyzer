package com.oussama_chatri.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AppTheme(
    themeId: AppThemeId = AppThemeId.PETROLEUM_DARK,
    content: @Composable () -> Unit
) {
    val c = themeById(themeId)

    val colorScheme = if (c.isDark) {
        darkColorScheme(
            primary              = c.accent,
            onPrimary            = c.background,
            primaryContainer     = c.accentDim,
            onPrimaryContainer   = c.accent,
            secondary            = c.safe,
            onSecondary          = c.background,
            secondaryContainer   = c.safeDim,
            onSecondaryContainer = c.safe,
            background           = c.background,
            onBackground         = c.textPrimary,
            surface              = c.surface,
            onSurface            = c.textPrimary,
            surfaceVariant       = c.cardSurface,
            onSurfaceVariant     = c.textSecondary,
            error                = c.danger,
            onError              = c.background,
            errorContainer       = c.dangerDim,
            onErrorContainer     = c.danger,
            outline              = c.divider,
            outlineVariant       = c.divider.copy(alpha = 0.5f),
            inverseSurface       = c.textPrimary,
            inverseOnSurface     = c.background,
        )
    } else {
        lightColorScheme(
            primary              = c.accent,
            onPrimary            = c.background,
            primaryContainer     = c.accentDim,
            onPrimaryContainer   = c.accent,
            secondary            = c.safe,
            onSecondary          = c.background,
            secondaryContainer   = c.safeDim,
            onSecondaryContainer = c.safe,
            background           = c.background,
            onBackground         = c.textPrimary,
            surface              = c.surface,
            onSurface            = c.textPrimary,
            surfaceVariant       = c.cardSurface,
            onSurfaceVariant     = c.textSecondary,
            error                = c.danger,
            onError              = c.background,
            outline              = c.divider,
        )
    }

    CompositionLocalProvider(LocalWellLogColors provides c) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = WellLogTypography,
            shapes      = WellLogShapes,
            content     = content
        )
    }
}