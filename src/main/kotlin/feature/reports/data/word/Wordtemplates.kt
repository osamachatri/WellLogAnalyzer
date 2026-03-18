package com.oussama_chatri.feature.reports.data.word

import org.docx4j.jaxb.Context
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import java.math.BigInteger

/**
 * Comprehensive reusable Word document templates.
 *
 * Usage:
 * ```kotlin
 * val pkg = WordprocessingMLPackage.createPackage()
 * val mdp = pkg.mainDocumentPart
 * WordTemplates.coverPage(mdp, "Report Title", "Well-07A", "Engineer", "Acme", "Mar 2026")
 * WordTemplates.pageBreak(mdp)
 * WordTemplates.h1(mdp, "1. Executive Summary")
 * WordTemplates.bodyParagraph(mdp, "This report summarises…")
 * ```
 */
object WordTemplates {

    private val factory: ObjectFactory = Context.getWmlObjectFactory()

    // Docx4j 11.x uses string "dxa" for the table width type — no TblWidthType enum exists
    private const val DXA = "dxa"

    private fun Int.twips(): BigInteger = BigInteger.valueOf(this.toLong())

    // Run builder

    fun run(
        text:   String,
        font:   String  = WordTheme.FONT_BODY,
        sizePt: Int     = 11,
        bold:   Boolean = false,
        italic: Boolean = false,
        color:  String  = WordTheme.DARK_TEXT
    ): R {
        val r   = factory.createR()
        val rPr = factory.createRPr()

        val rFonts = factory.createRFonts()
        rFonts.ascii = font
        rFonts.hAnsi = font
        rFonts.cs    = font
        rPr.rFonts   = rFonts

        val sz = factory.createHpsMeasure()
        sz.`val` = BigInteger.valueOf((sizePt * 2).toLong())
        rPr.sz   = sz
        rPr.szCs = sz

        if (bold)   rPr.b  = factory.createBooleanDefaultTrue()
        if (italic) rPr.i  = factory.createBooleanDefaultTrue()

        val clr = factory.createColor()
        clr.`val` = color
        rPr.color = clr

        r.rPr = rPr

        val t = factory.createText()
        t.value = text
        t.space = "preserve"
        r.content.add(t)

        return r
    }

    // Paragraph builder

    fun paragraph(
        runs:        List<R>,
        alignment:   JcEnumeration = JcEnumeration.LEFT,
        spaceBefore: Int           = 0,
        spaceAfter:  Int           = 120,
        bgColor:     String?       = null,
        indent:      Int           = 0
    ): P {
        val p   = factory.createP()
        val pPr = factory.createPPr()

        val jc = factory.createJc()
        jc.`val` = alignment
        pPr.jc   = jc

        val spacing = factory.createPPrBaseSpacing()
        spacing.before = spaceBefore.twips()
        spacing.after  = spaceAfter.twips()
        pPr.spacing    = spacing

        if (bgColor != null) {
            val shd = factory.createCTShd()
            shd.fill  = bgColor
            shd.color = bgColor
            shd.`val` = STShd.CLEAR
            pPr.shd   = shd
        }

        if (indent > 0) {
            val ind = factory.createPPrBaseInd()
            ind.left = indent.twips()
            pPr.ind  = ind
        }

        p.pPr = pPr
        p.content.addAll(runs)
        return p
    }

    fun addParagraph(mdp: MainDocumentPart, p: P) {
        mdp.content.add(p)
    }

    // Cover page

