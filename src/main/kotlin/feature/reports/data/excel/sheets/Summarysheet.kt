package com.oussama_chatri.feature.reports.data.excel.sheets

import com.oussama_chatri.core.util.NumberFormatter
import com.oussama_chatri.feature.reports.data.excel.ExcelTemplates
import com.oussama_chatri.feature.reports.data.excel.ExcelTheme
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object SummarySheet {

    fun build(
        wb:      XSSFWorkbook,
        theme:   ExcelTheme,
        config:  ReportConfig,
        profile: WellProfile,
        result:  SimulationResult
    ) {
        val sheet = wb.createSheet("Summary")
        ExcelTemplates.setColumnWidths(sheet, 24, 20, 20, 20, 20, 20, 20, 20)

        var row = ExcelTemplates.coverPage(
            sheet        = sheet,
            theme        = theme,
            title        = config.reportTitle,
            wellName     = profile.wellName,
            engineerName = config.engineerName,
            companyName  = config.companyName,
            date         = config.date
        )

        row = ExcelTemplates.spacer(sheet, row, 2)
        row = ExcelTemplates.sectionHeader(sheet, theme, row, "Key Performance Indicators")

        val kpis: List<Pair<String, String>> = listOf(
            Pair(NumberFormatter.ppg(result.maxEcd),                 "Max ECD"),
            Pair(NumberFormatter.psi(result.maxAnnularPressureLoss), "Max APL"),
            Pair(NumberFormatter.psi(result.bitPressureDrop),        "Bit Delta-P"),
            Pair(NumberFormatter.psi(result.totalSurfacePressure),   "Surface Pressure")
        )

        row = ExcelTemplates.kpiStrip(
            sheet    = sheet,
            theme    = theme,
            startRow = row,
            kpis     = kpis
        )

        row = ExcelTemplates.spacer(sheet, row, 1)
        row = ExcelTemplates.sectionHeader(sheet, theme, row, "Well Information")

        val meta: List<Pair<String, String>> = listOf(
            Pair("Well Name",         profile.wellName),
            Pair("Total Depth",       NumberFormatter.feet(profile.totalDepth)),
            Pair("Casing OD",         NumberFormatter.inches(profile.casingOd)),
            Pair("Casing ID",         NumberFormatter.inches(profile.casingId)),
            Pair("Bit Size",          NumberFormatter.inches(profile.bitParameters.bitSize)),
            Pair("Mud Weight",        NumberFormatter.ppg(profile.fluidProperties.mudWeight)),
            Pair("Flow Rate",         NumberFormatter.gpm(profile.fluidProperties.flowRate)),
            Pair("Rheology Model",    profile.fluidProperties.rheologyModel.name),
            Pair("Formation Zones",   profile.formationZones.size.toString()),
            Pair("Simulation Status", if (result.isEcdSafe) "SAFE" else "WARNING")
        )

        row = ExcelTemplates.metadataBlock(sheet, theme, row, meta)

        row = ExcelTemplates.spacer(sheet, row, 2)
        ExcelTemplates.footer(sheet, theme, row)
    }
}