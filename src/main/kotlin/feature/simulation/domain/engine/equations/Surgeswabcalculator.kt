package com.oussama_chatri.feature.simulation.domain.engine.equations

import com.oussama_chatri.feature.wellinput.domain.model.RheologyModel
import kotlin.math.pow

/**
 * Estimates surge and swab pressures induced by tripping the drill string
 * in or out of the wellbore, using the Bourgoyne et al. simplified method.
 *
 * Surge pressure acts when running pipe in (adds to hydrostatic).
 * Swab pressure acts when pulling pipe out (reduces effective hydrostatic).
 *
 * All values in API field units.
 */
object SurgeSwabCalculator {

    /**
     * Simplified surge/swab pressure estimate in psi.
     *
     * Uses an average pipe velocity based on trip speed and annular geometry.
     * The same formula applies for both surge (positive) and swab (negative offset).
     *
     * @param tripSpeedFtMin   Tripping speed in ft/min (typically 30–120 ft/min)
     * @param mudWeightPpg     Mud weight in ppg
     * @param casingIdIn       Casing / open-hole inner diameter in inches
     * @param pipeOdIn         Drill string outer diameter in inches
     * @param pipeIdIn         Drill string inner diameter in inches (open-ended assumed)
     * @param plasticViscosityCp  PV in cP
     * @param yieldPointLb100  YP in lb/100ft²
     * @param rheologyModel    Bingham or Power Law
     * @param tvdFt            True vertical depth to compute over in feet
     */
    fun surgeSwabPressure(
        tripSpeedFtMin: Double,
        mudWeightPpg: Double,
        casingIdIn: Double,
        pipeOdIn: Double,
        pipeIdIn: Double,
        plasticViscosityCp: Double,
        yieldPointLb100: Double,
        rheologyModel: RheologyModel,
        tvdFt: Double
    ): SurgeSwabResult {
        if (tvdFt <= 0.0) return SurgeSwabResult(0.0, 0.0)

        val dh = casingIdIn - pipeOdIn
        if (dh <= 0.0) return SurgeSwabResult(0.0, 0.0)

        // Effective average velocity in annulus due to pipe movement (closed-ended approximation)
        val pipeArea   = (Math.PI / 4.0) * (pipeOdIn / 12.0).pow(2)
        val annArea    = (Math.PI / 4.0) * ((casingIdIn / 12.0).pow(2) - (pipeOdIn / 12.0).pow(2))
        val clampedArea = annArea.coerceAtLeast(0.001)
        val effectiveVelocity = tripSpeedFtMin * (pipeArea / clampedArea)

        // Pressure gradient in psi/ft (Bingham laminar approximation)
        val pressureGradPerFt = when (rheologyModel) {
            RheologyModel.BINGHAM_PLASTIC -> {
                val term1 = (plasticViscosityCp * effectiveVelocity) / (1000.0 * dh * dh)
                val term2 = yieldPointLb100 / (200.0 * dh)
                term1 + term2
            }
            RheologyModel.POWER_LAW -> {
                // Simplified Power Law fallback using Bingham with PV only
                (plasticViscosityCp * effectiveVelocity) / (1000.0 * dh * dh)
            }
        }

        val pressurePsi = pressureGradPerFt * tvdFt
        return SurgeSwabResult(surgePsi = pressurePsi, swabPsi = pressurePsi)
    }

    data class SurgeSwabResult(
        val surgePsi: Double,
        val swabPsi: Double
    )
}