    fun coverPage(
        mdp:          MainDocumentPart,
        title:        String,
        wellName:     String,
        engineerName: String,
        companyName:  String,
        date:         String
    ) {
        repeat(2) {
            addParagraph(mdp, paragraph(
                runs        = listOf(run("", color = WordTheme.AMBER)),
                bgColor     = WordTheme.AMBER,
                spaceBefore = 0,
                spaceAfter  = 0
            ))
        }

        addParagraph(mdp, paragraph(
            runs        = listOf(
                run("| ", font = WordTheme.FONT_HEADING, sizePt = 18, bold = true, color = WordTheme.AMBER),
                run("WellLogAnalyzer", font = WordTheme.FONT_HEADING, sizePt = 18, bold = true, color = WordTheme.WHITE)
            ),
            bgColor     = WordTheme.NAVY,
            alignment   = JcEnumeration.LEFT,
            spaceBefore = 200,
            spaceAfter  = 200
        ))

        repeat(3) {
            addParagraph(mdp, paragraph(listOf(run("")), bgColor = WordTheme.NAVY))
        }

        addParagraph(mdp, paragraph(
            runs        = listOf(run(title, font = WordTheme.FONT_HEADING, sizePt = 28, bold = true, color = WordTheme.WHITE)),
            bgColor     = WordTheme.NAVY,
            alignment   = JcEnumeration.CENTER,
            spaceBefore = 400,
            spaceAfter  = 200
        ))

        addParagraph(mdp, paragraph(
            runs        = listOf(run(wellName, font = WordTheme.FONT_HEADING, sizePt = 16, color = WordTheme.AMBER)),
            bgColor     = WordTheme.NAVY,
            alignment   = JcEnumeration.CENTER,
            spaceBefore = 0,
            spaceAfter  = 400
        ))

        addParagraph(mdp, paragraph(
            runs      = listOf(run("---", sizePt = 7, color = WordTheme.AMBER)),
            bgColor   = WordTheme.NAVY,
            alignment = JcEnumeration.CENTER
        ))

        listOf(
            "Engineer  :  $engineerName",
            "Company   :  $companyName",
            "Date      :  $date"
        ).forEach { line ->
            addParagraph(mdp, paragraph(
                runs        = listOf(run(line, sizePt = 12, color = WordTheme.MID_GRAY)),
                bgColor     = WordTheme.NAVY,
                alignment   = JcEnumeration.CENTER,
                spaceBefore = 80,
                spaceAfter  = 80
            ))
        }

        repeat(4) {
            addParagraph(mdp, paragraph(listOf(run("")), bgColor = WordTheme.NAVY))
        }

        repeat(2) {
            addParagraph(mdp, paragraph(
                runs        = listOf(run("", color = WordTheme.AMBER)),
                bgColor     = WordTheme.AMBER,
                spaceBefore = 0,
                spaceAfter  = 0
            ))
        }
    }

    // Headings

    fun h1(mdp: MainDocumentPart, text: String) {
        addParagraph(mdp, paragraph(
            runs        = listOf(
                run("# ", sizePt = 18, bold = true, color = WordTheme.AMBER),
                run(text,  sizePt = 18, bold = true, color = WordTheme.DARK_TEXT)
            ),
            spaceBefore = 360,
            spaceAfter  = 120
        ))
    }

    fun h2(mdp: MainDocumentPart, text: String) {
        addParagraph(mdp, paragraph(
            runs        = listOf(run(text, sizePt = 14, bold = true, color = WordTheme.WHITE)),
            bgColor     = WordTheme.SLATE,
            spaceBefore = 280,
            spaceAfter  = 100,
            indent      = 120
        ))
    }

    fun h3(mdp: MainDocumentPart, text: String) {
        addParagraph(mdp, paragraph(
            runs        = listOf(run(text, sizePt = 12, bold = true, color = WordTheme.AMBER)),
            spaceBefore = 200,
            spaceAfter  = 80
        ))
    }

    // Body text

    fun bodyParagraph(mdp: MainDocumentPart, text: String, color: String = WordTheme.DARK_TEXT) {
        addParagraph(mdp, paragraph(
            runs       = listOf(run(text, sizePt = 11, color = color)),
            spaceAfter = 120
        ))
    }

    fun labelValueLine(mdp: MainDocumentPart, label: String, value: String) {
        addParagraph(mdp, paragraph(
            runs = listOf(
                run("$label:  ", sizePt = 11, bold = true, color = WordTheme.DARK_TEXT),
                run(value,        sizePt = 11,               color = WordTheme.DARK_TEXT)
            ),
            spaceAfter = 80
        ))
    }

