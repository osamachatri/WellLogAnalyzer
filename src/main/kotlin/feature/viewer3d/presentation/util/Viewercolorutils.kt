package com.oussama_chatri.feature.viewer3d.presentation.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.oussama_chatri.feature.wellinput.domain.model.Lithology

/**
 * Maps a [Lithology] to its display colour, consistent with the theme palette
 * defined in [com.oussama_chatri.core.theme.Color].
 */
fun lithologyColor(lithology: Lithology): Color = when (lithology) {
    Lithology.SHALE      -> Color(0xFF607080)
    Lithology.SANDSTONE  -> Color(0xFFD4A843)
    Lithology.LIMESTONE  -> Color(0xFFD9CDB4)
    Lithology.SALT       -> Color(0xFFB0C8D8)
    Lithology.DOLOMITE   -> Color(0xFF9EB89E)
    Lithology.ANHYDRITE  -> Color(0xFFCCB8CC)
}

/**
 * Maps an ECD value to a colour on the teal → amber → red gradient.
 *
 * @param ecd     The ECD value to map.
 * @param minEcd  Lower bound of the colour scale (maps to teal/safe).
 * @param maxEcd  Upper bound (maps to red/danger).
 */
fun ecdColor(ecd: Double, minEcd: Double, maxEcd: Double): Color {
    val span = (maxEcd - minEcd).coerceAtLeast(0.001)
    val t    = ((ecd - minEcd) / span).coerceIn(0.0, 1.0).toFloat()

    val safe   = Color(0xFF2EC4B6) // teal
    val warning = Color(0xFFF4A917) // amber
    val danger  = Color(0xFFE63946) // red

    return if (t < 0.5f) {
        lerp(safe, warning, t * 2f)
    } else {
        lerp(warning, danger, (t - 0.5f) * 2f)
    }
}