package com.oussama_chatri.feature.simulation.domain.model

/**
 * A single depth-pressure sample from the simulation run.
 * The solver emits one of these per depth step so the UI can update in real time.
 */
data class PressurePoint(
    val depth: Double,
    val hydrostaticPressure: Double,
    val annularPressureLoss: Double,
    val ecd: Double,
    val porePressure: Double,
    val fractureGradientPressure: Double
) {
    val ecdPressure: Double get() = ecd * 0.052 * depth

    val isEcdSafe: Boolean
        get() = ecd in porePressure..fractureGradientPressure
}