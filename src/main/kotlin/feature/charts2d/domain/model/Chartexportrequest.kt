package com.oussama_chatri.feature.charts2d.domain.model

/**
 * Carries everything needed to export a chart to a PNG file.
 */
data class ChartExportRequest(
    val type: ChartType,
    val wellName: String,
    val destinationPath: String
)