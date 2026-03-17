package com.oussama_chatri.feature.wellinput.domain.model

data class SurveyStation(
    val id: String,
    val measuredDepth: Double,
    val inclination: Double,
    val azimuth: Double,
    val tvd: Double? = null,
    val northing: Double? = null,
    val easting: Double? = null
) {
    companion object {
        fun empty(id: String = java.util.UUID.randomUUID().toString()) = SurveyStation(
            id            = id,
            measuredDepth = 0.0,
            inclination   = 0.0,
            azimuth       = 0.0
        )
    }
}