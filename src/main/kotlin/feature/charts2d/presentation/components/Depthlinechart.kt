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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oussama_chatri.core.theme.CardSurface
import com.oussama_chatri.core.theme.ChartSafeZone
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.NavyDeep
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.feature.charts2d.domain.model.ChartDataSet
import com.oussama_chatri.feature.charts2d.domain.model.ChartSeries

/**
 * Canvas-drawn depth-inverted line chart.
 *
 * - X axis: the measured quantity (pressure, ECD, velocity)
 * - Y axis: depth increasing downward (depth = 0 at top)
 * - Each [ChartSeries] is drawn as a polyline in its own color
 * - Dashed series use a [PathEffect.dashPathEffect]
 * - When the dataset has [safeWindowMin] and [safeWindowMax], the region between them
 *   is filled with a translucent teal band
 * - A crosshair tooltip follows the mouse pointer
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

    // Crosshair position in canvas coordinates
    var hoverOffset by remember { mutableStateOf<Offset?>(null) }

    val axisLabelStyle = TextStyle(
        color    = TextSecondary,
        fontSize = 10.sp
    )

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(NavyDeep)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 56.dp, end = 16.dp, top = 16.dp, bottom = 40.dp)
                .pointerInput(Unit) {
                    detectHoverPointerInput(
                        onMove = { event ->
                            hoverOffset = event.changes.first().position
                        },
                        onExit = { hoverOffset = null }
                    )
                }
        ) {
            val w = size.width
            val h = size.height

            if (w <= 0f || h <= 0f) return@Canvas

            val visibleSeries = dataSet.series.filter {
                seriesVisibility[it.id] != false && it.points.isNotEmpty()
            }

            if (visibleSeries.isEmpty()) return@Canvas

            // Compute full data range across all series
            val allDepths = visibleSeries.flatMap { it.points.map { p -> p.second } }
            val allX      = visibleSeries.flatMap { it.points.map { p -> p.first } }
            val minDepth  = allDepths.minOrNull() ?: 0.0
            val maxDepth  = allDepths.maxOrNull() ?: 1.0
            val minX      = (allX.minOrNull() ?: 0.0).coerceAtMost(0.0)
            val maxX      = (allX.maxOrNull() ?: 1.0)

            // Apply depth range filter
            val depthSpan  = maxDepth - minDepth
            val filterMin  = minDepth + depthFractionMin * depthSpan
            val filterMax  = minDepth + depthFractionMax * depthSpan

            fun xToCanvas(xVal: Double): Float =
                ((xVal - minX) / (maxX - minX) * w).toFloat().coerceIn(0f, w)

            fun depthToCanvas(depth: Double): Float =
                ((depth - filterMin) / (filterMax - filterMin) * h).toFloat().coerceIn(0f, h)

            // Grid lines
            val gridCount = 5
            for (i in 0..gridCount) {
                val xPos = w * i / gridCount
                drawLine(DividerColor.copy(alpha = 0.25f), Offset(xPos, 0f), Offset(xPos, h), 1f)

                val yPos = h * i / gridCount
                drawLine(DividerColor.copy(alpha = 0.25f), Offset(0f, yPos), Offset(w, yPos), 1f)
            }

            // Safe window shading
            val safeMin = dataSet.safeWindowMin?.filter { it.second in filterMin..filterMax }
            val safeMax = dataSet.safeWindowMax?.filter { it.second in filterMin..filterMax }
            if (!safeMin.isNullOrEmpty() && !safeMax.isNullOrEmpty()) {
                val path = Path()
                val minPts = safeMin.sortedBy { it.second }
                val maxPts = safeMax.sortedBy { it.second }.reversed()

                path.moveTo(xToCanvas(minPts.first().first), depthToCanvas(minPts.first().second))
                minPts.forEach { (x, d) -> path.lineTo(xToCanvas(x), depthToCanvas(d)) }
                maxPts.forEach { (x, d) -> path.lineTo(xToCanvas(x), depthToCanvas(d)) }
                path.close()

                drawPath(path, ChartSafeZone)
            }

            // Draw each series
            visibleSeries.forEach { series ->
                drawSeries(
                    series        = series,
                    filterMinDepth = filterMin,
                    filterMaxDepth = filterMax,
                    toX           = ::xToCanvas,
                    toY           = ::depthToCanvas
                )
            }

            // X axis tick labels
            for (i in 0..4) {
                val xVal  = minX + (maxX - minX) * i / 4
                val xPos  = xToCanvas(xVal)
                val label = if (xVal >= 1000) "${(xVal / 1000).toInt()}K" else String.format("%.1f", xVal)
                drawText(
                    textMeasurer = measurer,
                    text         = label,
                    style        = axisLabelStyle,
                    topLeft      = Offset(xPos - 12f, h + 6f)
                )
            }

            // Y axis tick labels
            for (i in 0..4) {
                val depth = filterMin + (filterMax - filterMin) * i / 4
                val yPos  = depthToCanvas(depth)
                drawText(
                    textMeasurer = measurer,
                    text         = "${depth.toInt()}",
                    style        = axisLabelStyle,
                    topLeft      = Offset(-52f, yPos - 6f)
                )
            }

            // Crosshair
            hoverOffset?.let { pos ->
                drawLine(
                    color       = TextMuted.copy(alpha = 0.5f),
                    start       = Offset(pos.x, 0f),
                    end         = Offset(pos.x, h),
                    strokeWidth = 1f,
                    pathEffect  = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                )
                drawLine(
                    color       = TextMuted.copy(alpha = 0.5f),
                    start       = Offset(0f, pos.y),
                    end         = Offset(w, pos.y),
                    strokeWidth = 1f,
                    pathEffect  = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                )
            }
        }
    }
}

private fun DrawScope.drawSeries(
    series: ChartSeries,
    filterMinDepth: Double,
    filterMaxDepth: Double,
    toX: (Double) -> Float,
    toY: (Double) -> Float
) {
    val pts = series.points
        .filter { it.second in filterMinDepth..filterMaxDepth }
        .sortedBy { it.second }

    if (pts.size < 2) return

    val path = Path()
    path.moveTo(toX(pts.first().first), toY(pts.first().second))
    pts.drop(1).forEach { (x, d) -> path.lineTo(toX(x), toY(d)) }

    val pathEffect = if (series.isDashed) {
        PathEffect.dashPathEffect(floatArrayOf(12f, 6f))
    } else null

    drawPath(
        path        = path,
        color       = series.color,
        style       = Stroke(width = 2f, pathEffect = pathEffect)
    )

    // Draw small dot at first and last visible point
    listOf(pts.first(), pts.last()).forEach { (x, d) ->
        drawCircle(series.color, radius = 3f, center = Offset(toX(x), toY(d)))
    }
}

// Hover pointer detection workaround for Compose Desktop
private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectHoverPointerInput(
    onMove: (androidx.compose.ui.input.pointer.PointerEvent) -> Unit,
    onExit: () -> Unit
) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            when (event.type) {
                androidx.compose.ui.input.pointer.PointerEventType.Move -> onMove(event)
                androidx.compose.ui.input.pointer.PointerEventType.Exit -> onExit()
                else -> Unit
            }
        }
    }
}