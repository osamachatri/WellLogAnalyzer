package com.oussama_chatri.feature.wellinput.data.local

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class WellProfileProto(
    @ProtoNumber(1) val id: String = "",
    @ProtoNumber(2) val wellName: String = "",
    @ProtoNumber(3) val totalDepth: Double = 0.0,
    @ProtoNumber(4) val casingOd: Double = 0.0,
    @ProtoNumber(5) val casingId: Double = 0.0,
    @ProtoNumber(6) val drillString: DrillStringProto = DrillStringProto(),
    @ProtoNumber(7) val bitParameters: BitParametersProto = BitParametersProto(),
    @ProtoNumber(8) val fluidProperties: FluidPropertiesProto = FluidPropertiesProto(),
    @ProtoNumber(9) val formationZones: List<FormationZoneProto> = emptyList(),
    @ProtoNumber(10) val deviationSurvey: List<SurveyStationProto> = emptyList(),
    @ProtoNumber(11) val lastModified: Long = 0L
)

@Serializable
data class DrillStringProto(
    @ProtoNumber(1) val drillPipeOd: Double = 0.0,
    @ProtoNumber(2) val drillPipeId: Double = 0.0,
    @ProtoNumber(3) val drillCollarOd: Double = 0.0,
    @ProtoNumber(4) val drillCollarLength: Double = 0.0,
    @ProtoNumber(5) val sections: List<DrillStringSectionProto> = emptyList()
)

@Serializable
data class DrillStringSectionProto(
    @ProtoNumber(1) val name: String = "",
    @ProtoNumber(2) val od: Double = 0.0,
    @ProtoNumber(3) val id: Double = 0.0,
    @ProtoNumber(4) val length: Double = 0.0,
    @ProtoNumber(5) val weightPerFoot: Double = 0.0
)

@Serializable
data class BitParametersProto(
    @ProtoNumber(1) val bitSize: Double = 0.0,
    @ProtoNumber(2) val nozzleCount: Int = 0,
    @ProtoNumber(3) val nozzleSizes: List<Double> = emptyList()
)

@Serializable
data class FluidPropertiesProto(
    @ProtoNumber(1)  val mudWeight: Double = 0.0,
    @ProtoNumber(2)  val flowRate: Double = 0.0,
    @ProtoNumber(3)  val surfaceTemperature: Double = 70.0,
    @ProtoNumber(4)  val bottomholeTemperature: Double = 200.0,
    @ProtoNumber(5)  val rheologyModel: Int = 0,   // 0 = BINGHAM, 1 = POWER_LAW
    @ProtoNumber(6)  val plasticViscosity: Double = 0.0,
    @ProtoNumber(7)  val yieldPoint: Double = 0.0,
    @ProtoNumber(8)  val flowBehaviorIndex: Double = 0.8,
    @ProtoNumber(9)  val consistencyIndex: Double = 0.0,
    @ProtoNumber(10) val mudType: Int = 0,          // 0 = WATER_BASED, 1 = OIL_BASED, 2 = SYNTHETIC
    @ProtoNumber(11) val solidsContent: Double = 0.0,
    @ProtoNumber(12) val pH: Double = 9.0
)

@Serializable
data class FormationZoneProto(
    @ProtoNumber(1) val id: String = "",
    @ProtoNumber(2) val zoneName: String = "",
    @ProtoNumber(3) val topDepth: Double = 0.0,
    @ProtoNumber(4) val bottomDepth: Double = 0.0,
    @ProtoNumber(5) val porePressureGradient: Double = 0.0,
    @ProtoNumber(6) val fractureGradient: Double = 0.0,
    @ProtoNumber(7) val lithology: Int = 0  // ordinal of Lithology enum
)

@Serializable
data class SurveyStationProto(
    @ProtoNumber(1) val id: String = "",
    @ProtoNumber(2) val measuredDepth: Double = 0.0,
    @ProtoNumber(3) val inclination: Double = 0.0,
    @ProtoNumber(4) val azimuth: Double = 0.0,
    @ProtoNumber(5) val tvd: Double = 0.0,
    @ProtoNumber(6) val northing: Double = 0.0,
    @ProtoNumber(7) val easting: Double = 0.0
)