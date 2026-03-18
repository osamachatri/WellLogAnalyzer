package com.oussama_chatri.feature.dashboard.domain.model

/**
 * Lightweight summary of a saved well project shown on the Dashboard.
 * Never contains raw simulation data — just enough to populate the table and stat cards.
 */
data class ProjectSummary(
    val id: String,
    val wellName: String,
    val totalDepth: Double,          // ft
    val simulationCount: Int,
    val lastRunTimestamp: Long?,     // epoch ms, null = never run
    val lastExportTimestamp: Long?,  // epoch ms, null = never exported
    val lastExportFormat: String?,   // "Excel" | "Word" | "Both" | null
    val maxEcd: Double?,             // ppg, null = no simulation yet
    val isEcdSafe: Boolean?,         // null = no simulation yet
    val createdAt: Long              // epoch ms
)