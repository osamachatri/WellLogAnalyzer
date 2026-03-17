package com.oussama_chatri.feature.simulation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.util.NumberFormatter
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult

@Composable
fun ResultsSummaryCard(
    result: SimulationResult,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text  = "Results Summary",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val ecdColor = when {
                result.maxEcd <= result.formationZones.minOfOrNull { it.porePressureGradient } ?: 0.0 -> CoralDanger
                result.isEcdSafe -> TealSafe
                else             -> AmberWarning
            }

            MetricCard(
                label    = "Max ECD",
                value    = NumberFormatter.ppg(result.maxEcd),
                accent   = ecdColor,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label    = "Max Annular Pressure Loss",
                value    = NumberFormatter.psi(result.maxAnnularPressureLoss),
                accent   = AmberGold,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                label    = "Bit Pressure Drop",
                value    = NumberFormatter.psi(result.bitPressureDrop),
                accent   = ChartBlue,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label    = "Total Surface Pressure",
                value    = NumberFormatter.psi(result.totalSurfacePressure),
                accent   = TextSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallMetricChip("HSI",     NumberFormatter.hsi(result.hsi),             Modifier.weight(1f))
            SmallMetricChip("HHP",     NumberFormatter.hp(result.hydraulicHorsepower), Modifier.weight(1f))
            SmallMetricChip("Impact",  NumberFormatter.lbf(result.impactForce),     Modifier.weight(1f))
            SmallMetricChip("Surge",   NumberFormatter.psi(result.surgePressure),   Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = CardSurface),
        shape    = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(accent)
            )
            Text(
                text  = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SmallMetricChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(CardElevated)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text  = value,
            style = MaterialTheme.typography.labelLarge,
            color = AmberGold
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}