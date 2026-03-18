package com.oussama_chatri.feature.reports.data.excel.sheets

import com.oussama_chatri.core.util.NumberFormatter
import com.oussama_chatri.feature.reports.data.excel.ExcelTemplates
import com.oussama_chatri.feature.reports.data.excel.ExcelTheme
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object ECDSheet {

    fun build(wb: XSSFWorkbook, theme: ExcelTheme, result: SimulationResult) {
        val sheet = wb.createSheet("ECD Profile")
        ExcelTemplates.setColumnWidths(sheet, 14, 14, 14, 14, 16, 16)

        var row = ExcelTemplates.sectionHeader(sheet, theme, 0, "ECD Profile — ${result.wellName}")
        row = ExcelTemplates.spacer(sheet, row, 1)

        val minEcd    = result.pressureProfile.minOfOrNull { it.ecd } ?: 0.0
        val safeCount = result.pressureProfile.count { it.isEcdSafe }
        val pct       = if (result.pressureProfile.isNotEmpty())
            safeCount * 100.0 / result.pressureProfile.size else 0.0

        // Explicit Pair<String,String> type avoids Serializable inference ambiguity
        val kpis: List<Pair<String, String>> = listOf(
            Pair(NumberFormatter.ppg(result.maxEcd),               "Max ECD"),
            Pair(NumberFormatter.ppg(minEcd),                      "Min ECD"),
            Pair("${String.format("%.1f", pct)}%",                 "Safe Window %"),
            Pair(if (result.isEcdSafe) "SAFE" else "WARNING",      "Overall Status")
        )

        row = ExcelTemplates.kpiStrip(
            sheet    = sheet,
            theme    = theme,
            startRow = row,
            kpis     = kpis
        )

        row = ExcelTemplates.spacer(sheet, row, 1)

        val headers = listOf(
            "Depth (ft)", "ECD (ppg)", "PP Gradient (ppg)", "FG Gradient (ppg)",
            "Ann. Velocity (ft/min)", "Flow Regime"
        )

        val tableRows = result.pressureProfile.mapIndexed { idx, p ->
            val vel = result.velocityProfile.getOrNull(idx)
            listOf(
                String.format("%.0f",  p.depth),
                String.format("%.3f",  p.ecd),
                String.format("%.3f",  p.porePressure),
                String.format("%.3f",  p.fractureGradientPressure),
                String.format("%.1f",  vel?.annularVelocity ?: 0.0),
                vel?.flowRegime?.name ?: "—"
            )
        }

        ExcelTemplates.dataTable(
            sheet       = sheet,
            theme       = theme,
            startRow    = row,
            headers     = headers,
            rows        = tableRows,
            numericCols = setOf(0, 1, 2, 3, 4)
        )

        ExcelTemplates.footer(sheet, theme, row + tableRows.size + 2)
    }
}