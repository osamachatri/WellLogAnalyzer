package com.oussama_chatri.feature.simulation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.LabeledTextField
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.core.util.NumberFormatter
import com.oussama_chatri.feature.simulation.domain.model.SimulationStatus
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

@Composable
fun SimulationControlPanel(
    profile: WellProfile?,
    status: SimulationStatus,
    depthStep: Double,
    flowRateOverride: Double?,
    onDepthStepChange: (String) -> Unit,
    onFlowRateOverrideChange: (String) -> Unit,
    onRun: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = status is SimulationStatus.Running

    Column(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard(title = "Run Configuration") {
            LabeledTextField(
                label         = "Flow Rate Override",
                value         = flowRateOverride?.toString() ?: "",
                onValueChange = onFlowRateOverrideChange,
                unit          = "gpm",
                placeholder   = profile?.fluidProperties?.flowRate?.let {
                    NumberFormatter.format(it, 0)
                } ?: "—",
                keyboardType  = KeyboardType.Decimal
            )

            Spacer(Modifier.height(8.dp))

            LabeledTextField(
                label         = "Depth Step",
                value         = depthStep.let {
                    if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString()
                },
                onValueChange = onDepthStepChange,
                unit          = "ft",
                keyboardType  = KeyboardType.Decimal
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick  = onRun,
                enabled  = !isRunning && profile != null,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = AmberGold,
                    contentColor           = NavyDeep,
                    disabledContainerColor = CardElevated,
                    disabledContentColor   = TextMuted
                )
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Run Simulation", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick  = onStop,
                    enabled  = isRunning,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = CoralDanger),
                    border   = androidx.compose.foundation.BorderStroke(
                        1.dp, if (isRunning) CoralDanger.copy(alpha = 0.7f) else DividerColor
                    )
                ) {
                    Icon(Icons.Default.Stop, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Stop", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick  = onReset,
                    enabled  = !isRunning,
                    modifier = Modifier.weight(1f),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reset", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        if (profile != null) {
            SectionCard(title = "Well Summary") {
                WellSummaryRow("Well",       profile.wellName)
                WellSummaryRow("Total Depth", NumberFormatter.feet(profile.totalDepth))
                WellSummaryRow("Mud Weight",  NumberFormatter.ppg(profile.fluidProperties.mudWeight))
                WellSummaryRow("Flow Rate",   NumberFormatter.gpm(profile.fluidProperties.flowRate))
                WellSummaryRow("Bit Size",    NumberFormatter.inches(profile.bitParameters.bitSize))
            }
        }
    }
}

@Composable
private fun WellSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}