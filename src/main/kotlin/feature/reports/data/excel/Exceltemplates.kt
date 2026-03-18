package com.oussama_chatri.feature.reports.data.excel

import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFSheet

/**
 * Reusable Excel sheet-building blocks.
 *
 * Every function returns the next available row index so callers can chain:
 * ```kotlin
 * var row = 0
 * row = ExcelTemplates.sectionHeader(sheet, theme, row, "1. Summary", 8)
 * row = ExcelTemplates.kpiStrip(sheet, theme, row, kpis)
 * ```
 */
object ExcelTemplates {

    // Layout helpers

    fun setColumnWidths(sheet: XSSFSheet, vararg widths: Int) {
        widths.forEachIndexed { col, w -> sheet.setColumnWidth(col, w * 256) }
    }

    fun spacer(sheet: XSSFSheet, startRow: Int, count: Int = 1): Int {
        repeat(count) { sheet.createRow(startRow + it) }
        return startRow + count
    }

    fun merge(sheet: XSSFSheet, row: Int, firstCol: Int, lastCol: Int) {
        sheet.addMergedRegion(CellRangeAddress(row, row, firstCol, lastCol))
    }

    fun mergeBlock(sheet: XSSFSheet, firstRow: Int, lastRow: Int, firstCol: Int, lastCol: Int) {
        sheet.addMergedRegion(CellRangeAddress(firstRow, lastRow, firstCol, lastCol))
    }

    // Cover page

    fun coverPage(
        sheet:        XSSFSheet,
        theme:        ExcelTheme,
        title:        String,
        wellName:     String,
        engineerName: String,
        companyName:  String,
        date:         String,
        colSpan:      Int = 7
    ): Int {
        setColumnWidths(sheet, 4, 18, 18, 18, 18, 18, 18)
        sheet.defaultRowHeightInPoints = 22f

        for (r in 0..18) {
            val row = sheet.createRow(r)
            row.heightInPoints = 28f
            for (c in 0 until colSpan) {
                row.createCell(c).cellStyle =
                    if (r in 15..18) theme.headerAmber else theme.coverTitle
            }
        }

        fun setMergedCell(rowIdx: Int, text: String, style: XSSFCellStyle, height: Float = 28f) {
            val row = sheet.getRow(rowIdx) ?: sheet.createRow(rowIdx)
            row.heightInPoints = height
            val cell = row.getCell(1) ?: row.createCell(1)
            cell.setCellValue(text)
            cell.cellStyle = style
            for (c in 2 until colSpan) {
                val other = row.getCell(c) ?: row.createCell(c)
                other.cellStyle = style
            }
            val region = CellRangeAddress(rowIdx, rowIdx, 1, colSpan - 1)
            if (sheet.mergedRegions.none { it == region }) {
                sheet.addMergedRegion(region)
            }
        }

        setMergedCell(0, "",  theme.coverTitle)
        setMergedCell(1, "",  theme.coverTitle)
        setMergedCell(2, "| WellLogAnalyzer", theme.coverSubTitle, 30f)
        setMergedCell(3, "",  theme.coverTitle)
        setMergedCell(4, "",  theme.coverTitle)
        setMergedCell(5, title,    theme.coverTitle, 46f)
        setMergedCell(6, "",       theme.coverTitle, 20f)
        setMergedCell(7, wellName, theme.coverSubTitle, 30f)
        setMergedCell(8, "",       theme.coverTitle)
        setMergedCell(9, "---",    theme.coverSubTitle, 12f)
        setMergedCell(10, "Engineer:   $engineerName", theme.coverMeta)
        setMergedCell(11, "Company:    $companyName",  theme.coverMeta)
        setMergedCell(12, "Date:       $date",         theme.coverMeta)
        setMergedCell(13, "", theme.coverTitle)
        setMergedCell(14, "", theme.coverTitle)
        for (r in 15..18) setMergedCell(r, "", theme.headerAmber, 10f)

        return 20
    }

    // Section header

    fun sectionHeader(
        sheet:     XSSFSheet,
        theme:     ExcelTheme,
        startRow:  Int,
        title:     String,
        colSpan:   Int     = 8,
        accentBar: Boolean = true
    ): Int {
        var r = startRow
        val hRow = sheet.createRow(r++)
        hRow.heightInPoints = 28f
        hRow.createCell(0).apply { setCellValue(title); cellStyle = theme.headerDark }
        for (c in 1 until colSpan) hRow.createCell(c).cellStyle = theme.headerDark
        merge(sheet, r - 1, 0, colSpan - 1)

        if (accentBar) {
            val aRow = sheet.createRow(r++)
            aRow.heightInPoints = 4f
            for (c in 0 until colSpan) aRow.createCell(c).cellStyle = theme.statusWarning
            merge(sheet, r - 1, 0, colSpan - 1)
        }
        return r
    }

