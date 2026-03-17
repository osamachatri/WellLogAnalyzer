package com.oussama_chatri.feature.wellinput.domain.model

import kotlin.math.PI

data class BitParameters(
    val bitSize: Double,
    val nozzleCount: Int,
    val nozzleSizes: List<Double>
) {
    companion object {
        fun empty() = BitParameters(
            bitSize     = 0.0,
            nozzleCount = 0,
            nozzleSizes = emptyList()
        )
    }

    /**
     * Total nozzle flow area in square inches.
     * Formula: A = (π/4) × Σ(d_i / 32)²
     */
    val totalFlowArea: Double
        get() = nozzleSizes.sumOf { sizeIn32 ->
            val diameterIn = sizeIn32 / 32.0
            PI / 4.0 * diameterIn * diameterIn
        }

    /**
     * Bit cross-sectional area in square inches.
     */
    val bitArea: Double
        get() = PI / 4.0 * bitSize * bitSize
}