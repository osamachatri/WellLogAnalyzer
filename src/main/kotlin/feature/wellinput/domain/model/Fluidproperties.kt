package com.oussama_chatri.feature.wellinput.domain.model

data class FluidProperties(
    val mudWeight: Double,
    val flowRate: Double,
    val surfaceTemperature: Double,
    val bottomholeTemperature: Double,
    val rheologyModel: RheologyModel,
    val plasticViscosity: Double,
    val yieldPoint: Double,
    val flowBehaviorIndex: Double,
    val consistencyIndex: Double,
    val mudType: MudType,
    val solidsContent: Double,
    val pH: Double
) {
    companion object {
        fun empty() = FluidProperties(
            mudWeight               = 0.0,
            flowRate                = 0.0,
            surfaceTemperature      = 70.0,
            bottomholeTemperature   = 200.0,
            rheologyModel           = RheologyModel.BINGHAM_PLASTIC,
            plasticViscosity        = 0.0,
            yieldPoint              = 0.0,
            flowBehaviorIndex       = 0.8,
            consistencyIndex        = 0.0,
            mudType                 = MudType.WATER_BASED,
            solidsContent           = 0.0,
            pH                      = 9.0
        )
    }
}

/** Rheology model selection for pressure-drop calculations. */
enum class RheologyModel {
    BINGHAM_PLASTIC,
    POWER_LAW
}

/** Mud base fluid type. */
enum class MudType(val displayName: String) {
    WATER_BASED("Water-Based"),
    OIL_BASED("Oil-Based"),
    SYNTHETIC("Synthetic")
}