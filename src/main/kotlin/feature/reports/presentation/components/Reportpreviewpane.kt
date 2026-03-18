package com.oussama_chatri.feature.reports.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.core.util.NumberFormatter
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.reports.domain.model.ReportSection
import com.oussama_chatri.feature.reports.presentation.viewmodel.ExportState
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

@Composable
fun ReportPreviewPane(
    config:      ReportConfig,
    profile:     WellProfile?,
    result:      SimulationResult?,
    exportState: ExportState,
    modifier:    Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Document outline tree
        SectionCard(title = "Report Outline Preview") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

                OutlineItem(icon = "📄", text = "WellLogAnalyzer Report", depth = 0, bold = true)

                val sections = config.sections.sortedBy { it.ordinal }
                var sectionNum = 1

                sections.forEach { section ->
                    OutlineItem(
                        icon  = sectionIcon(section),
                        text  = "$sectionNum. ${section.displayName}",
                        depth = 1
                    )
                    // Sub-items
                    sectionSubItems(section, profile, result).forEach { sub ->
                        OutlineItem(icon = "·", text = sub, depth = 2, muted = true)
                    }
                    sectionNum++
                }

                if (sections.isEmpty()) {
                    Text(
                        text  = "No sections selected.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        // Export progress / status
        SectionCard(title = "Export Status") {
            when (val state = exportState) {
                is ExportState.Idle -> {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Description, null,
                            tint = TextMuted, modifier = Modifier.size(16.dp))
                        Text("Ready to export",
                            style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }

                is ExportState.Exporting -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Exporting ${state.format.displayName}…",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberGold
                        )
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color    = AmberGold,
                            trackColor = DividerColor
                        )
                    }
                }

                is ExportState.Done -> {
                    SuccessStatus(
                        label    = "${state.format.displayName} exported",
                        filePath = state.filePath
                    )
                }

                is ExportState.BothDone -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SuccessStatus(label = "Excel exported", filePath = state.excelPath)
                        SuccessStatus(label = "Word exported",  filePath = state.wordPath)
                    }
                }

                is ExportState.Failed -> {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(CoralDanger)
                        )
                        Text(
                            text  = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = CoralDanger
                        )
                    }
                }
            }
        }

        // Quick stats if result available
        if (result != null) {
            SectionCard(title = "Simulation Summary") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuickStat("Max ECD",         NumberFormatter.ppg(result.maxEcd))
                    QuickStat("Max APL",          NumberFormatter.psi(result.maxAnnularPressureLoss))
                    QuickStat("Bit ΔP",           NumberFormatter.psi(result.bitPressureDrop))
                    QuickStat("Surface Pressure", NumberFormatter.psi(result.totalSurfacePressure))
                    QuickStat("HSI",              NumberFormatter.hsi(result.hsi))
                    QuickStat("Status",
                        if (result.isEcdSafe) "SAFE ✓" else "⚠ WARNING",
                        valueColor = if (result.isEcdSafe) TealSafe else AmberWarning
                    )
                }
            }
        }
    }
}

@Composable
private fun OutlineItem(
    icon:  String,
    text:  String,
    depth: Int,
    bold:  Boolean = false,
    muted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (depth * 16).dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(icon, style = MaterialTheme.typography.bodySmall, color = AmberGold)
        Text(
            text  = text,
            style = if (bold) MaterialTheme.typography.titleSmall
            else      MaterialTheme.typography.bodySmall,
            color = when {
                muted -> TextMuted
                bold  -> TextPrimary
                else  -> TextSecondary
            }
        )
    }
}

@Composable
private fun SuccessStatus(label: String, filePath: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.Default.CheckCircle, null,
                tint = TealSafe, modifier = Modifier.size(14.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = TealSafe)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 20.dp)
        ) {
            Icon(Icons.Default.FolderOpen, null,
                tint = TextMuted, modifier = Modifier.size(12.dp))
            Text(
                text  = filePath,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun QuickStat(
    label:      String,
    value:      String,
    valueColor: androidx.compose.ui.graphics.Color = AmberGold
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.labelMedium, color = valueColor)
    }
}

private fun sectionIcon(section: ReportSection) = when (section) {
    ReportSection.COVER_PAGE         -> "📋"
    ReportSection.EXECUTIVE_SUMMARY  -> "📝"
    ReportSection.INPUT_PARAMETERS   -> "⚙️"
    ReportSection.SIMULATION_RESULTS -> "📊"
    ReportSection.PRESSURE_CHARTS    -> "📈"
    ReportSection.ECD_PROFILE        -> "🔬"
    ReportSection.FORMATION_ZONES    -> "🗺️"
    ReportSection.GEOLOGY_SUMMARY    -> "🪨"
    ReportSection.APPENDIX_RAW_DATA  -> "📑"
}

private fun sectionSubItems(
    section: ReportSection,
    profile: WellProfile?,
    result:  SimulationResult?
): List<String> = when (section) {
    ReportSection.COVER_PAGE         -> listOf("Well name, date, engineer, company")
    ReportSection.EXECUTIVE_SUMMARY  -> listOf(
        "Well overview",
        "Simulation status: ${if (result?.isEcdSafe == true) "SAFE ✓" else "—"}",
        "KPI summary block"
    )
    ReportSection.INPUT_PARAMETERS   -> listOf("3.1 Drill String", "3.2 Bit Parameters", "3.3 Fluid Properties")
    ReportSection.SIMULATION_RESULTS -> listOf(
        "Max ECD: ${result?.let { String.format("%.2f ppg", it.maxEcd) } ?: "—"}",
        "Pressure profile table (sampled)",
        "Surge & swab summary"
    )
    ReportSection.PRESSURE_CHARTS    -> listOf("Depth vs. pressure table")
    ReportSection.ECD_PROFILE        -> listOf("ECD vs. depth table")
    ReportSection.FORMATION_ZONES    -> listOf("${profile?.formationZones?.size ?: 0} zones defined")
    ReportSection.GEOLOGY_SUMMARY    -> listOf("Lithology summary")
    ReportSection.APPENDIX_RAW_DATA  -> listOf("Raw pressure point data")
}