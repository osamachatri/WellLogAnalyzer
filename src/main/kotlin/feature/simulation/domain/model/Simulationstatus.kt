package com.oussama_chatri.feature.simulation.domain.model

/**
 * Lifecycle state of the simulation engine, observed by the ViewModel
 * and reflected in the UI as progress, results, or error states.
 */
sealed class SimulationStatus {

    data object Idle : SimulationStatus()

    data class Running(
        val currentDepth: Double,
        val totalDepth: Double
    ) : SimulationStatus() {
        val progressFraction: Float
            get() = if (totalDepth > 0) (currentDepth / totalDepth).toFloat().coerceIn(0f, 1f) else 0f
    }

    data class Done(val result: SimulationResult) : SimulationStatus()

    data class Failed(
        val message: String,
        val cause: Throwable? = null
    ) : SimulationStatus()
}