package com.oussama_chatri.feature.wellinput.data.mapper

import com.oussama_chatri.feature.wellinput.data.local.*
import com.oussama_chatri.feature.wellinput.domain.model.*

/**
 * Bidirectional mapper between domain models and Proto DTOs.
 * All mapping logic lives here — the repository and store stay clean.
 */
object WellProfileMapper {

    // Domain → Proto

    fun toProto(domain: WellProfile): WellProfileProto = WellProfileProto(
        id               = domain.id,
        wellName         = domain.wellName,
        totalDepth       = domain.totalDepth,
        casingOd         = domain.casingOd,
        casingId         = domain.casingId,
        drillString      = toProto(domain.drillString),
        bitParameters    = toProto(domain.bitParameters),
        fluidProperties  = toProto(domain.fluidProperties),
        formationZones   = domain.formationZones.map { toProto(it) },
        deviationSurvey  = domain.deviationSurvey.map { toProto(it) },
        lastModified     = domain.lastModified
    )

    private fun toProto(ds: DrillString): DrillStringProto = DrillStringProto(
        drillPipeOd       = ds.drillPipeOd,
        drillPipeId       = ds.drillPipeId,
        drillCollarOd     = ds.drillCollarOd,
        drillCollarLength = ds.drillCollarLength,
        sections          = ds.sections.map { toProto(it) }
    )

    private fun toProto(s: DrillStringSection): DrillStringSectionProto = DrillStringSectionProto(
        name         = s.name,
        od           = s.od,
        id           = s.id,
        length       = s.length,
        weightPerFoot = s.weightPerFoot
    )

    private fun toProto(bit: BitParameters): BitParametersProto = BitParametersProto(
        bitSize      = bit.bitSize,
        nozzleCount  = bit.nozzleCount,
        nozzleSizes  = bit.nozzleSizes
    )

    private fun toProto(f: FluidProperties): FluidPropertiesProto = FluidPropertiesProto(
        mudWeight               = f.mudWeight,
        flowRate                = f.flowRate,
        surfaceTemperature      = f.surfaceTemperature,
        bottomholeTemperature   = f.bottomholeTemperature,
        rheologyModel           = f.rheologyModel.ordinal,
        plasticViscosity        = f.plasticViscosity,
        yieldPoint              = f.yieldPoint,
        flowBehaviorIndex       = f.flowBehaviorIndex,
        consistencyIndex        = f.consistencyIndex,
        mudType                 = f.mudType.ordinal,
        solidsContent           = f.solidsContent,
        pH                      = f.pH
    )

    private fun toProto(z: FormationZone): FormationZoneProto = FormationZoneProto(
        id                    = z.id,
        zoneName              = z.zoneName,
        topDepth              = z.topDepth,
        bottomDepth           = z.bottomDepth,
        porePressureGradient  = z.porePressureGradient,
        fractureGradient      = z.fractureGradient,
        lithology             = z.lithology.ordinal
    )

    private fun toProto(s: SurveyStation): SurveyStationProto = SurveyStationProto(
        id            = s.id,
        measuredDepth = s.measuredDepth,
        inclination   = s.inclination,
        azimuth       = s.azimuth,
        tvd           = s.tvd ?: 0.0,
        northing      = s.northing ?: 0.0,
        easting       = s.easting ?: 0.0
    )

    // Proto → Domain

    fun toDomain(proto: WellProfileProto): WellProfile = WellProfile(
        id               = proto.id,
        wellName         = proto.wellName,
        totalDepth       = proto.totalDepth,
        casingOd         = proto.casingOd,
        casingId         = proto.casingId,
        drillString      = toDomain(proto.drillString),
        bitParameters    = toDomain(proto.bitParameters),
        fluidProperties  = toDomain(proto.fluidProperties),
        formationZones   = proto.formationZones.map { toDomain(it) },
        deviationSurvey  = proto.deviationSurvey.map { toDomain(it) },
        lastModified     = proto.lastModified
    )

    private fun toDomain(proto: DrillStringProto): DrillString = DrillString(
        drillPipeOd       = proto.drillPipeOd,
        drillPipeId       = proto.drillPipeId,
        drillCollarOd     = proto.drillCollarOd,
        drillCollarLength = proto.drillCollarLength,
        sections          = proto.sections.map { toDomain(it) }
    )

    private fun toDomain(proto: DrillStringSectionProto): DrillStringSection = DrillStringSection(
        name          = proto.name,
        od            = proto.od,
        id            = proto.id,
        length        = proto.length,
        weightPerFoot = proto.weightPerFoot
    )

    private fun toDomain(proto: BitParametersProto): BitParameters = BitParameters(
        bitSize     = proto.bitSize,
        nozzleCount = proto.nozzleCount,
        nozzleSizes = proto.nozzleSizes
    )

    private fun toDomain(proto: FluidPropertiesProto): FluidProperties = FluidProperties(
        mudWeight               = proto.mudWeight,
        flowRate                = proto.flowRate,
        surfaceTemperature      = proto.surfaceTemperature,
        bottomholeTemperature   = proto.bottomholeTemperature,
        rheologyModel           = RheologyModel.entries.getOrElse(proto.rheologyModel) { RheologyModel.BINGHAM_PLASTIC },
        plasticViscosity        = proto.plasticViscosity,
        yieldPoint              = proto.yieldPoint,
        flowBehaviorIndex       = proto.flowBehaviorIndex,
        consistencyIndex        = proto.consistencyIndex,
        mudType                 = MudType.entries.getOrElse(proto.mudType) { MudType.WATER_BASED },
        solidsContent           = proto.solidsContent,
        pH                      = proto.pH
    )

    private fun toDomain(proto: FormationZoneProto): FormationZone = FormationZone(
        id                    = proto.id,
        zoneName              = proto.zoneName,
        topDepth              = proto.topDepth,
        bottomDepth           = proto.bottomDepth,
        porePressureGradient  = proto.porePressureGradient,
        fractureGradient      = proto.fractureGradient,
        lithology             = Lithology.entries.getOrElse(proto.lithology) { Lithology.SHALE }
    )

    private fun toDomain(proto: SurveyStationProto): SurveyStation = SurveyStation(
        id            = proto.id,
        measuredDepth = proto.measuredDepth,
        inclination   = proto.inclination,
        azimuth       = proto.azimuth,
        tvd           = proto.tvd.takeIf { it != 0.0 },
        northing      = proto.northing.takeIf { it != 0.0 },
        easting       = proto.easting.takeIf { it != 0.0 }
    )
}