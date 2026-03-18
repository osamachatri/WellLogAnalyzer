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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
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
 * Horizontal bar chart for Bit Hydraulics and Component Breakdown tabs.
 */
@Composable
fun HorizontalBarChart(
    dataSet: ChartDataSet,
    seriesVisibility: Map<String, Boolean>,
    modifier: Modifier = Modifier
) {
    val measurer = rememberTextMeasurer()

    val colorBg      = NavyDeep
    val colorDivider = DividerColor
    val colorTextSec = TextSecondary

    val labelStyle = TextStyle(color = colorTextSec, fontSize = 10.sp)
    val valueStyle = TextStyle(
        color    = androidx.compose.ui.graphics.Color.White,
        fontSize = 10.sp
    )

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(colorBg)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
        ) {
            val w = size.width
            val h = size.height

            if (w < 1f || h < 1f) return@Canvas

            val visibleSeries = dataSet.series.filter {
                seriesVisibility[it.id] != false && it.points.isNotEmpty()
            }
            if (visibleSeries.isEmpty()) return@Canvas

            val maxValue   = visibleSeries.maxOf { it.points.first().first }.coerceAtLeast(1.0)
            val barCount   = visibleSeries.size
            val barSpacing = h / barCount
            val barHeight  = (barSpacing * 0.5f).coerceIn(8f, 40f)

            // Vertical grid lines
            for (i in 0..4) {
                val xPos = w * i / 4f
                drawLine(colorDivider.copy(alpha = 0.2f), Offset(xPos, 0f), Offset(xPos, h), 1f)
            }

            visibleSeries.forEachIndexed { index, series ->
                val value   = series.points.first().first
                val maxBarW = w * 0.78f
                val barLen  = (value / maxValue * maxBarW).toFloat().coerceAtLeast(4f)
                val yCenter = barSpacing * index + barSpacing / 2f
                val yTop    = (yCenter - barHeight / 2f).coerceAtLeast(0f)

                drawRoundRect(
                    color        = series.color,
                    topLeft      = Offset(0f, yTop),
                    size         = Size(barLen, barHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )

                safeDrawText(
                    measurer     = measurer,
                    text         = series.label,
                    style        = labelStyle,
                    topLeft      = Offset(4f, yTop - 14f),
                    canvasWidth  = w,
                    canvasHeight = h
                )

                safeDrawText(
                    measurer     = measurer,
                    text         = formatBarValue(value),
                    style        = valueStyle,
                    topLeft      = Offset(barLen + 6f, yTop + barHeight / 2f - 6f),
                    canvasWidth  = w,
                    canvasHeight = h
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

private fun DrawScope.safeDrawText(
    measurer: TextMeasurer,
    text: String,
    style: TextStyle,
    topLeft: Offset,
    canvasWidth: Float,
    canvasHeight: Float
) {
    if (canvasWidth < 1f || canvasHeight < 1f) return
    val measured = measurer.measure(text, style)
    if (measured.size.width <= 0 || measured.size.height <= 0) return
    try {
        drawText(textMeasurer = measurer, text = text, style = style, topLeft = topLeft)
    } catch (_: IllegalArgumentException) { }
}