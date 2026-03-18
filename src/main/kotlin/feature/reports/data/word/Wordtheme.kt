package com.oussama_chatri.feature.reports.data.word

/**
 * Brand colour constants for the Word report templates.
 *
 * Values are 6-character hex strings (no '#') as required by Docx4j's
 * color setters (e.g. rPr.color.`val` = WordTheme.AMBER).
 *
 * Matches the Compose theme palette in [com.oussama_chatri.core.theme.Color].
 */
object WordTheme {

    // Primary palette
    const val NAVY       = "0D1B2A"   // deep navy — page/header backgrounds
    const val SLATE      = "1A2535"   // dark slate — table header backgrounds
    const val CARD       = "212E42"   // card surface

    // Accent
    const val AMBER      = "F4A917"   // primary CTA / KPI values
    const val AMBER_DARK = "D4920F"   // pressed / border amber

    // Semantic
    const val TEAL       = "2EC4B6"   // safe / success
    const val CORAL      = "E63946"   // danger / alert

    // Neutrals
    const val WHITE      = "FFFFFF"
    const val LIGHT_GRAY = "EBF0F5"   // alternating table row tint
    const val MID_GRAY   = "94A3B8"   // secondary / caption text
    const val DARK_TEXT  = "0D1B2A"   // body text on white background
    const val DIVIDER    = "2E3E58"   // subtle borders

    // Typography
    const val FONT_BODY    = "Calibri"
    const val FONT_HEADING = "Calibri"
    const val FONT_MONO    = "Courier New"

    // Size helpers (half-points — multiply pt by 2)
    const val SIZE_DISPLAY  = 48  // 24 pt
    const val SIZE_H1       = 40  // 20 pt
    const val SIZE_H2       = 32  // 16 pt
    const val SIZE_H3       = 26  // 13 pt
    const val SIZE_BODY     = 22  // 11 pt
    const val SIZE_SMALL    = 18  // 9 pt
    const val SIZE_CAPTION  = 16  // 8 pt

    // Page margins (twips: 1 inch = 1440 twips)
    const val MARGIN_TOP    = 1080  // 0.75 in
    const val MARGIN_BOTTOM = 1080
    const val MARGIN_LEFT   = 1260  // 0.875 in
    const val MARGIN_RIGHT  = 1260
}