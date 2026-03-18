package com.oussama_chatri.feature.reports.domain.usecase

import com.oussama_chatri.core.util.FileUtil
import com.oussama_chatri.feature.reports.domain.model.ExportResult
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.reports.domain.repository.ReportRepository
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

class ExportExcelReportUseCase(private val repository: ReportRepository) {

    suspend fun execute(
        config:     ReportConfig,
        profile:    WellProfile,
        result:     SimulationResult,
        outputPath: String? = null
    ): ExportResult {
        val path = outputPath
            ?: defaultPath(profile.wellName, "xlsx")

        return repository.exportExcel(config, profile, result, path)
    }

    private fun defaultPath(wellName: String, ext: String): String {
        val safe = FileUtil.sanitizeFileName(wellName.ifBlank { "Well" })
        val dir  = FileUtil.appDataDir().resolve("exports").toFile().also { it.mkdirs() }
        return dir.resolve("${safe}_Report.${ext}").absolutePath
    }
}

class ExportWordReportUseCase(private val repository: ReportRepository) {

    suspend fun execute(
        config:     ReportConfig,
        profile:    WellProfile,
        result:     SimulationResult,
        outputPath: String? = null
    ): ExportResult {
        val path = outputPath
            ?: defaultPath(profile.wellName, "docx")

        return repository.exportWord(config, profile, result, path)
    }

    private fun defaultPath(wellName: String, ext: String): String {
        val safe = FileUtil.sanitizeFileName(wellName.ifBlank { "Well" })
        val dir  = FileUtil.appDataDir().resolve("exports").toFile().also { it.mkdirs() }
        return dir.resolve("${safe}_Report.${ext}").absolutePath
    }
}