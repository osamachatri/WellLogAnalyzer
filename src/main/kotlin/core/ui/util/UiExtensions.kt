package com.oussama_chatri.core.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * UI utility extensions for WellLogAnalyzer composables.
 */

/**
 * Applies a card-like appearance: clip + background + optional border.
 * Shorthand for the common `clip → background → border` triple.
 */
fun Modifier.cardStyle(
    shape: Shape,
    backgroundColor: Color,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .then(
        if (borderColor != null)
            Modifier.border(borderWidth, borderColor, shape)
        else
            Modifier
    )

/**
 * Adds symmetric horizontal + vertical padding in one call.
 */
fun Modifier.symmetricPadding(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp
): Modifier = this.padding(horizontal = horizontal, vertical = vertical)

/**
 * Returns a contrasting text color (black or white) for a given [background].
 * Useful for dynamic badge text where the background color varies.
 */
fun Color.contrastingTextColor(): Color =
    if (luminance() > 0.35f) Color(0xFF0D1B2A) else Color.White

/**
 * Linearly interpolates between two colors by [fraction] (0f = start, 1f = end).
 */
fun lerp(start: Color, end: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red   = start.red   + (end.red   - start.red)   * f,
        green = start.green + (end.green - start.green) * f,
        blue  = start.blue  + (end.blue  - start.blue)  * f,
        alpha = start.alpha + (end.alpha - start.alpha) * f
    )
}

/**
 * Maps a [value] in [domain] range to a color along a gradient defined by [stops].
 *
 * Used for ECD color mapping along the wellbore tube:
 * ```kotlin
 * val ecdColor = mapToGradient(
 *     value  = ecd,
 *     domain = 10.0f..14.0f,
 *     stops  = listOf(TealSafe, AmberWarning, CoralDanger)
 * )
 * ```
 */
fun mapToGradient(
    value: Float,
    domain: ClosedFloatingPointRange<Float>,
    stops: List<Color>
): Color {
    if (stops.isEmpty()) return Color.Gray
    if (stops.size == 1) return stops.first()

    val normalized = ((value - domain.start) / (domain.endInclusive - domain.start))
        .coerceIn(0f, 1f)
    val scaledIndex = normalized * (stops.size - 1)
    val lowerIndex  = scaledIndex.toInt().coerceAtMost(stops.size - 2)
    val localFrac   = scaledIndex - lowerIndex

    return lerp(stops[lowerIndex], stops[lowerIndex + 1], localFrac)
}