    fun caption(mdp: MainDocumentPart, text: String) {
        addParagraph(mdp, paragraph(
            runs       = listOf(run(text, sizePt = 9, italic = true, color = WordTheme.MID_GRAY)),
            spaceAfter = 80
        ))
    }

    fun alertBox(mdp: MainDocumentPart, text: String, kind: AlertKind = AlertKind.INFO) {
        val bg = when (kind) {
            AlertKind.INFO    -> WordTheme.SLATE
            AlertKind.SUCCESS -> WordTheme.TEAL
            AlertKind.WARNING -> WordTheme.AMBER
            AlertKind.DANGER  -> WordTheme.CORAL
        }
        val fg = when (kind) {
            AlertKind.INFO    -> WordTheme.WHITE
            AlertKind.SUCCESS -> WordTheme.DARK_TEXT
            AlertKind.WARNING -> WordTheme.DARK_TEXT
            AlertKind.DANGER  -> WordTheme.WHITE
        }
        val prefix = when (kind) {
            AlertKind.INFO    -> "[i]  "
            AlertKind.SUCCESS -> "[OK] "
            AlertKind.WARNING -> "[!]  "
            AlertKind.DANGER  -> "[X]  "
        }
        addParagraph(mdp, paragraph(
            runs        = listOf(run("$prefix$text", sizePt = 11, bold = true, color = fg)),
            bgColor     = bg,
            indent      = 180,
            spaceBefore = 100,
            spaceAfter  = 100
        ))
    }

    enum class AlertKind { INFO, SUCCESS, WARNING, DANGER }

    // Table width helper

    private fun makeTblWidth(value: Int): TblWidth {
        val w = factory.createTblWidth()
        w.w    = BigInteger.valueOf(value.toLong())
        w.type = DXA
        return w
    }

    // Data table

    fun dataTable(
        mdp:        MainDocumentPart,
        headers:    List<String>,
        rows:       List<List<String>>,
        colWidths:  List<Int>? = null,
        statusCols: Set<Int>  = emptySet()
    ): Tbl {
        val tbl   = factory.createTbl()
        val tblPr = factory.createTblPr()

        tblPr.tblW = makeTblWidth(9360)

        val bdr = {
            factory.createCTBorder().apply {
                `val` = STBorder.SINGLE
                sz    = BigInteger.valueOf(4)
                color = WordTheme.DIVIDER
            }
        }
        tblPr.tblBorders = factory.createTblBorders().apply {
            top     = bdr(); bottom  = bdr()
            left    = bdr(); right   = bdr()
            insideH = bdr(); insideV = bdr()
        }

        tbl.content.add(tblPr)

        val totalCols = headers.size.coerceAtLeast(1)
        val defaultW  = 9360 / totalCols
        val widths    = colWidths ?: List(totalCols) { defaultW }

        tbl.content.add(tableRow(headers, widths, WordTheme.NAVY, WordTheme.WHITE, bold = true))

        rows.forEachIndexed { rowIdx, cells ->
            val bg = if (rowIdx % 2 == 0) null else WordTheme.LIGHT_GRAY
            tbl.content.add(tableRow(cells, widths, bg, WordTheme.DARK_TEXT, statusCols = statusCols))
        }

        mdp.content.add(tbl)
        return tbl
    }

    private fun tableRow(
        cells:      List<String>,
        widths:     List<Int>,
        bgColor:    String? = null,
        textColor:  String  = WordTheme.DARK_TEXT,
        bold:       Boolean = false,
        statusCols: Set<Int> = emptySet()
    ): Tr {
        val tr  = factory.createTr()
        tr.trPr = factory.createTrPr()

        cells.forEachIndexed { idx, text ->
            val tc   = factory.createTc()
            val tcPr = factory.createTcPr()

            tcPr.tcW = makeTblWidth(widths.getOrElse(idx) { widths.last() })

            val cellBg = when {
                idx in statusCols -> statusBg(text)
                bgColor != null   -> bgColor
                else              -> null
            }
            if (cellBg != null) {
                val shd = factory.createCTShd()
                shd.fill  = cellBg
                shd.color = cellBg
                shd.`val` = STShd.CLEAR
                tcPr.shd  = shd
            }

            tc.tcPr = tcPr

            val cellTextColor = if (idx in statusCols) statusFg(text) else textColor
            tc.content.add(paragraph(
                runs        = listOf(run(text, sizePt = 10, bold = bold || idx in statusCols, color = cellTextColor)),
                spaceBefore = 40,
                spaceAfter  = 40
            ))

            tr.content.add(tc)
        }

        return tr
    }

