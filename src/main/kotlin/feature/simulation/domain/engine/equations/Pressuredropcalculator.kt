package com.oussama_chatri.feature.simulation.domain.engine.equations

import com.oussama_chatri.feature.simulation.domain.model.FlowRegime
import com.oussama_chatri.feature.wellinput.domain.model.RheologyModel
import kotlin.math.pow

/**
 * Calculates annular pressure loss per unit length (psi/ft) at a given depth station.
 *
 * Supports both Bingham Plastic and Power Law rheology models following
 * standard API RP 13D / Bourgoyne et al. methodology.
 *
 * All inputs in API field units.
 */
object PressureDropCalculator {

    /**
     * Returns the annular pressure loss in psi for a given [intervalLength] in feet.
     *
     * @param flowRateGpm         Circulation rate in gal/min
     * @param casingIdIn          Annulus outer diameter in inches
     * @param pipeOdIn            Drill string outer diameter in inches
     * @param mudWeightPpg        Mud weight in ppg
     * @param plasticViscosityCp  PV in cP (Bingham only)
     * @param yieldPointLb100     YP in lb/100ft² (Bingham only)
     * @param flowBehaviorIndex   n (Power Law only)
     * @param consistencyIndex    K in eq.cP (Power Law only)
     * @param rheologyModel       Which model to apply
     * @param intervalLength      Length of this annular section in ft
     */
    fun annularPressureLoss(
        flowRateGpm: Double,
        casingIdIn: Double,
        pipeOdIn: Double,
        mudWeightPpg: Double,
        plasticViscosityCp: Double,
        yieldPointLb100: Double,
        flowBehaviorIndex: Double,
        consistencyIndex: Double,
        rheologyModel: RheologyModel,
        intervalLength: Double
    ): Double {
        if (intervalLength <= 0.0) return 0.0

        return when (rheologyModel) {
            RheologyModel.BINGHAM_PLASTIC -> binghamAnnularLoss(
                flowRateGpm, casingIdIn, pipeOdIn,
                mudWeightPpg, plasticViscosityCp, yieldPointLb100, intervalLength
            )
            RheologyModel.POWER_LAW -> powerLawAnnularLoss(
                flowRateGpm, casingIdIn, pipeOdIn,
                mudWeightPpg, flowBehaviorIndex, consistencyIndex, intervalLength
            )
        }
    }

    // Bingham Plastic model

    private fun binghamAnnularLoss(
        flowRateGpm: Double,
        casingIdIn: Double,
        pipeOdIn: Double,
        mudWeightPpg: Double,
        pv: Double,
        yp: Double,
        length: Double
    ): Double {
        val va = AnnularVelocityCalculator.annularVelocity(flowRateGpm, casingIdIn, pipeOdIn)
        val dh = casingIdIn - pipeOdIn

        val re = AnnularVelocityCalculator.binghamReynoldsAnnular(
            mudWeightPpg, va, casingIdIn, pipeOdIn, pv
        )

        return when (AnnularVelocityCalculator.flowRegime(re)) {
            FlowRegime.LAMINAR -> {
                // Bourgoyne laminar Bingham annular: (PV × va) / (1000 × dh²) + yp / (200 × dh)
                val term1 = (pv * va) / (1000.0 * dh * dh)
                val term2 = yp / (200.0 * dh)
                (term1 + term2) * length
            }
            FlowRegime.TURBULENT -> {
                // Turbulent Bingham: 0.000027 × ρ^0.8 × va^1.8 × pv^0.2 / dh^1.2
                val lossPerFt = 0.000027 * mudWeightPpg.pow(0.8) * va.pow(1.8) * pv.pow(0.2) /
                        dh.pow(1.2)
                lossPerFt * length
            }
            FlowRegime.TRANSITIONAL -> {
                // Interpolate between laminar and turbulent at the transition midpoint
                val laminar = run {
                    val t1 = (pv * va) / (1000.0 * dh * dh)
                    val t2 = yp / (200.0 * dh)
                    (t1 + t2) * length
                }
                val turbulent = 0.000027 * mudWeightPpg.pow(0.8) * va.pow(1.8) * pv.pow(0.2) /
                        dh.pow(1.2) * length
                val fraction = (re - 2100.0) / (3000.0 - 2100.0)
                laminar + fraction * (turbulent - laminar)
            }
        }
    }

    // Power Law model

    private fun powerLawAnnularLoss(
        flowRateGpm: Double,
        casingIdIn: Double,
        pipeOdIn: Double,
        mudWeightPpg: Double,
        n: Double,
        k: Double,
        length: Double
    ): Double {
        val va = AnnularVelocityCalculator.annularVelocity(flowRateGpm, casingIdIn, pipeOdIn)
        val dh = casingIdIn - pipeOdIn
        if (dh <= 0.0 || n <= 0.0) return 0.0

        // Effective viscosity for Re calculation (Power Law)
        val muEff = (k / 144.0) * ((96.0 * va / dh).pow(n - 1)) *
                ((2.0 + 1.0 / n) / 0.0208).pow(n)
        val re = (109.0 * mudWeightPpg * va * dh) / (muEff.coerceAtLeast(0.001))

        return when (AnnularVelocityCalculator.flowRegime(re)) {
            FlowRegime.LAMINAR -> {
                // Power Law laminar annular pressure gradient
                val lossPerFt = (k / 144.0) * ((144.0 * va / dh).pow(n)) *
                        ((2.0 + 1.0 / n) / 0.0208).pow(n) / dh
                lossPerFt * length
            }
            FlowRegime.TURBULENT, FlowRegime.TRANSITIONAL -> {
                // Turbulent Power Law approximation
                val lossPerFt = 0.000027 * mudWeightPpg.pow(0.8) * va.pow(1.8) * muEff.pow(0.2) /
                        dh.pow(1.2)
                lossPerFt * length
            }
        }
    }

    /**
     * Hydrostatic pressure in psi at a given TVD.
     * Formula: P = 0.052 × mudWeight(ppg) × tvd(ft)
     */
    fun hydrostaticPressure(mudWeightPpg: Double, tvdFt: Double): Double =
        0.052 * mudWeightPpg * tvdFt
}