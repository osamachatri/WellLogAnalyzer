package com.oussama_chatri.feature.wellinput.domain.model

data class WellProfile(
    val id: String,
    val wellName: String,
    val totalDepth: Double,
    val casingOd: Double,
    val casingId: Double,
    val drillString: DrillString,
    val bitParameters: BitParameters,
    val fluidProperties: FluidProperties,
    val formationZones: List<FormationZone>,
    val deviationSurvey: List<SurveyStation>,
    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        /** Returns a blank WellProfile with a fresh UUID — suitable for "New Well". */
        fun empty(): WellProfile = WellProfile(
            id               = java.util.UUID.randomUUID().toString(),
            wellName         = "",
            totalDepth       = 0.0,
            casingOd         = 0.0,
            casingId         = 0.0,
            drillString      = DrillString.empty(),
            bitParameters    = BitParameters.empty(),
            fluidProperties  = FluidProperties.empty(),
            formationZones   = emptyList(),
            deviationSurvey  = emptyList()
        )
    }
}