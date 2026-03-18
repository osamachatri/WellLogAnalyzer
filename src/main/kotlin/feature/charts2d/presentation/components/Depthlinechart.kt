package com.oussama_chatri.feature.charts2d.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oussama_chatri.core.theme.ChartSafeZone
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.NavyDeep
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.feature.charts2d.domain.model.ChartDataSet
import com.oussama_chatri.feature.charts2d.domain.model.ChartSeries

/**
 * Canvas-drawn depth-inverted line chart.
 * Y axis is depth increasing downward; X axis is the measured quantity.
 */
@Composable
fun DepthLineChart(
    dataSet: ChartDataSet,
    seriesVisibility: Map<String, Boolean>,
    depthFractionMin: Float,
    depthFractionMax: Float,
    modifier: Modifier = Modifier
) {
    val measurer = rememberTextMeasurer()
    var hoverOffset by remember { mutableStateOf<Offset?>(null) }

    val colorBg        = NavyDeep
    val colorDivider   = DividerColor
    val colorSafeZone  = ChartSafeZone
    val colorTextMuted = TextMuted
    val colorTextSec   = TextSecondary

    val axisLabelStyle = TextStyle(
        color    = colorTextSec,
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
                .padding(start = 60.dp, end = 20.dp, top = 20.dp, bottom = 44.dp)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Move ->
                                    hoverOffset = event.changes.firstOrNull()?.position
                                PointerEventType.Exit ->
                                    hoverOffset = null
                                else -> Unit
                            }
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            if (w < 1f || h < 1f) return@Canvas

            val visibleSeries = dataSet.series.filter {
                seriesVisibility[it.id] != false && it.points.isNotEmpty()
            }
            if (visibleSeries.isEmpty()) return@Canvas

            val allDepths = visibleSeries.flatMap { s -> s.points.map { it.second } }
            val allX      = visibleSeries.flatMap { s -> s.points.map { it.first } }

            val minDepth = allDepths.minOrNull() ?: 0.0
            val maxDepth = (allDepths.maxOrNull() ?: 1.0).let { if (it == minDepth) minDepth + 1.0 else it }
            val minX     = (allX.minOrNull() ?: 0.0).coerceAtMost(0.0)
            val maxX     = (allX.maxOrNull() ?: 1.0).let { if (it == minX) minX + 1.0 else it }

            val depthSpan  = maxDepth - minDepth
            val filterMin  = minDepth + depthFractionMin * depthSpan
            val filterMax  = minDepth + depthFractionMax * depthSpan
            val filterSpan = (filterMax - filterMin).coerceAtLeast(1.0)
            val xSpan      = (maxX - minX).coerceAtLeast(1.0)

            fun xToCanvas(xVal: Double): Float =
                ((xVal - minX) / xSpan * w).toFloat().coerceIn(0f, w)

            fun depthToCanvas(depth: Double): Float =
                ((depth - filterMin) / filterSpan * h).toFloat().coerceIn(0f, h)

            // Grid
            for (i in 0..4) {
                val xPos = w * i / 4f
                drawLine(colorDivider.copy(alpha = 0.25f), Offset(xPos, 0f), Offset(xPos, h), 1f)
                val yPos = h * i / 4f
                drawLine(colorDivider.copy(alpha = 0.25f), Offset(0f, yPos), Offset(w, yPos), 1f)
            }

            // Safe window shading
            val safeMin = dataSet.safeWindowMin?.filter { it.second in filterMin..filterMax }
            val safeMax = dataSet.safeWindowMax?.filter { it.second in filterMin..filterMax }
            if (!safeMin.isNullOrEmpty() && !safeMax.isNullOrEmpty()) {
                val path   = Path()
                val minPts = safeMin.sortedBy { it.second }
                val maxPts = safeMax.sortedBy { it.second }.reversed()
                path.moveTo(xToCanvas(minPts.first().first), depthToCanvas(minPts.first().second))
                minPts.forEach { (x, d) -> path.lineTo(xToCanvas(x), depthToCanvas(d)) }
                maxPts.forEach { (x, d) -> path.lineTo(xToCanvas(x), depthToCanvas(d)) }
                path.close()
                drawPath(path, colorSafeZone)
            }

            // Series lines
            visibleSeries.forEach { series ->
                val pts = series.points
                    .filter { it.second in filterMin..filterMax }
                    .sortedBy { it.second }

                if (pts.size >= 2) {
                    val path = Path()
                    path.moveTo(xToCanvas(pts.first().first), depthToCanvas(pts.first().second))
                    pts.drop(1).forEach { (x, d) -> path.lineTo(xToCanvas(x), depthToCanvas(d)) }

                    val pathEffect = if (series.isDashed)
                        PathEffect.dashPathEffect(floatArrayOf(12f, 6f)) else null

                    drawPath(path, series.color, style = Stroke(width = 2f, pathEffect = pathEffect))

                    listOf(pts.first(), pts.last()).forEach { (x, d) ->
                        drawCircle(series.color, radius = 3f, center = Offset(xToCanvas(x), depthToCanvas(d)))
                    }
                }
            }

            // X axis tick labels
            if (h > 20f) {
                for (i in 0..4) {
                    val xVal  = minX + xSpan * i / 4.0
                    val xPos  = xToCanvas(xVal)
                    val label = if (xVal >= 1000) "${(xVal / 1000).toInt()}K"
                    else String.format("%.1f", xVal)
                    safeDrawText(measurer, label, axisLabelStyle, Offset(xPos - 12f, h + 6f), w, h)
                }
            }

            // Y axis tick labels
            if (w > 20f) {
                for (i in 0..4) {
                    val depth = filterMin + filterSpan * i / 4.0
                    val yPos  = depthToCanvas(depth)
                    safeDrawText(measurer, "${depth.toInt()}", axisLabelStyle, Offset(-52f, yPos - 6f), w, h)
                }
            }

            // Crosshair
            hoverOffset?.let { pos ->
                val dash = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                drawLine(colorTextMuted.copy(alpha = 0.5f), Offset(pos.x, 0f), Offset(pos.x, h), 1f, pathEffect = dash)
                drawLine(colorTextMuted.copy(alpha = 0.5f), Offset(0f, pos.y), Offset(w, pos.y), 1f, pathEffect = dash)
            }
        }
    }
}

private fun DrawScope.safeDrawText(
    measurer: TextMeasurer,
    text: String,
    style: TextStyle,
    topLeft: Offset,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val measured = measurer.measure(text, style)
    val tw = measured.size.width.toFloat()
    val th = measured.size.height.toFloat()
    if (tw <= 0f || th <= 0f) return
    if (canvasWidth <= 0f || canvasHeight <= 0f) return
    try {
        drawText(textMeasurer = measurer, text = text, style = style, topLeft = topLeft)
    } catch (_: IllegalArgumentException) { }
}