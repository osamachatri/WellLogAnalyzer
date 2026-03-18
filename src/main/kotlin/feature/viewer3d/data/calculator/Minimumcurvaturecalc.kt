package com.oussama_chatri.feature.viewer3d.data.calculator

import com.oussama_chatri.feature.viewer3d.domain.model.Trajectory3DPoint
import com.oussama_chatri.feature.wellinput.domain.model.SurveyStation
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan
import kotlin.math.PI

/**
 * Converts a directional survey (MD / Inclination / Azimuth) into a list of
 * Cartesian 3D coordinates using the Minimum Curvature Method.
 *
 * Reference: API Recommended Practice 11V (and Bourgoyne et al. Chapter 8).
 *
 * Coordinate system:
 *   +X = East
 *   +Y = North
 *   +Z = TVD (positive downward)
 */
object MinimumCurvatureCalc {

    /**
     * @param stations  Ordered survey stations from surface to TD.
     * @param ecdMap    Optional map of measuredDepth → ECD for colour-mapping.
     * @return          Ordered list of [Trajectory3DPoint] including the surface point (0,0,0).
     */
    fun compute(
        stations: List<SurveyStation>,
        ecdMap: Map<Double, Double> = emptyMap()
    ): List<Trajectory3DPoint> {
        if (stations.isEmpty()) return listOf(Trajectory3DPoint(0.0, 0.0, 0.0, 0.0))

        val sorted = stations.sortedBy { it.measuredDepth }
        val result = mutableListOf<Trajectory3DPoint>()

        // Surface point
        result += Trajectory3DPoint(
            measuredDepth = 0.0,
            x = 0.0, y = 0.0, z = 0.0,
            ecd = ecdMap[0.0]
        )

        var x = 0.0
        var y = 0.0
        var z = 0.0

        for (i in 1 until sorted.size) {
            val upper = sorted[i - 1]
            val lower = sorted[i]

            val dMd   = lower.measuredDepth - upper.measuredDepth
            if (dMd <= 0.0) continue

            val i1 = Math.toRadians(upper.inclination)
            val i2 = Math.toRadians(lower.inclination)
            val a1 = Math.toRadians(upper.azimuth)
            val a2 = Math.toRadians(lower.azimuth)

            // Dog-leg angle
            val dl = acosSafe(
                cos(i2 - i1) - sin(i1) * sin(i2) * (1.0 - cos(a2 - a1))
            )

            // Ratio factor — approaches dMd/2 as dl → 0
            val rf = if (abs(dl) < 1e-9) 1.0 else (2.0 / dl) * tan(dl / 2.0)

            val dTvd = (dMd / 2.0) * (cos(i1) + cos(i2)) * rf
            val dN   = (dMd / 2.0) * (sin(i1) * cos(a1) + sin(i2) * cos(a2)) * rf
            val dE   = (dMd / 2.0) * (sin(i1) * sin(a1) + sin(i2) * sin(a2)) * rf

            z += dTvd
            y += dN
            x += dE

            result += Trajectory3DPoint(
                measuredDepth = lower.measuredDepth,
                x = x,
                y = y,
                z = z,
                ecd = ecdMap.entries.minByOrNull { abs(it.key - lower.measuredDepth) }?.value
            )
        }

        return result
    }

    private fun acosSafe(v: Double): Double =
        Math.acos(v.coerceIn(-1.0, 1.0))

    /**
     * Builds a vertical trajectory when no deviation survey stations are provided.
     * Simply steps straight down in TVD.
     */
    fun vertical(totalDepth: Double, stepFt: Double = 500.0): List<Trajectory3DPoint> {
        val pts = mutableListOf<Trajectory3DPoint>()
        var depth = 0.0
        while (depth <= totalDepth) {
            pts += Trajectory3DPoint(depth, 0.0, 0.0, depth)
            depth += stepFt
        }
        if (pts.last().z < totalDepth) {
            pts += Trajectory3DPoint(totalDepth, 0.0, 0.0, totalDepth)
        }
        return pts
    }
}