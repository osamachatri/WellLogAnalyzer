package com.oussama_chatri.feature.reports.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.feature.reports.presentation.components.ExportFormatCard
import com.oussama_chatri.feature.reports.presentation.components.ReportConfigPanel
import com.oussama_chatri.feature.reports.presentation.components.ReportPreviewPane
import com.oussama_chatri.feature.reports.presentation.viewmodel.ExportState
import com.oussama_chatri.feature.reports.presentation.viewmodel.ReportViewModel
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import org.koin.compose.koinInject

@Composable
fun ReportScreen(
    profile:  WellProfile?,
    result:   SimulationResult?,
    modifier: Modifier = Modifier
) {
    val viewModel: ReportViewModel = koinInject()

    val config      by viewModel.config.collectAsState()
    val exportState by viewModel.exportState.collectAsState()

    // Pre-fill title from profile
    LaunchedEffect(profile) {
        profile?.let { viewModel.setProfile(it) }
    }

    val isExporting = exportState is ExportState.Exporting

    if (profile == null || result == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Article, null,
                    tint     = DividerColor,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    "No simulation result available",
                    style    = MaterialTheme.typography.headlineSmall,
                    color    = TextSecondary,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    "Run a simulation first, then come back to export a report.",
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = TextMuted,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
        return
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Left panel: config
        Column(
            modifier = Modifier
                .width(380.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReportConfigPanel(
                config           = config,
                onTitleChange    = viewModel::updateTitle,
                onEngineerChange = viewModel::updateEngineer,
                onCompanyChange  = viewModel::updateCompany,
                onDateChange     = viewModel::updateDate,
                onToggleSection  = viewModel::toggleSection,
                onSelectAll      = viewModel::selectAllSections,
                onClearAll       = viewModel::clearAllSections,
                modifier         = Modifier.weight(1f)
            )
        }

        // Divider
        Box(Modifier.width(1.dp).fillMaxHeight().background(DividerColor))

        // Center: format cards
        Column(
            modifier = Modifier
                .width(340.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text  = "Export Format",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )

            ExportFormatCard(
                icon        = Icons.Default.GridOn,
                iconTint    = TealSafe,
                formatName  = "Excel (.xlsx)",
                description = "Tabular data with professional formatting, KPI strip, alternating row shading, and colour-coded status cells.",
                buttonLabel = "Export Excel",
                isExporting = exportState is ExportState.Exporting &&
                        (exportState as ExportState.Exporting).format.name == "EXCEL",
                onExport    = { viewModel.exportExcel(profile, result) }
            )

            ExportFormatCard(
                icon        = Icons.Default.Article,
                iconTint    = TealSafe.copy(blue = 0.9f),
                formatName  = "Word (.docx)",
                description = "Narrative report with cover page, branded headers, KPI blocks, data tables, and alert boxes.",
                buttonLabel = "Export Word",
                isExporting = exportState is ExportState.Exporting &&
                        (exportState as ExportState.Exporting).format.name == "WORD",
                onExport    = { viewModel.exportWord(profile, result) }
            )

            // Export Both button
            Button(
                onClick  = { viewModel.exportBoth(profile, result) },
                enabled  = !isExporting,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = AmberGold,
                    contentColor           = NavyDeep,
                    disabledContainerColor = CardElevated,
                    disabledContentColor   = TextMuted
                )
            ) {
                Icon(Icons.Default.TableChart, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Export Both", style = MaterialTheme.typography.labelLarge)
            }

            // Dismiss result banner
            if (exportState is ExportState.Done ||
                exportState is ExportState.BothDone ||
                exportState is ExportState.Failed) {
                OutlinedButton(
                    onClick  = viewModel::dismissResult,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
                ) {
                    Text("Dismiss", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Divider
        Box(Modifier.width(1.dp).fillMaxHeight().background(DividerColor))

        // Right panel: preview
        ReportPreviewPane(
            config      = config,
            profile     = profile,
            result      = result,
            exportState = exportState,
            modifier    = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        )
    }
}