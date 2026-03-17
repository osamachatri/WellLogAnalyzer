package com.oussama_chatri.feature.simulation.domain.engine.equations

import com.oussama_chatri.feature.wellinput.domain.model.BitParameters
import kotlin.math.sqrt

/**
 * Calculates all bit hydraulics parameters per API RP 13D.
 *
 * Field unit formulas:
 *   - Flow rate in gpm
 *   - Mud weight in ppg
 *   - Nozzle sizes in 1/32 in
 *   - Pressure in psi
 *   - Velocity in ft/s
 *   - HHP in hydraulic horsepower
 *   - Impact force in lbf
 */
object BitHydraulicsCalculator {

    private const val NOZZLE_DISCHARGE_COEFF = 0.95

    /**
     * Bit pressure drop in psi.
     * ΔP_bit = ρ × q² / (12031 × C_d² × TFA²)
     *
     * @param mudWeightPpg   Mud weight in ppg
     * @param flowRateGpm    Flow rate in gpm
     * @param bit            Bit parameters (supplies TFA)
     */
    fun bitPressureDrop(
        mudWeightPpg: Double,
        flowRateGpm: Double,
        bit: BitParameters
    ): Double {
        val tfa = bit.totalFlowArea
        if (tfa <= 0.0) return 0.0
        return (mudWeightPpg * flowRateGpm * flowRateGpm) /
                (12031.0 * NOZZLE_DISCHARGE_COEFF * NOZZLE_DISCHARGE_COEFF * tfa * tfa)
    }

    /**
     * Nozzle exit velocity in ft/s.
     * v_n = 0.3208 × q / (C_d × TFA)
     */
    fun nozzleVelocity(flowRateGpm: Double, bit: BitParameters): Double {
        val tfa = bit.totalFlowArea
        if (tfa <= 0.0) return 0.0
        return (0.3208 * flowRateGpm) / (NOZZLE_DISCHARGE_COEFF * tfa)
    }

    /**
     * Hydraulic horsepower at the bit in hp.
     * HHP = (ΔP_bit × q) / 1714
     */
    fun hydraulicHorsepower(bitPressureDropPsi: Double, flowRateGpm: Double): Double =
        (bitPressureDropPsi * flowRateGpm) / 1714.0

    /**
     * Hydraulic Horsepower per Square Inch (HSI) in hp/in².
     * HSI = HHP / A_bit
     * API RP 13D target range: 1.0–1.5 hp/in²
     */
    fun hsi(hydraulicHorsepowerHp: Double, bit: BitParameters): Double {
        val bitArea = bit.bitArea
        if (bitArea <= 0.0) return 0.0
        return hydraulicHorsepowerHp / bitArea
    }

    /**
     * Jet impact force in lbf.
     * F_i = 0.000518 × ρ × q × v_n
     */
    fun impactForce(
        mudWeightPpg: Double,
        flowRateGpm: Double,
        nozzleVelocityFtS: Double
    ): Double = 0.000518 * mudWeightPpg * flowRateGpm * nozzleVelocityFtS

    /**
     * Convenience: computes all bit hydraulics values in one call.
     */
    data class BitHydraulicsResult(
        val bitPressureDrop: Double,
        val nozzleVelocity: Double,
        val hydraulicHorsepower: Double,
        val hsi: Double,
        val impactForce: Double
    )

    fun computeAll(
        mudWeightPpg: Double,
        flowRateGpm: Double,
        bit: BitParameters
    ): BitHydraulicsResult {
        val bpd = bitPressureDrop(mudWeightPpg, flowRateGpm, bit)
        val vn  = nozzleVelocity(flowRateGpm, bit)
        val hhp = hydraulicHorsepower(bpd, flowRateGpm)
        val h   = hsi(hhp, bit)
        val fi  = impactForce(mudWeightPpg, flowRateGpm, vn)
        return BitHydraulicsResult(bpd, vn, hhp, h, fi)
    }
}