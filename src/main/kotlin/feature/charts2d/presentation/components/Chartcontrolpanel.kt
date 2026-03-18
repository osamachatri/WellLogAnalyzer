package com.oussama_chatri.feature.charts2d.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.NavyDeep
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.core.ui.components.ParameterSlider
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.feature.charts2d.domain.model.ChartDataSet
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult

@Composable
fun ChartControlPanel(
    dataSet: ChartDataSet?,
    result: SimulationResult?,
    seriesVisibility: Map<String, Boolean>,
    depthRangeMin: Float,
    depthRangeMax: Float,
    onToggleSeries: (String) -> Unit,
    onDepthMinChange: (Float) -> Unit,
    onDepthMaxChange: (Float) -> Unit,
    onExportPng: () -> Unit,
    onAddToReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDepth = result?.pressureProfile?.lastOrNull()?.depth ?: 0.0

    Column(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Series visibility toggles
        if (dataSet != null && dataSet.series.isNotEmpty()) {
            SectionCard(title = "Data Series") {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    dataSet.series.forEach { series ->
                        ChartLegendItem(
                            series    = series,
                            isVisible = seriesVisibility[series.id] != false,
                            onToggle  = { onToggleSeries(series.id) }
                        )
                    }
                }
            }
        }

        if (dataSet?.depthInverted == true && totalDepth > 0.0) {
            SectionCard(title = "Depth Range") {
                val minDepth = (depthRangeMin * totalDepth).toInt()
                val maxDepth = (depthRangeMax * totalDepth).toInt()

                ParameterSlider(
                    label        = "From",
                    value        = depthRangeMin,
                    valueRange   = 0f..1f,
                    onValueChange = onDepthMinChange,
                    displayValue = "$minDepth ft"
                )

                Spacer(Modifier.height(8.dp))

                ParameterSlider(
                    label        = "To",
                    value        = depthRangeMax,
                    valueRange   = 0f..1f,
                    onValueChange = onDepthMaxChange,
                    displayValue = "$maxDepth ft"
                )
            }
        }

        // Key depths card
        if (result != null && result.formationZones.isNotEmpty()) {
            SectionCard(title = "Key Depths") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    result.formationZones.forEach { zone ->
                        KeyDepthRow(
                            label = zone.zoneName,
                            depth = zone.topDepth
                        )
                    }
                    result.pressureProfile.lastOrNull()?.let { last ->
                        KeyDepthRow(label = "TD", depth = last.depth)
                    }
                }
            }
        }

        HorizontalDivider(color = DividerColor)

        // Actions
        OutlinedButton(
            onClick  = onExportPng,
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
            border   = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.6f))
        ) {
            Icon(
                imageVector        = Icons.Default.Download,
                contentDescription = null,
                modifier           = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Export as PNG", style = MaterialTheme.typography.labelMedium)
        }

        Button(
            onClick  = onAddToReport,
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.buttonColors(
                containerColor = AmberGold,
                contentColor   = NavyDeep
            )
        ) {
            Icon(
                imageVector        = Icons.Default.PlaylistAdd,
                contentDescription = null,
                modifier           = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Add to Report", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun KeyDepthRow(label: String, depth: Double) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text  = "${depth.toInt()} ft",
            style = MaterialTheme.typography.bodySmall,
            color = AmberGold
        )
    }
}