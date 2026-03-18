package com.oussama_chatri.feature.dashboard.data.mapper

import com.oussama_chatri.feature.dashboard.data.local.ProjectSummaryProto
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary

object ProjectMapper {

    fun toDomain(proto: ProjectSummaryProto): ProjectSummary = ProjectSummary(
        id                  = proto.id,
        wellName            = proto.wellName,
        totalDepth          = proto.totalDepth,
        simulationCount     = proto.simulationCount,
        lastRunTimestamp    = proto.lastRunTimestamp.takeIf { it > 0L },
        lastExportTimestamp = proto.lastExportTimestamp.takeIf { it > 0L },
        lastExportFormat    = proto.lastExportFormat.ifBlank { null },
        maxEcd              = if (proto.hasSimulation) proto.maxEcd else null,
        isEcdSafe           = if (proto.hasSimulation) proto.isEcdSafe else null,
        createdAt           = proto.createdAt
    )

    fun toProto(domain: ProjectSummary): ProjectSummaryProto = ProjectSummaryProto(
        id                  = domain.id,
        wellName            = domain.wellName,
        totalDepth          = domain.totalDepth,
        simulationCount     = domain.simulationCount,
        lastRunTimestamp    = domain.lastRunTimestamp ?: 0L,
        lastExportTimestamp = domain.lastExportTimestamp ?: 0L,
        lastExportFormat    = domain.lastExportFormat ?: "",
        maxEcd              = domain.maxEcd ?: 0.0,
        isEcdSafe           = domain.isEcdSafe ?: false,
        hasSimulation       = domain.maxEcd != null,
        createdAt           = domain.createdAt
    )
}