    private fun statusBg(text: String) = when (text.lowercase()) {
        "safe", "ok", "pass" -> WordTheme.TEAL
        "warning", "caution" -> WordTheme.AMBER
        else                 -> WordTheme.CORAL
    }

    private fun statusFg(text: String) = when (text.lowercase()) {
        "safe", "ok", "pass" -> WordTheme.DARK_TEXT
        "warning", "caution" -> WordTheme.DARK_TEXT
        else                 -> WordTheme.WHITE
    }

    // KPI block

    fun kpiBlock(mdp: MainDocumentPart, kpis: List<Pair<String, String>>) {
        val tbl   = factory.createTbl()
        val tblPr = factory.createTblPr()
        tblPr.tblW = makeTblWidth(9360)
        tbl.content.add(tblPr)

        val perCol   = 9360 / kpis.size.coerceAtLeast(1)
        val valueRow = factory.createTr()
        val labelRow = factory.createTr()

        kpis.forEach { kpi ->
            valueRow.content.add(kpiCell(kpi.first,  isValue = true,  width = perCol))
            labelRow.content.add(kpiCell(kpi.second, isValue = false, width = perCol))
        }

        tbl.content.add(valueRow)
        tbl.content.add(labelRow)
        mdp.content.add(tbl)
    }

    private fun kpiCell(text: String, isValue: Boolean, width: Int): Tc {
        val tc   = factory.createTc()
        val tcPr = factory.createTcPr()
        tcPr.tcW = makeTblWidth(width)

        val bg = if (isValue) WordTheme.NAVY else WordTheme.SLATE
        val shd = factory.createCTShd()
        shd.fill  = bg; shd.color = bg; shd.`val` = STShd.CLEAR
        tcPr.shd  = shd
        tc.tcPr   = tcPr

        tc.content.add(paragraph(
            runs        = listOf(run(
                text,
                sizePt = if (isValue) 18 else 9,
                bold   = isValue,
                color  = if (isValue) WordTheme.AMBER else WordTheme.MID_GRAY
            )),
            alignment   = JcEnumeration.CENTER,
            spaceBefore = if (isValue) 120 else 40,
            spaceAfter  = if (isValue) 40  else 120
        ))
        return tc
    }

    // Misc

    fun pageBreak(mdp: MainDocumentPart) {
        val p  = factory.createP()
        val r  = factory.createR()
        val br = factory.createBr()
        br.type = STBrType.PAGE
        r.content.add(br)
        p.content.add(r)
        mdp.content.add(p)
    }

    fun horizontalRule(mdp: MainDocumentPart, color: String = WordTheme.AMBER) {
        addParagraph(mdp, paragraph(
            runs       = listOf(run("---", sizePt = 7, color = color)),
            alignment  = JcEnumeration.CENTER,
            spaceAfter = 120
        ))
    }

    fun bulletList(mdp: MainDocumentPart, items: List<String>) {
        items.forEach { item ->
            addParagraph(mdp, paragraph(
                runs       = listOf(
                    run("-  ", sizePt = 11, bold = true, color = WordTheme.AMBER),
                    run(item,   sizePt = 11,               color = WordTheme.DARK_TEXT)
                ),
                indent     = 360,
                spaceAfter = 80
            ))
        }
    }

    fun footerLine(mdp: MainDocumentPart, text: String = "Generated by WellLogAnalyzer  -  Confidential") {
        horizontalRule(mdp)
        addParagraph(mdp, paragraph(
            runs      = listOf(run(text, sizePt = 8, italic = true, color = WordTheme.MID_GRAY)),
            alignment = JcEnumeration.CENTER
        ))
    }
}