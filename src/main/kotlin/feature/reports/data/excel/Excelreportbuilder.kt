package com.oussama_chatri.feature.reports.data.excel

import com.oussama_chatri.feature.reports.data.excel.sheets.BitHydraulicsSheet
import com.oussama_chatri.feature.reports.data.excel.sheets.ECDSheet
import com.oussama_chatri.feature.reports.data.excel.sheets.FormationZoneSheet
import com.oussama_chatri.feature.reports.data.excel.sheets.PressureProfileSheet
import com.oussama_chatri.feature.reports.data.excel.sheets.SummarySheet
import com.oussama_chatri.feature.reports.domain.model.ExportFormat
import com.oussama_chatri.feature.reports.domain.model.ExportResult
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.reports.domain.model.ReportSection
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream

/**
 * Assembles a full multi-sheet Excel workbook from all enabled report sections.
 */
class ExcelReportBuilder {

    private val logger = LoggerFactory.getLogger(ExcelReportBuilder::class.java)

    fun build(
        config:     ReportConfig,
        profile:    WellProfile,
        result:     SimulationResult,
        outputPath: String
    ): ExportResult {
        return try {
            val wb    = XSSFWorkbook()
            val theme = ExcelTheme(wb)

            // Always include summary sheet
            SummarySheet.build(wb, theme, config, profile, result)

            if (ReportSection.PRESSURE_CHARTS in config.sections) {
                PressureProfileSheet.build(wb, theme, result)
            }
            if (ReportSection.ECD_PROFILE in config.sections) {
                ECDSheet.build(wb, theme, result)
            }
            if (ReportSection.FORMATION_ZONES in config.sections) {
                FormationZoneSheet.build(wb, theme, profile)
            }
            if (ReportSection.SIMULATION_RESULTS in config.sections) {
                BitHydraulicsSheet.build(wb, theme, profile, result)
            }

            val file = File(outputPath)
            file.parentFile?.mkdirs()
            FileOutputStream(file).use { wb.write(it) }
            wb.close()

            logger.info("Excel report written to {}", outputPath)
            ExportResult.Success(outputPath, ExportFormat.EXCEL)

        } catch (e: Exception) {
            logger.error("Excel export failed", e)
            ExportResult.Failure("Excel export failed: ${e.message}", e)
        }
    }
}