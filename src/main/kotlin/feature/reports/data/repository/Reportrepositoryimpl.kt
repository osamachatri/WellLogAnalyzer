package com.oussama_chatri.feature.reports.data.repository

import com.oussama_chatri.core.util.withIO
import com.oussama_chatri.feature.reports.data.excel.ExcelReportBuilder
import com.oussama_chatri.feature.reports.data.word.WordReportBuilder
import com.oussama_chatri.feature.reports.domain.model.ExportResult
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.reports.domain.repository.ReportRepository
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

class ReportRepositoryImpl(
    private val excelBuilder: ExcelReportBuilder,
    private val wordBuilder:  WordReportBuilder
) : ReportRepository {

    override suspend fun exportExcel(
        config:     ReportConfig,
        profile:    WellProfile,
        result:     SimulationResult,
        outputPath: String
    ): ExportResult = withIO {
        excelBuilder.build(config, profile, result, outputPath)
    }

    override suspend fun exportWord(
        config:     ReportConfig,
        profile:    WellProfile,
        result:     SimulationResult,
        outputPath: String
    ): ExportResult = withIO {
        wordBuilder.build(config, profile, result, outputPath)
    }
}