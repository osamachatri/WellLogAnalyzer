package com.oussama_chatri.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Backgrounds & surfaces ──────────────────────────────────────────────────
val NavyDeep:     Color @Composable get() = LocalWellLogColors.current.background
val SurfaceDark:  Color @Composable get() = LocalWellLogColors.current.surface
val CardSurface:  Color @Composable get() = LocalWellLogColors.current.cardSurface
val CardElevated: Color @Composable get() = LocalWellLogColors.current.cardElevated
val DividerColor: Color @Composable get() = LocalWellLogColors.current.divider

// ── Accent / amber ──────────────────────────────────────────────────────────
val AmberGold:    Color @Composable get() = LocalWellLogColors.current.accent
val AmberDark:    Color @Composable get() = LocalWellLogColors.current.accentDark
val AmberDim:     Color @Composable get() = LocalWellLogColors.current.accentDim

// ── Semantic status ─────────────────────────────────────────────────────────
val TealSafe:     Color @Composable get() = LocalWellLogColors.current.safe
val TealSafeDim:  Color @Composable get() = LocalWellLogColors.current.safeDim
val CoralDanger:  Color @Composable get() = LocalWellLogColors.current.danger
val CoralDim:     Color @Composable get() = LocalWellLogColors.current.dangerDim
val AmberWarning: Color @Composable get() = LocalWellLogColors.current.warning
val StatusNotRun: Color @Composable get() = LocalWellLogColors.current.textMuted

// ── Text ────────────────────────────────────────────────────────────────────
val TextPrimary:   Color @Composable get() = LocalWellLogColors.current.textPrimary
val TextSecondary: Color @Composable get() = LocalWellLogColors.current.textSecondary
val TextMuted:     Color @Composable get() = LocalWellLogColors.current.textMuted

// ── Chart series ────────────────────────────────────────────────────────────
val ChartAmber:    Color @Composable get() = LocalWellLogColors.current.chartAmber
val ChartTeal:     Color @Composable get() = LocalWellLogColors.current.chartTeal
val ChartBlue:     Color @Composable get() = LocalWellLogColors.current.chartBlue
val ChartCoral:    Color @Composable get() = LocalWellLogColors.current.chartCoral
val ChartSafeZone: Color @Composable get() = LocalWellLogColors.current.chartSafeZone

// ── Lithology ───────────────────────────────────────────────────────────────
val LithShale:      Color @Composable get() = LocalWellLogColors.current.lithShale
val LithSandstone:  Color @Composable get() = LocalWellLogColors.current.lithSandstone
val LithLimestone:  Color @Composable get() = LocalWellLogColors.current.lithLimestone
val LithSalt:       Color @Composable get() = LocalWellLogColors.current.lithSalt
val LithTargetSand: Color @Composable get() = LocalWellLogColors.current.lithTargetSand