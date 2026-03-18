package com.oussama_chatri.feature.reports.data.excel.sheets

import com.oussama_chatri.feature.reports.data.excel.ExcelTemplates
import com.oussama_chatri.feature.reports.data.excel.ExcelTheme
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.core.util.NumberFormatter
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object PressureProfileSheet {

    fun build(wb: XSSFWorkbook, theme: ExcelTheme, result: SimulationResult) {
        val sheet = wb.createSheet("Pressure Profile")
        ExcelTemplates.setColumnWidths(sheet, 14, 16, 16, 14, 14, 14, 14)

        var row = ExcelTemplates.sectionHeader(sheet, theme, 0, "Pressure Profile — ${result.wellName}")
        row = ExcelTemplates.spacer(sheet, row, 1)

        // KPI strip
        row = ExcelTemplates.kpiStrip(
            sheet = sheet,
            theme = theme,
            startRow = row,
            kpis = listOf(
                NumberFormatter.ppg(result.maxEcd)               to "Max ECD",
                NumberFormatter.psi(result.maxAnnularPressureLoss) to "Max Ann. Pressure Loss",
                NumberFormatter.psi(result.hydrostaticAtTd)       to "Hydrostatic @ TD",
                NumberFormatter.psi(result.totalSurfacePressure)  to "Total Surface Pressure"
            )
        )

        row = ExcelTemplates.spacer(sheet, row, 1)

        // Table
        val headers = listOf(
            "Depth (ft)", "Hydrostatic (psi)", "Ann. Loss (psi)",
            "ECD (ppg)", "Pore Pressure (ppg)", "Frac. Gradient (ppg)", "Status"
        )

        val tableRows = result.pressureProfile.map { p ->
            listOf(
                String.format("%.0f", p.depth),
                String.format("%.1f", p.hydrostaticPressure),
                String.format("%.1f", p.annularPressureLoss),
                String.format("%.3f", p.ecd),
                String.format("%.3f", p.porePressure),
                String.format("%.3f", p.fractureGradientPressure),
                if (p.isEcdSafe) "SAFE" else "WARNING"
            )
        }

        row = ExcelTemplates.dataTable(
            sheet       = sheet,
            theme       = theme,
            startRow    = row,
            headers     = headers,
            rows        = tableRows,
            numericCols = setOf(0, 1, 2, 3, 4, 5)
        )

        // Colour the status column manually
        val dataStartRow = row - tableRows.size
        result.pressureProfile.forEachIndexed { idx, p ->
            val sheetRow = sheet.getRow(dataStartRow + idx) ?: return@forEachIndexed
            val cell     = sheetRow.getCell(6) ?: return@forEachIndexed
            cell.cellStyle = if (p.isEcdSafe) theme.statusSafe else theme.statusWarning
        }

        ExcelTemplates.spacer(sheet, row, 1)
        ExcelTemplates.footer(sheet, theme, row + 1)
    }
}