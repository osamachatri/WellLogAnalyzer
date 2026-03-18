package com.oussama_chatri.feature.viewer3d.domain.model

/**
 * Complete 3D representation of a wellbore ready for rendering.
 *
 * [trajectoryPoints] are ordered surface → TD.
 * [geologyLayers]    are ordered top → bottom.
 * [totalDepth]       mirrors the well profile total depth for axis scaling.
 */
data class Wellbore3DModel(
    val wellName: String,
    val totalDepth: Double,
    val trajectoryPoints: List<Trajectory3DPoint>,
    val geologyLayers: List<GeologyLayer>,
    val maxEcd: Double,
    val minEcd: Double
) {
    val maxHorizontalReach: Double
        get() = trajectoryPoints.maxOfOrNull { kotlin.math.sqrt(it.x * it.x + it.y * it.y) } ?: 0.0

    val maxInclination: Double
        get() {
            if (trajectoryPoints.size < 2) return 0.0
            var maxInc = 0.0
            for (i in 1 until trajectoryPoints.size) {
                val prev = trajectoryPoints[i - 1]
                val curr = trajectoryPoints[i]
                val dz   = curr.z - prev.z
                val dh   = kotlin.math.sqrt((curr.x - prev.x).pow(2) + (curr.y - prev.y).pow(2))
                val inc  = Math.toDegrees(kotlin.math.atan2(dh, dz))
                if (inc > maxInc) maxInc = inc
            }
            return maxInc
        }
}

private fun Double.pow(n: Int): Double = Math.pow(this, n.toDouble())