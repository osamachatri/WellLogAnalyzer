package com.oussama_chatri.feature.dashboard.data.local

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ProjectSummaryProto(
    @ProtoNumber(1)  val id: String                = "",
    @ProtoNumber(2)  val wellName: String          = "",
    @ProtoNumber(3)  val totalDepth: Double        = 0.0,
    @ProtoNumber(4)  val simulationCount: Int      = 0,
    @ProtoNumber(5)  val lastRunTimestamp: Long    = 0L,
    @ProtoNumber(6)  val lastExportTimestamp: Long = 0L,
    @ProtoNumber(7)  val lastExportFormat: String  = "",
    @ProtoNumber(8)  val maxEcd: Double            = 0.0,
    @ProtoNumber(9)  val isEcdSafe: Boolean        = false,
    @ProtoNumber(10) val hasSimulation: Boolean    = false,  // distinguishes "no sim" from ecd=0
    @ProtoNumber(11) val createdAt: Long           = 0L
)

@Serializable
data class ProjectListProto(
    @ProtoNumber(1) val projects: List<ProjectSummaryProto> = emptyList()
)