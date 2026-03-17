package com.oussama_chatri.feature.simulation.domain.model

import com.oussama_chatri.feature.wellinput.domain.model.BitParameters
import com.oussama_chatri.feature.wellinput.domain.model.DrillString
import com.oussama_chatri.feature.wellinput.domain.model.FluidProperties
import com.oussama_chatri.feature.wellinput.domain.model.FormationZone
import com.oussama_chatri.feature.wellinput.domain.model.SurveyStation

/**
 * All parameters required to run a single hydraulics simulation pass.
 * Assembled from a validated [WellProfile] before handing off to the engine.
 */
data class SimulationInput(
    val wellId: String,
    val wellName: String,
    val totalDepth: Double,
    val casingOd: Double,
    val casingId: Double,
    val drillString: DrillString,
    val bitParameters: BitParameters,
    val fluidProperties: FluidProperties,
    val formationZones: List<FormationZone>,
    val deviationSurvey: List<SurveyStation>,
    val depthStep: Double = 100.0,
    val flowRateOverride: Double? = null
) {
    val effectiveFlowRate: Double
        get() = flowRateOverride ?: fluidProperties.flowRate
}