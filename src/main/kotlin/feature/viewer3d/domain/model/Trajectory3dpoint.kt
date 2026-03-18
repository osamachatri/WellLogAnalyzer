package com.oussama_chatri.feature.viewer3d.domain.model

/**
 * A single point on the computed 3D well trajectory.
 *
 * Coordinates are in feet, relative to the surface location (0, 0, 0).
 *  - [x] = Easting departure
 *  - [y] = Northing departure
 *  - [z] = True Vertical Depth (positive downward)
 *  - [measuredDepth] = along-hole distance from surface
 *  - [ecd] = ECD value at this depth for colour-mapping; null if simulation
 *            result is not yet available
 */
data class Trajectory3DPoint(
    val measuredDepth: Double,
    val x: Double,
    val y: Double,
    val z: Double,
    val ecd: Double? = null
)