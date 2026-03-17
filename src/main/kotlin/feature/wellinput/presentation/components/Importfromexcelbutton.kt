package com.oussama_chatri.feature.wellinput.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.base.UiState
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.CoralDanger
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.NavyDeep
import com.oussama_chatri.core.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger("ImportFromExcel")

/**
 * Button that opens a JFileChooser for .xlsx / .xls files and
 * attempts to read well profile data from a standardised template.
 *
 * Expected Excel layout (Row 1 = header):
 *   Sheet "WellInfo"   : A2=WellName, B2=TotalDepth, C2=CasingOD, D2=CasingID
 *   Sheet "DrillString": columns OD, ID, Length, Weight
 *   Sheet "FluidProps" : A2=MudWeight, B2=FlowRate, C2=PV, D2=YP
 *   Sheet "Formations" : columns ZoneName, TopDepth, BottomDepth, PP, FG, Lithology
 *   Sheet "Survey"     : columns MD, Inclination, Azimuth
 */
@Composable
fun ImportFromExcelButton(
    importState: UiState<Unit>,
    onImportResult: (ExcelImportData) -> Unit,
    onResetState: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope    = rememberCoroutineScope()
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = {
                scope.launch {
                    // JFileChooser works fine on IO dispatcher — no AWT EDT required
                    val file = withContext(Dispatchers.IO) {
                        FileUtil.showOpenDialog(
                            title      = "Import Well Profile from Excel",
                            extensions = listOf("xlsx", "xls")
                        )
                    }
                    if (file != null) {
                        try {
                            val data = withContext(Dispatchers.IO) { parseExcel(file) }
                            onImportResult(data)
                            errorMsg = null
                        } catch (e: Exception) {
                            logger.error("Excel import failed", e)
                            errorMsg = "Import failed: ${e.message}"
                        }
                    }
                }
            },
            enabled = importState !is UiState.Loading,
            colors  = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
            border  = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
        ) {
            if (importState is UiState.Loading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(14.dp),
                    color       = AmberGold,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Importing…")
            } else {
                Icon(Icons.Default.FileOpen, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Import from Excel")
            }
        }

        errorMsg?.let { msg ->
            Spacer(Modifier.height(4.dp))
            Text(
                text  = msg,
                style = MaterialTheme.typography.labelSmall,
                color = CoralDanger
            )
        }
    }
}

// Data class returned to the ViewModel
data class ExcelImportData(
    val wellName: String?    = null,
    val totalDepth: Double?  = null,
    val casingOd: Double?    = null,
    val casingId: Double?    = null,
    val mudWeight: Double?   = null,
    val flowRate: Double?    = null,
    val plasticViscosity: Double? = null,
    val yieldPoint: Double?  = null,
    val formations: List<Map<String, String>> = emptyList(),
    val survey: List<Map<String, String>>     = emptyList()
)

// Apache POI parsing
private fun parseExcel(file: File): ExcelImportData {
    val workbook = WorkbookFactory.create(file)
    workbook.use { wb ->
        val wellSheet  = wb.getSheet("WellInfo")
        val wellName   = wellSheet?.getRow(1)?.getCell(0)?.stringCellValue
        val totalDepth = wellSheet?.getRow(1)?.getCell(1)?.numericCellValue
        val casingOd   = wellSheet?.getRow(1)?.getCell(2)?.numericCellValue
        val casingId   = wellSheet?.getRow(1)?.getCell(3)?.numericCellValue

        val fluidSheet = wb.getSheet("FluidProps")
        val mudWeight  = fluidSheet?.getRow(1)?.getCell(0)?.numericCellValue
        val flowRate   = fluidSheet?.getRow(1)?.getCell(1)?.numericCellValue
        val pv         = fluidSheet?.getRow(1)?.getCell(2)?.numericCellValue
        val yp         = fluidSheet?.getRow(1)?.getCell(3)?.numericCellValue

        val formSheet  = wb.getSheet("Formations")
        val formations = mutableListOf<Map<String, String>>()
        if (formSheet != null) {
            for (rowIdx in 1..formSheet.lastRowNum) {
                val row = formSheet.getRow(rowIdx) ?: continue
                formations += mapOf(
                    "zoneName"    to (row.getCell(0)?.stringCellValue  ?: ""),
                    "topDepth"    to (row.getCell(1)?.numericCellValue?.toString() ?: ""),
                    "bottomDepth" to (row.getCell(2)?.numericCellValue?.toString() ?: ""),
                    "pp"          to (row.getCell(3)?.numericCellValue?.toString() ?: ""),
                    "fg"          to (row.getCell(4)?.numericCellValue?.toString() ?: ""),
                    "lithology"   to (row.getCell(5)?.stringCellValue  ?: "")
                )
            }
        }

        val surveySheet = wb.getSheet("Survey")
        val survey      = mutableListOf<Map<String, String>>()
        if (surveySheet != null) {
            for (rowIdx in 1..surveySheet.lastRowNum) {
                val row = surveySheet.getRow(rowIdx) ?: continue
                survey += mapOf(
                    "md"  to (row.getCell(0)?.numericCellValue?.toString() ?: ""),
                    "inc" to (row.getCell(1)?.numericCellValue?.toString() ?: ""),
                    "azi" to (row.getCell(2)?.numericCellValue?.toString() ?: "")
                )
            }
        }

        return ExcelImportData(
            wellName         = wellName,
            totalDepth       = totalDepth,
            casingOd         = casingOd,
            casingId         = casingId,
            mudWeight        = mudWeight,
            flowRate         = flowRate,
            plasticViscosity = pv,
            yieldPoint       = yp,
            formations       = formations,
            survey           = survey
        )
    }
}