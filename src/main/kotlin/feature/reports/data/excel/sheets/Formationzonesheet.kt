package com.oussama_chatri.feature.reports.data.excel.sheets

import com.oussama_chatri.feature.reports.data.excel.ExcelTemplates
import com.oussama_chatri.feature.reports.data.excel.ExcelTheme
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object FormationZoneSheet {

    fun build(wb: XSSFWorkbook, theme: ExcelTheme, profile: WellProfile) {
        val sheet = wb.createSheet("Formation Zones")
        ExcelTemplates.setColumnWidths(sheet, 20, 14, 14, 16, 16, 14)

        var row = ExcelTemplates.sectionHeader(sheet, theme, 0, "Formation Zones — ${profile.wellName}")
        row = ExcelTemplates.spacer(sheet, row, 1)

        val headers = listOf(
            "Zone Name", "Top Depth (ft)", "Bottom Depth (ft)",
            "Pore Pressure (ppg)", "Frac. Gradient (ppg)", "Lithology"
        )

        val tableRows = profile.formationZones.map { z ->
            listOf(
                z.zoneName,
                String.format("%.0f", z.topDepth),
                String.format("%.0f", z.bottomDepth),
                String.format("%.2f", z.porePressureGradient),
                String.format("%.2f", z.fractureGradient),
                z.lithology.displayName
            )
        }

        row = ExcelTemplates.dataTable(
            sheet       = sheet,
            theme       = theme,
            startRow    = row,
            headers     = headers,
            rows        = tableRows,
            numericCols = setOf(1, 2, 3, 4)
        )

        row = ExcelTemplates.spacer(sheet, row, 1)

        // Summary stats
        row = ExcelTemplates.sectionHeader(sheet, theme, row, "Zone Statistics", accentBar = false)
        val minPP  = profile.formationZones.minOfOrNull { it.porePressureGradient } ?: 0.0
        val maxPP  = profile.formationZones.maxOfOrNull { it.porePressureGradient } ?: 0.0
        val minFG  = profile.formationZones.minOfOrNull { it.fractureGradient } ?: 0.0
        val maxFG  = profile.formationZones.maxOfOrNull { it.fractureGradient } ?: 0.0

        ExcelTemplates.metadataBlock(
            sheet    = sheet,
            theme    = theme,
            startRow = row,
            pairs    = listOf(
                "Total zones"           to profile.formationZones.size.toString(),
                "Min Pore Pressure"     to "${String.format("%.2f", minPP)} ppg",
                "Max Pore Pressure"     to "${String.format("%.2f", maxPP)} ppg",
                "Min Fracture Gradient" to "${String.format("%.2f", minFG)} ppg",
                "Max Fracture Gradient" to "${String.format("%.2f", maxFG)} ppg"
            )
        )

        ExcelTemplates.footer(sheet, theme, row + 7)
    }
}