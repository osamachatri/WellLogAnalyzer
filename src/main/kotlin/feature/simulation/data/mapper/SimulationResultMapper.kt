package com.oussama_chatri.feature.simulation.data.mapper

import com.oussama_chatri.feature.simulation.data.local.PressurePointProto
import com.oussama_chatri.feature.simulation.data.local.SimFormationZoneProto
import com.oussama_chatri.feature.simulation.data.local.SimulationResultProto
import com.oussama_chatri.feature.simulation.data.local.VelocityProfileProto
import com.oussama_chatri.feature.simulation.domain.model.FlowRegime
import com.oussama_chatri.feature.simulation.domain.model.PressurePoint
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.simulation.domain.model.VelocityProfile
import com.oussama_chatri.feature.wellinput.domain.model.FormationZone
import com.oussama_chatri.feature.wellinput.domain.model.Lithology

object SimulationResultMapper {

    // Domain → Proto

    fun toProto(domain: SimulationResult): SimulationResultProto = SimulationResultProto(
        wellId              = domain.wellId,
        wellName            = domain.wellName,
        timestamp           = domain.timestamp,
        pressureProfile     = domain.pressureProfile.map { toProto(it) },
        velocityProfile     = domain.velocityProfile.map { toProto(it) },
        bitPressureDrop     = domain.bitPressureDrop,
        hydraulicHorsepower = domain.hydraulicHorsepower,
        nozzleVelocity      = domain.nozzleVelocity,
        impactForce         = domain.impactForce,
        hsi                 = domain.hsi,
        surgePressure       = domain.surgePressure,
        swabPressure        = domain.swabPressure,
        formationZones      = domain.formationZones.map { toProto(it) }
    )

    private fun toProto(p: PressurePoint): PressurePointProto = PressurePointProto(
        depth                    = p.depth,
        hydrostaticPressure      = p.hydrostaticPressure,
        annularPressureLoss      = p.annularPressureLoss,
        ecd                      = p.ecd,
        porePressure             = p.porePressure,
        fractureGradientPressure = p.fractureGradientPressure
    )

    private fun toProto(v: VelocityProfile): VelocityProfileProto = VelocityProfileProto(
        depth           = v.depth,
        annularVelocity = v.annularVelocity,
        pipeVelocity    = v.pipeVelocity,
        flowRegime      = v.flowRegime.ordinal
    )

    private fun toProto(z: FormationZone): SimFormationZoneProto = SimFormationZoneProto(
        id                   = z.id,
        zoneName             = z.zoneName,
        topDepth             = z.topDepth,
        bottomDepth          = z.bottomDepth,
        porePressureGradient = z.porePressureGradient,
        fractureGradient     = z.fractureGradient,
        lithology            = z.lithology.ordinal
    )

    // Proto → Domain

    fun toDomain(proto: SimulationResultProto): SimulationResult = SimulationResult(
        wellId              = proto.wellId,
        wellName            = proto.wellName,
        timestamp           = proto.timestamp,
        pressureProfile     = proto.pressureProfile.map { toDomain(it) },
        velocityProfile     = proto.velocityProfile.map { toDomain(it) },
        bitPressureDrop     = proto.bitPressureDrop,
        hydraulicHorsepower = proto.hydraulicHorsepower,
        nozzleVelocity      = proto.nozzleVelocity,
        impactForce         = proto.impactForce,
        hsi                 = proto.hsi,
        surgePressure       = proto.surgePressure,
        swabPressure        = proto.swabPressure,
        formationZones      = proto.formationZones.map { toDomain(it) }
    )

    private fun toDomain(proto: PressurePointProto): PressurePoint = PressurePoint(
        depth                    = proto.depth,
        hydrostaticPressure      = proto.hydrostaticPressure,
        annularPressureLoss      = proto.annularPressureLoss,
        ecd                      = proto.ecd,
        porePressure             = proto.porePressure,
        fractureGradientPressure = proto.fractureGradientPressure
    )

    private fun toDomain(proto: VelocityProfileProto): VelocityProfile = VelocityProfile(
        depth           = proto.depth,
        annularVelocity = proto.annularVelocity,
        pipeVelocity    = proto.pipeVelocity,
        flowRegime      = FlowRegime.entries.getOrElse(proto.flowRegime) { FlowRegime.LAMINAR }
    )

    private fun toDomain(proto: SimFormationZoneProto): FormationZone = FormationZone(
        id                   = proto.id,
        zoneName             = proto.zoneName,
        topDepth             = proto.topDepth,
        bottomDepth          = proto.bottomDepth,
        porePressureGradient = proto.porePressureGradient,
        fractureGradient     = proto.fractureGradient,
        lithology            = Lithology.entries.getOrElse(proto.lithology) { Lithology.SHALE }
    )
}