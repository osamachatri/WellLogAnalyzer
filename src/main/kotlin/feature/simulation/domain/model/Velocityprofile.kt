package com.oussama_chatri.feature.simulation.domain.model

/**
 * Annular velocity at a specific depth.
 * Paired with [PressurePoint] for a complete picture of the hydraulics state at that depth.
 */
data class VelocityProfile(
    val depth: Double,
    val annularVelocity: Double,
    val pipeVelocity: Double,
    val flowRegime: FlowRegime
)

enum class FlowRegime { LAMINAR, TURBULENT, TRANSITIONAL }