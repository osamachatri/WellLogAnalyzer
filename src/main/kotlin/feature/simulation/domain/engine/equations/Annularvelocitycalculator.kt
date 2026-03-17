package com.oussama_chatri.feature.simulation.domain.engine.equations

import com.oussama_chatri.feature.simulation.domain.model.FlowRegime
import com.oussama_chatri.feature.simulation.domain.model.VelocityProfile
import kotlin.math.PI

/**
 * Calculates annular and pipe velocities and determines the flow regime
 * using the Reynolds number with the Bingham Plastic or Power Law model.
 *
 * All inputs in API/field units:
 *   - diameters in inches
 *   - flow rate in gpm
 *   - mud weight in ppg
 *   - PV in cP, YP in lb/100ft²
 */
object AnnularVelocityCalculator {

    private const val GPM_TO_CU_FT_PER_MIN = 1.0 / 7.48052

    /**
     * Annular velocity in ft/min.
     *
     * @param flowRateGpm  Circulation rate in gal/min
     * @param casingIdIn   Inner diameter of the annulus outer wall in inches
     * @param pipeOdIn     Outer diameter of the drill pipe / collar in inches
     */
    fun annularVelocity(
        flowRateGpm: Double,
        casingIdIn: Double,
        pipeOdIn: Double
    ): Double {
        val casingIdFt = casingIdIn / 12.0
        val pipeOdFt   = pipeOdIn  / 12.0
        val areaSqFt   = (PI / 4.0) * (casingIdFt * casingIdFt - pipeOdFt * pipeOdFt)
        if (areaSqFt <= 0.0) return 0.0
        val flowCuFtPerMin = flowRateGpm * GPM_TO_CU_FT_PER_MIN
        return flowCuFtPerMin / areaSqFt
    }

    /**
     * Pipe (bore) velocity in ft/min.
     *
     * @param flowRateGpm  Circulation rate in gal/min
     * @param pipeIdIn     Inner diameter of the drill pipe in inches
     */
    fun pipeVelocity(flowRateGpm: Double, pipeIdIn: Double): Double {
        val pipeIdFt   = pipeIdIn / 12.0
        val areaSqFt   = (PI / 4.0) * pipeIdFt * pipeIdFt
        if (areaSqFt <= 0.0) return 0.0
        val flowCuFtPerMin = flowRateGpm * GPM_TO_CU_FT_PER_MIN
        return flowCuFtPerMin / areaSqFt
    }

    /**
     * Bingham Plastic annular Reynolds number.
     * Re_a = (109 × ρ × v_a × (d_h - d_p)) / PV
     * where d_h and d_p are in inches, v_a in ft/min, ρ in ppg, PV in cP.
     */
    fun binghamReynoldsAnnular(
        mudWeightPpg: Double,
        annularVelocityFtMin: Double,
        casingIdIn: Double,
        pipeOdIn: Double,
        plasticViscosityCp: Double
    ): Double {
        if (plasticViscosityCp <= 0.0) return 0.0
        return (109.0 * mudWeightPpg * annularVelocityFtMin * (casingIdIn - pipeOdIn)) /
                plasticViscosityCp
    }

    /**
     * Determines flow regime based on the Bingham Plastic critical velocity approach.
     * Laminar: Re < 2100, Turbulent: Re > 3000, else Transitional.
     */
    fun flowRegime(reynoldsNumber: Double): FlowRegime = when {
        reynoldsNumber < 2100 -> FlowRegime.LAMINAR
        reynoldsNumber > 3000 -> FlowRegime.TURBULENT
        else                  -> FlowRegime.TRANSITIONAL
    }

    /**
     * Builds a full [VelocityProfile] for a given depth station.
     */
    fun compute(
        depth: Double,
        flowRateGpm: Double,
        casingIdIn: Double,
        pipeOdIn: Double,
        pipeIdIn: Double,
        mudWeightPpg: Double,
        plasticViscosityCp: Double
    ): VelocityProfile {
        val va = annularVelocity(flowRateGpm, casingIdIn, pipeOdIn)
        val vp = pipeVelocity(flowRateGpm, pipeIdIn)
        val re = binghamReynoldsAnnular(mudWeightPpg, va, casingIdIn, pipeOdIn, plasticViscosityCp)
        return VelocityProfile(
            depth           = depth,
            annularVelocity = va,
            pipeVelocity    = vp,
            flowRegime      = flowRegime(re)
        )
    }
}