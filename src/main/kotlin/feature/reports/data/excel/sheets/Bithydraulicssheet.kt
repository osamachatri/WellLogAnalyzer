package com.oussama_chatri.feature.reports.data.excel.sheets

import com.oussama_chatri.core.util.NumberFormatter
import com.oussama_chatri.feature.reports.data.excel.ExcelTemplates
import com.oussama_chatri.feature.reports.data.excel.ExcelTheme
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object BitHydraulicsSheet {

    fun build(wb: XSSFWorkbook, theme: ExcelTheme, profile: WellProfile, result: SimulationResult) {
        val sheet = wb.createSheet("Bit Hydraulics")
        ExcelTemplates.setColumnWidths(sheet, 26, 18, 18, 18)

        var row = ExcelTemplates.sectionHeader(sheet, theme, 0, "Bit Hydraulics — ${profile.wellName}")
        row = ExcelTemplates.spacer(sheet, row, 1)

        val kpis: List<Pair<String, String>> = listOf(
            Pair(NumberFormatter.psi(result.bitPressureDrop),                         "Bit Pressure Drop"),
            Pair(NumberFormatter.hp(result.hydraulicHorsepower),                      "Hydraulic HP"),
            Pair("${String.format("%.1f", result.nozzleVelocity)} ft/s",              "Nozzle Velocity"),
            Pair(NumberFormatter.lbf(result.impactForce),                             "Impact Force")
        )

        row = ExcelTemplates.kpiStrip(sheet, theme, row, kpis)
        row = ExcelTemplates.spacer(sheet, row, 1)

        val hsiStatus = when {
            result.hsi < 1.0  -> "LOW"
            result.hsi <= 1.5 -> "OPTIMAL"
            else              -> "HIGH"
        }

        row = ExcelTemplates.sectionHeader(sheet, theme, row, "HSI (Hydraulic HP per sq in)", accentBar = false)

        val hsiMeta: List<Pair<String, String>> = listOf(
            Pair("HSI Value",        NumberFormatter.hsi(result.hsi)),
            Pair("API RP 13D Range", "1.0 - 1.5 hp/in2"),
            Pair("Status",           hsiStatus),
            Pair("Recommendation", when (hsiStatus) {
                "LOW"  -> "Increase flow rate or enlarge nozzles"
                "HIGH" -> "Reduce flow rate or use smaller nozzles"
                else   -> "HSI is within optimal range"
            })
        )
        row = ExcelTemplates.metadataBlock(sheet, theme, row, hsiMeta)
        row = ExcelTemplates.spacer(sheet, row, 1)

        row = ExcelTemplates.sectionHeader(sheet, theme, row, "Nozzle Configuration", accentBar = false)
        val nozzleRows = profile.bitParameters.nozzleSizes.mapIndexed { idx, size ->
            listOf(
                "Nozzle ${idx + 1}",
                "${size.toInt()} / 32 in",
                String.format("%.4f", (size / 32.0) * (size / 32.0) * Math.PI / 4.0) + " in2"
            )
        }
        row = ExcelTemplates.dataTable(sheet, theme, row,
            listOf("Nozzle", "Size", "Individual Area"), nozzleRows)

        row = ExcelTemplates.spacer(sheet, row, 1)
        row = ExcelTemplates.sectionHeader(sheet, theme, row, "Surge & Swab Pressures", accentBar = false)

        val surgeMeta: List<Pair<String, String>> = listOf(
            Pair("Surge Pressure", NumberFormatter.psi(result.surgePressure)),
            Pair("Swab Pressure",  NumberFormatter.psi(result.swabPressure)),
            Pair("Trip Speed",     "60 ft/min (assumed)")
        )
        ExcelTemplates.metadataBlock(sheet, theme, row, surgeMeta)
        ExcelTemplates.footer(sheet, theme, row + 5)
    }
}