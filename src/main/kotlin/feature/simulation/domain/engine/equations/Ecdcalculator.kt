package com.oussama_chatri.feature.simulation.domain.engine.equations

/**
 * Calculates Equivalent Circulating Density (ECD) and related pressures.
 *
 * ECD represents the effective mud weight that the formation experiences
 * while circulating, accounting for annular friction losses.
 * Formula: ECD = MW + APL / (0.052 × TVD)
 */
object ECDCalculator {

    /**
     * ECD in ppg at a given depth.
     *
     * @param mudWeightPpg         Static mud weight in ppg
     * @param annularPressureLoss  Cumulative APL from surface to this depth in psi
     * @param tvdFt                True vertical depth in feet
     */
    fun ecd(
        mudWeightPpg: Double,
        annularPressureLoss: Double,
        tvdFt: Double
    ): Double {
        if (tvdFt <= 0.0) return mudWeightPpg
        return mudWeightPpg + annularPressureLoss / (0.052 * tvdFt)
    }

    /**
     * Interpolates the pore pressure gradient at a given depth
     * from the ordered list of formation zones.
     * Returns 8.6 ppg (fresh water equivalent) as a safe default if no zone matches.
     */
    fun porePressureGradientAt(
        depth: Double,
        formationZones: List<com.oussama_chatri.feature.wellinput.domain.model.FormationZone>
    ): Double {
        val zone = formationZones.firstOrNull { depth >= it.topDepth && depth <= it.bottomDepth }
            ?: formationZones.lastOrNull { depth > it.bottomDepth }
        return zone?.porePressureGradient ?: 8.6
    }

    /**
     * Interpolates the fracture gradient at a given depth from formation zones.
     * Returns 15.0 ppg as a conservative default if no zone matches.
     */
    fun fractureGradientAt(
        depth: Double,
        formationZones: List<com.oussama_chatri.feature.wellinput.domain.model.FormationZone>
    ): Double {
        val zone = formationZones.firstOrNull { depth >= it.topDepth && depth <= it.bottomDepth }
            ?: formationZones.lastOrNull { depth > it.bottomDepth }
        return zone?.fractureGradient ?: 15.0
    }
}