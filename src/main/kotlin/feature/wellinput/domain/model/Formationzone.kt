package com.oussama_chatri.feature.wellinput.domain.model

data class FormationZone(
    val id: String,
    val zoneName: String,
    val topDepth: Double,
    val bottomDepth: Double,
    val porePressureGradient: Double,
    val fractureGradient: Double,
    val lithology: Lithology
) {
    /** Thickness of this zone in feet. */
    val thickness: Double get() = bottomDepth - topDepth

    companion object {
        fun empty(id: String = java.util.UUID.randomUUID().toString()) = FormationZone(
            id                    = id,
            zoneName              = "",
            topDepth              = 0.0,
            bottomDepth           = 0.0,
            porePressureGradient  = 8.6,
            fractureGradient      = 10.0,
            lithology             = Lithology.SHALE
        )
    }
}

/** Lithology classification for colour-coding and labelling. */
enum class Lithology(val displayName: String) {
    SHALE("Shale"),
    SANDSTONE("Sandstone"),
    LIMESTONE("Limestone"),
    SALT("Salt"),
    DOLOMITE("Dolomite"),
    ANHYDRITE("Anhydrite")
}