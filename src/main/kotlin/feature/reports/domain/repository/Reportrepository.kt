package com.oussama_chatri.feature.reports.domain.repository

import com.oussama_chatri.feature.reports.domain.model.ExportResult
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

interface ReportRepository {

    suspend fun exportExcel(
        config:  ReportConfig,
        profile: WellProfile,
        result:  SimulationResult,
        outputPath: String
    ): ExportResult

    suspend fun exportWord(
        config:  ReportConfig,
        profile: WellProfile,
        result:  SimulationResult,
        outputPath: String
    ): ExportResult
}