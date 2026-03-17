package com.oussama_chatri.feature.simulation.domain.model

import com.oussama_chatri.feature.wellinput.domain.model.FormationZone

/**
 * Complete output of one simulation run.
 * Consumed by Charts2D, Viewer3D, and Reports.
 */
data class SimulationResult(
    val wellId: String,
    val wellName: String,
    val timestamp: Long = System.currentTimeMillis(),

    // Depth profiles
    val pressureProfile: List<PressurePoint>,
    val velocityProfile: List<VelocityProfile>,

    // Bit hydraulics (single values, computed at surface conditions)
    val bitPressureDrop: Double,
    val hydraulicHorsepower: Double,
    val nozzleVelocity: Double,
    val impactForce: Double,
    val hsi: Double,

    // Surge / swab (computed at TD)
    val surgePressure: Double,
    val swabPressure: Double,

    // Formation context carried for chart overlays
    val formationZones: List<FormationZone>
) {
    val maxEcd: Double get() = pressureProfile.maxOfOrNull { it.ecd } ?: 0.0
    val maxAnnularPressureLoss: Double get() = pressureProfile.maxOfOrNull { it.annularPressureLoss } ?: 0.0
    val totalSurfacePressure: Double get() = bitPressureDrop + maxAnnularPressureLoss
    val hydrostaticAtTd: Double get() = pressureProfile.lastOrNull()?.hydrostaticPressure ?: 0.0

    val isEcdSafe: Boolean get() = pressureProfile.all { it.isEcdSafe }
}
