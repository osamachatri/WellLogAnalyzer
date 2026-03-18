package com.oussama_chatri.feature.viewer3d.domain.usecase

import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.viewer3d.data.calculator.MinimumCurvatureCalc
import com.oussama_chatri.feature.viewer3d.domain.model.Trajectory3DPoint
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

/**
 * Computes the 3D well trajectory from a [WellProfile]'s deviation survey,
 * optionally colour-mapping ECD values from a [SimulationResult].
 */
class ComputeWellTrajectoryUseCase {

    /**
     * @param profile  The well profile containing the deviation survey.
     * @param result   Optional simulation result used to map ECD onto the tube.
     * @return         Ordered trajectory points from surface to TD.
     */
    fun execute(
        profile: WellProfile,
        result: SimulationResult? = null
    ): List<Trajectory3DPoint> {
        // Build depth → ECD lookup from pressure profile
        val ecdMap: Map<Double, Double> = result?.pressureProfile
            ?.associate { it.depth to it.ecd }
            ?: emptyMap()

        val stations = profile.deviationSurvey
        return if (stations.isEmpty()) {
            MinimumCurvatureCalc.vertical(profile.totalDepth)
        } else {
            MinimumCurvatureCalc.compute(stations, ecdMap)
        }
    }
}