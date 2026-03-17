package com.oussama_chatri.feature.wellinput.domain.model

data class DrillString(
    val drillPipeOd: Double,
    val drillPipeId: Double,
    val drillCollarOd: Double,
    val drillCollarLength: Double,
    val sections: List<DrillStringSection>
) {
    companion object {
        fun empty() = DrillString(
            drillPipeOd       = 0.0,
            drillPipeId       = 0.0,
            drillCollarOd     = 0.0,
            drillCollarLength = 0.0,
            sections          = emptyList()
        )
    }
}

data class DrillStringSection(
    val name: String,
    val od: Double,
    val id: Double,
    val length: Double,
    val weightPerFoot: Double
)