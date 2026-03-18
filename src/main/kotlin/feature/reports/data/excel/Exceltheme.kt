package com.oussama_chatri.feature.reports.data.excel

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color

/**
 * Central style factory for WellLogAnalyzer Excel reports.
 *
 * All brand colours match the Compose theme palette:
 *   Navy    #0D1B2A  — cover / primary header background
 *   Slate   #1A2535  — section header background
 *   Amber   #F4A917  — accent / KPI values
 *   Teal    #2EC4B6  — safe / success cells
 *   Coral   #E63946  — danger / alert cells
 */
class ExcelTheme(private val wb: XSSFWorkbook) {

    // Raw XSSFColor helpers

    private fun rgb(hex: String): XSSFColor {
        val c = Color.decode(hex)
        return XSSFColor(byteArrayOf(c.red.toByte(), c.green.toByte(), c.blue.toByte()), null)
    }

    val navy     = rgb("#0D1B2A")
    val slate    = rgb("#1A2535")
    val amber    = rgb("#F4A917")
    val teal     = rgb("#2EC4B6")
    val coral    = rgb("#E63946")
    val white    = rgb("#FFFFFF")
    val ltGray   = rgb("#EBF0F5")
    val midGray  = rgb("#94A3B8")
    val darkText = rgb("#0D1B2A")

    // Font builder

    fun font(
        name:   String      = "Calibri",
        size:   Short       = 11,
        bold:   Boolean     = false,
        italic: Boolean     = false,
        color:  XSSFColor   = white
    ): XSSFFont = wb.createFont().also { f ->
        f.fontName              = name
        f.fontHeightInPoints    = size
        f.bold                  = bold
        f.italic                = italic
        f.setColor(color)
    }

    // Style builder helper

    private fun style(
        bgColor:   XSSFColor? = null,
        font:      XSSFFont,
        hAlign:    HorizontalAlignment = HorizontalAlignment.LEFT,
        vAlign:    VerticalAlignment   = VerticalAlignment.CENTER,
        wrap:      Boolean             = false,
        borderBot: BorderStyle?        = null
    ): XSSFCellStyle {
        val s = wb.createCellStyle()
        if (bgColor != null) {
            s.setFillForegroundColor(bgColor)
            s.fillPattern = FillPatternType.SOLID_FOREGROUND
        }
        s.setFont(font)
        s.setAlignment(hAlign)
        s.setVerticalAlignment(vAlign)
        s.wrapText = wrap
        if (borderBot != null) {
            s.setBorderBottom(borderBot)
            s.bottomBorderColor = IndexedColors.AUTOMATIC.index
        }
        return s
    }

    // Pre-built styles

    val headerDark: XSSFCellStyle
        get() = style(navy,  font(bold = true, size = 12))

    val headerSlate: XSSFCellStyle
        get() = style(slate, font(bold = true, size = 11))

    val headerAmber: XSSFCellStyle
        get() = style(amber, font(bold = true, size = 11, color = darkText), HorizontalAlignment.CENTER)

    val dataCell: XSSFCellStyle
        get() = style(null, font(color = darkText))

    val dataCellAlt: XSSFCellStyle
        get() = style(ltGray, font(color = darkText))

    val numericCell: XSSFCellStyle
        get() = style(null, font(color = darkText), HorizontalAlignment.RIGHT)

    val numericCellAlt: XSSFCellStyle
        get() = style(ltGray, font(color = darkText), HorizontalAlignment.RIGHT)

    val kpiValue: XSSFCellStyle
        get() = style(navy, font(bold = true, size = 16, color = amber), HorizontalAlignment.CENTER)

    val kpiLabel: XSSFCellStyle
        get() = style(slate, font(size = 9, color = midGray), HorizontalAlignment.CENTER)

    val statusSafe: XSSFCellStyle
        get() = style(teal, font(bold = true, size = 10, color = darkText), HorizontalAlignment.CENTER)

    val statusWarning: XSSFCellStyle
        get() = style(amber, font(bold = true, size = 10, color = darkText), HorizontalAlignment.CENTER)

    val statusDanger: XSSFCellStyle
        get() = style(coral, font(bold = true, size = 10, color = white), HorizontalAlignment.CENTER)

    val coverTitle: XSSFCellStyle
        get() = style(navy, font(bold = true, size = 22, color = white), HorizontalAlignment.CENTER, wrap = true)

    val coverSubTitle: XSSFCellStyle
        get() = style(navy, font(size = 13, color = amber), HorizontalAlignment.CENTER)

    val coverMeta: XSSFCellStyle
        get() = style(navy, font(size = 11, color = midGray), HorizontalAlignment.CENTER)

    val boldLabel: XSSFCellStyle
        get() = style(null, font(bold = true, size = 10, color = darkText))

    val bodyText: XSSFCellStyle
        get() = style(null, font(size = 10, color = darkText), wrap = true)

    val sectionDivider: XSSFCellStyle
        get() = style(navy, font(bold = true, size = 13, color = amber),
            borderBot = BorderStyle.MEDIUM)

    val decimalFormat: Short
        get() = wb.createDataFormat().getFormat("0.00").toShort()

    val intFormat: Short
        get() = wb.createDataFormat().getFormat("#,##0").toShort()
}