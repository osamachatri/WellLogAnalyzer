package com.oussama_chatri.feature.charts2d.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.NavyDeep
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.feature.charts2d.domain.model.ChartDataSet

/**
 * Horizontal bar chart for the Bit Hydraulics and Component Breakdown tabs.
 * Each [ChartSeries] contributes one bar; the bar length is proportional
 * to the single value stored in [ChartSeries.points[0].first].
 */
@Composable
fun HorizontalBarChart(
    dataSet: ChartDataSet,
    seriesVisibility: Map<String, Boolean>,
    modifier: Modifier = Modifier
) {
    val measurer = rememberTextMeasurer()

    val labelStyle = TextStyle(color = TextSecondary, fontSize = 10.sp)
    val valueStyle = TextStyle(color = androidx.compose.ui.graphics.Color.White, fontSize = 10.sp)

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(NavyDeep)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 140.dp, end = 32.dp, top = 24.dp, bottom = 24.dp)
        ) {
            val w = size.width
            val h = size.height

            val visibleSeries = dataSet.series.filter {
                seriesVisibility[it.id] != false && it.points.isNotEmpty()
            }

            if (visibleSeries.isEmpty()) return@Canvas

            val maxValue = visibleSeries.maxOf { it.points.first().first }.coerceAtLeast(1.0)
            val barCount = visibleSeries.size
            val barSpacing = h / barCount
            val barHeight  = (barSpacing * 0.55f).coerceAtLeast(16f)

            // Grid lines
            for (i in 0..4) {
                val xPos = w * i / 4
                drawLine(DividerColor.copy(alpha = 0.2f), Offset(xPos, 0f), Offset(xPos, h), 1f)
            }

            visibleSeries.forEachIndexed { index, series ->
                val value   = series.points.first().first
                val barLen  = (value / maxValue * w).toFloat().coerceAtLeast(4f)
                val yCenter = barSpacing * index + barSpacing / 2f
                val yTop    = yCenter - barHeight / 2f

                // Bar fill
                drawRoundRect(
                    color       = series.color,
                    topLeft     = Offset(0f, yTop),
                    size        = Size(barLen, barHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )

                // Bar label (left side, outside the canvas padding)
                drawText(
                    textMeasurer = measurer,
                    text         = series.label,
                    style        = labelStyle,
                    topLeft      = Offset(-130.dp.toPx(), yTop + barHeight / 2f - 6f)
                )

                // Value label inside or just after the bar
                val valueText = formatBarValue(value)
                drawText(
                    textMeasurer = measurer,
                    text         = valueText,
                    style        = valueStyle,
                    topLeft      = Offset(barLen + 4f, yTop + barHeight / 2f - 6f)
                )
            }
        }
    }
}

private fun formatBarValue(value: Double): String = when {
    value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000)
    value >= 1_000     -> String.format("%.1fK", value / 1_000)
    value >= 100       -> String.format("%.0f", value)
    else               -> String.format("%.2f", value)
}