    // Column headers

    fun columnHeaders(
        sheet:    XSSFSheet,
        theme:    ExcelTheme,
        startRow: Int,
        headers:  List<String>
    ): Int {
        val row = sheet.createRow(startRow)
        row.heightInPoints = 22f
        headers.forEachIndexed { col, label ->
            row.createCell(col).apply { setCellValue(label); cellStyle = theme.headerAmber }
        }
        return startRow + 1
    }

    // KPI strip

    /**
     * @param kpis  Each entry is Pair(displayValue, label).
     *              Typed explicitly as [List] of [Pair] of [String] to prevent
     *              Kotlin from widening the list to List<Serializable>.
     */
    fun kpiStrip(
        sheet:      XSSFSheet,
        theme:      ExcelTheme,
        startRow:   Int,
        kpis:       List<Pair<String, String>>,
        colsPerKpi: Int = 2
    ): Int {
        val valueRow = sheet.createRow(startRow)
        val labelRow = sheet.createRow(startRow + 1)
        valueRow.heightInPoints = 36f
        labelRow.heightInPoints = 18f

        kpis.forEachIndexed { idx, kpi ->
            val value    = kpi.first
            val label    = kpi.second
            val startCol = idx * colsPerKpi
            val endCol   = startCol + colsPerKpi - 1

            valueRow.createCell(startCol).apply { setCellValue(value); cellStyle = theme.kpiValue }
            for (c in startCol + 1..endCol) valueRow.createCell(c).cellStyle = theme.kpiValue
            merge(sheet, startRow, startCol, endCol)

            labelRow.createCell(startCol).apply { setCellValue(label); cellStyle = theme.kpiLabel }
            for (c in startCol + 1..endCol) labelRow.createCell(c).cellStyle = theme.kpiLabel
            merge(sheet, startRow + 1, startCol, endCol)
        }

        return startRow + 2
    }

    // Metadata block

    /**
     * @param pairs  Each entry is Pair(label, value).
     *               Typed explicitly as [List] of [Pair] of [String].
     */
    fun metadataBlock(
        sheet:    XSSFSheet,
        theme:    ExcelTheme,
        startRow: Int,
        pairs:    List<Pair<String, String>>,
        labelCol: Int = 0,
        valueCol: Int = 1
    ): Int {
        var r = startRow
        pairs.forEachIndexed { idx, pair ->
            val label = pair.first
            val value = pair.second
            val isAlt = idx % 2 != 0
            val row   = sheet.createRow(r++)
            row.createCell(labelCol).apply { setCellValue(label); cellStyle = theme.boldLabel }
            row.createCell(valueCol).apply {
                setCellValue(value)
                cellStyle = if (isAlt) theme.dataCellAlt else theme.dataCell
            }
        }
        return r
    }

    // Data table

    fun dataTable(
        sheet:       XSSFSheet,
        theme:       ExcelTheme,
        startRow:    Int,
        headers:     List<String>,
        rows:        List<List<String>>,
        numericCols: Set<Int> = emptySet()
    ): Int {
        var r = columnHeaders(sheet, theme, startRow, headers)
        rows.forEachIndexed { rowIdx, cells ->
            val sheetRow = sheet.createRow(r++)
            val isAlt    = rowIdx % 2 != 0
            cells.forEachIndexed { col, text ->
                sheetRow.createCell(col).apply {
                    setCellValue(text)
                    cellStyle = when {
                        col in numericCols && isAlt -> theme.numericCellAlt
                        col in numericCols          -> theme.numericCell
                        isAlt                       -> theme.dataCellAlt
                        else                        -> theme.dataCell
                    }
                }
            }
        }
        return r
    }

    // Status style

    fun statusStyle(theme: ExcelTheme, status: String) = when (status.lowercase()) {
        "safe", "ok", "pass" -> theme.statusSafe
        "warning", "caution" -> theme.statusWarning
        else                 -> theme.statusDanger
    }

    // Footer

    fun footer(
        sheet:    XSSFSheet,
        theme:    ExcelTheme,
        startRow: Int,
        colSpan:  Int    = 8,
        text:     String = "Generated by WellLogAnalyzer  -  Confidential"
    ): Int {
        val row = sheet.createRow(startRow)
        row.heightInPoints = 18f
        row.createCell(0).apply { setCellValue(text); cellStyle = theme.kpiLabel }
        for (c in 1 until colSpan) row.createCell(c).cellStyle = theme.kpiLabel
        merge(sheet, startRow, 0, colSpan - 1)
        return startRow + 1
    }
}