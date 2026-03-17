package com.oussama_chatri.feature.simulation.domain.engine.equations

import com.oussama_chatri.feature.simulation.domain.model.FlowRegime
import com.oussama_chatri.feature.wellinput.domain.model.RheologyModel
import kotlin.math.pow

/**
 * Calculates annular pressure loss in psi for a given interval length.
 *
 * Follows Bourgoyne et al. "Applied Drilling Engineering" (SPE Vol. 2)
 * and API RP 13D field-unit conventions:
 *   - va  in ft/min
 *   - dh  in inches  (dh = casingID - pipeOD)
 *   - ρ   in ppg
 *   - PV  in cP
 *   - YP  in lb/100ft²
 *   - ΔP  in psi
 */
object PressureDropCalculator {

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

    // ── Bingham Plastic ──────────────────────────────────────────────────────

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
        if (dh <= 0.0) return 0.0

        val re = AnnularVelocityCalculator.binghamReynoldsAnnular(
            mudWeightPpg, va, casingIdIn, pipeOdIn, pv
        )

        return when (AnnularVelocityCalculator.flowRegime(re)) {
            FlowRegime.LAMINAR -> {
                // Bourgoyne Eq 4.34 (field units, va in ft/min):
                // dP/dL = PV×va / (60000×dh²)  +  YP / (200×dh)
                val term1 = (pv * va) / (60000.0 * dh * dh)
                val term2 = yp / (200.0 * dh)
                (term1 + term2) * length
            }
            FlowRegime.TURBULENT -> {
                // Bourgoyne turbulent annular (va in ft/min, field units):
                // dP/dL = 0.00000518 × ρ^0.8 × va^1.8 × PV^0.2 / dh^1.2
                val lossPerFt = 0.00000518 * mudWeightPpg.pow(0.8) *
                        va.pow(1.8) * pv.pow(0.2) / dh.pow(1.2)
                lossPerFt * length
            }
            FlowRegime.TRANSITIONAL -> {
                val laminar = run {
                    val t1 = (pv * va) / (60000.0 * dh * dh)
                    val t2 = yp / (200.0 * dh)
                    (t1 + t2) * length
                }
                val turbulent = 0.00000518 * mudWeightPpg.pow(0.8) *
                        va.pow(1.8) * pv.pow(0.2) / dh.pow(1.2) * length
                val fraction = (re - 2100.0) / (3000.0 - 2100.0)
                laminar + fraction * (turbulent - laminar)
            }
        }
    }

    // ── Power Law ────────────────────────────────────────────────────────────

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

        // Effective viscosity in cP for Reynolds number (va in ft/min → convert to ft/s ÷60)
        val vaFtS  = va / 60.0
        val muEff  = (k * ((144.0 * vaFtS / dh).pow(n - 1)) *
                ((2.0 + 1.0 / n) / 0.0208).pow(n)).coerceAtLeast(0.001)

        val re = (109.0 * mudWeightPpg * va * dh) / muEff

        return when (AnnularVelocityCalculator.flowRegime(re)) {
            FlowRegime.LAMINAR -> {
                // Power Law laminar annular (Bourgoyne, va in ft/min):
                // dP/dL = [K/(144×dh)] × [144×va/(dh×(2+1/n)/0.0208)]^n
                val bracket = (144.0 * vaFtS) / (dh * (2.0 + 1.0 / n) / 0.0208)
                val lossPerFt = (k / (144.0 * dh)) * bracket.pow(n)
                lossPerFt * length
            }
            FlowRegime.TURBULENT, FlowRegime.TRANSITIONAL -> {
                val lossPerFt = 0.00000518 * mudWeightPpg.pow(0.8) *
                        va.pow(1.8) * muEff.pow(0.2) / dh.pow(1.2)
                lossPerFt * length
            }
        }
    }

    /**
     * Hydrostatic pressure in psi.
     * P = 0.052 × mudWeight(ppg) × tvd(ft)
     */
    fun hydrostaticPressure(mudWeightPpg: Double, tvdFt: Double): Double =
        0.052 * mudWeightPpg * tvdFt
}