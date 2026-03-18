package com.oussama_chatri.feature.charts2d.domain.model

import androidx.compose.ui.graphics.Color

/**
 * A single named series of (x, y) data points for one line or bar.
 */
data class ChartSeries(
    val id: String,
    val label: String,
    val color: Color,
    val points: List<Pair<Double, Double>>,
    val isDashed: Boolean = false
)

/**
 * Everything a chart composable needs to render itself.
 *
 * [xLabel] and [yLabel] are axis titles.
 * [safeWindowMin] / [safeWindowMax] are optional — when both are set,
 * the chart shades the region between them (e.g., pore pressure → frac gradient).
 */
data class ChartDataSet(
    val type: ChartType,
    val title: String,
    val xLabel: String,
    val yLabel: String,
    val series: List<ChartSeries>,
    val depthInverted: Boolean = false,
    val safeWindowMin: List<Pair<Double, Double>>? = null,
    val safeWindowMax: List<Pair<Double, Double>>? = null
)