package com.oussama_chatri.core.theme

import androidx.compose.ui.graphics.Color


// ── Primary brand ──────────────────────────────────────────────────────────
/** Deep navy — primary background, sidebar base */
val NavyDeep       = Color(0xFF0D1B2A)
/** Dark slate — surface / card background */
val SlateDark      = Color(0xFF1A2535)
/** Card surface — slightly lighter than SlateDark for layering */
val CardSurface    = Color(0xFF212E42)
/** Elevated card — hover states, selected rows */
val CardElevated   = Color(0xFF2A3A54)
/** Divider / subtle border */
val DividerColor   = Color(0xFF2E3E58)

// ── Accent ─────────────────────────────────────────────────────────────────
/** Amber gold — primary CTA, icons, highlights */
val AmberGold      = Color(0xFFF4A917)
/** Amber dark — pressed state for amber buttons */
val AmberDark      = Color(0xFFD4920F)
/** Amber dim — amber at low opacity for backgrounds */
val AmberDim       = Color(0x1AF4A917)

// ── Semantic ───────────────────────────────────────────────────────────────
/** Teal green — safe / success / OK states */
val TealSafe       = Color(0xFF2EC4B6)
/** Teal dim — teal at low opacity */
val TealDim        = Color(0x1A2EC4B6)
/** Coral red — danger / alert / error states */
val CoralDanger    = Color(0xFFE63946)
/** Coral dim — coral at low opacity */
val CoralDim       = Color(0x1AE63946)
/** Amber warning — mid-range warning state */
val AmberWarning   = Color(0xFFF4A917)

// ── Typography ─────────────────────────────────────────────────────────────
/** White — primary text */
val TextPrimary    = Color(0xFFFFFFFF)
/** Light gray — secondary / label text */
val TextSecondary  = Color(0xFF94A3B8)
/** Muted — disabled / placeholder text */
val TextMuted      = Color(0xFF4A5568)

// ── Chart colors ───────────────────────────────────────────────────────────
/** Hydrostatic pressure line */
val ChartAmber     = Color(0xFFF4A917)
/** ECD line */
val ChartTeal      = Color(0xFF2EC4B6)
/** Pore pressure line */
val ChartBlue      = Color(0xFF4FC3F7)
/** Fracture gradient line */
val ChartCoral     = Color(0xFFE63946)
/** Safe window fill */
val ChartSafeZone  = Color(0x1A2EC4B6)

// ── Geology lithology colors ────────────────────────────────────────────────
val LithShale      = Color(0xFF607080)
val LithSandstone  = Color(0xFFD4A843)
val LithLimestone  = Color(0xFFD9CDB4)
val LithSalt       = Color(0xFFB0C8D8)
val LithTargetSand = Color(0xFFF4A917)

// ── Status badge fills (20% opacity versions are computed in composables) ──
val StatusSafe     = TealSafe
val StatusWarning  = AmberWarning
val StatusDanger   = CoralDanger
val StatusNotRun   = Color(0xFF4A5568)