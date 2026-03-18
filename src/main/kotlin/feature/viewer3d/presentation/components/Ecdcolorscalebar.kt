package com.oussama_chatri.feature.viewer3d.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.CardSurface
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.core.util.NumberFormatter

/**
 * Vertical gradient bar showing the ECD colour scale from minEcd (teal) to maxEcd (red).
 * A tick line marks the current max ECD on the scale.
 */
@Composable
fun ECDColorScaleBar(
    minEcd: Double,
    maxEcd: Double,
    currentMaxEcd: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(CardSurface.copy(alpha = 0.92f))
            .border(1.dp, DividerColor, MaterialTheme.shapes.medium)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text  = "ECD Scale",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )

        Text(
            text  = NumberFormatter.ppg(maxEcd),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFE63946)
        )

        // Gradient bar + tick
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(180.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE63946), // red at top (high ECD)
                                Color(0xFFF4A917), // amber mid
                                Color(0xFF2EC4B6)  // teal at bottom (safe)
                            )
                        )
                    )
            )

            // Current max ECD tick line
            if (maxEcd > minEcd) {
                val fraction = ((currentMaxEcd - minEcd) / (maxEcd - minEcd))
                    .coerceIn(0.0, 1.0)
                    .toFloat()

                Canvas(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
                    val y = fraction * size.height
                    drawLine(
                        color       = Color.White,
                        start       = Offset(0f, y),
                        end         = Offset(size.width, y),
                        strokeWidth = 2f
                    )
                }
            }
        }

        Text(
            text  = NumberFormatter.ppg(minEcd),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF2EC4B6)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text  = "Max: ${NumberFormatter.ppg(currentMaxEcd)}",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}