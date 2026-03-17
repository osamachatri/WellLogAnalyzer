package com.oussama_chatri.feature.simulation.data.local

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class SimulationResultProto(
    @ProtoNumber(1)  val wellId: String = "",
    @ProtoNumber(2)  val wellName: String = "",
    @ProtoNumber(3)  val timestamp: Long = 0L,
    @ProtoNumber(4)  val pressureProfile: List<PressurePointProto> = emptyList(),
    @ProtoNumber(5)  val velocityProfile: List<VelocityProfileProto> = emptyList(),
    @ProtoNumber(6)  val bitPressureDrop: Double = 0.0,
    @ProtoNumber(7)  val hydraulicHorsepower: Double = 0.0,
    @ProtoNumber(8)  val nozzleVelocity: Double = 0.0,
    @ProtoNumber(9)  val impactForce: Double = 0.0,
    @ProtoNumber(10) val hsi: Double = 0.0,
    @ProtoNumber(11) val surgePressure: Double = 0.0,
    @ProtoNumber(12) val swabPressure: Double = 0.0,
    @ProtoNumber(13) val formationZones: List<SimFormationZoneProto> = emptyList()
)

@Serializable
data class PressurePointProto(
    @ProtoNumber(1) val depth: Double = 0.0,
    @ProtoNumber(2) val hydrostaticPressure: Double = 0.0,
    @ProtoNumber(3) val annularPressureLoss: Double = 0.0,
    @ProtoNumber(4) val ecd: Double = 0.0,
    @ProtoNumber(5) val porePressure: Double = 0.0,
    @ProtoNumber(6) val fractureGradientPressure: Double = 0.0
)

@Serializable
data class VelocityProfileProto(
    @ProtoNumber(1) val depth: Double = 0.0,
    @ProtoNumber(2) val annularVelocity: Double = 0.0,
    @ProtoNumber(3) val pipeVelocity: Double = 0.0,
    @ProtoNumber(4) val flowRegime: Int = 0
)

@Serializable
data class SimFormationZoneProto(
    @ProtoNumber(1) val id: String = "",
    @ProtoNumber(2) val zoneName: String = "",
    @ProtoNumber(3) val topDepth: Double = 0.0,
    @ProtoNumber(4) val bottomDepth: Double = 0.0,
    @ProtoNumber(5) val porePressureGradient: Double = 0.0,
    @ProtoNumber(6) val fractureGradient: Double = 0.0,
    @ProtoNumber(7) val lithology: Int = 0
)