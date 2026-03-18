package com.oussama_chatri.feature.charts2d.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.CardSurface
import com.oussama_chatri.core.theme.CoralDanger
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.core.theme.TealSafe
import com.oussama_chatri.core.util.NumberFormatter
import com.oussama_chatri.feature.simulation.domain.model.SimulationResult

/**
 * Compact metric strip shown between the tab bar and the chart.
 * Displays the four key numbers from the simulation at a glance.
 */
@Composable
fun ResultsSummaryBar(
    result: SimulationResult,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(CardSurface)
            .border(1.dp, DividerColor, MaterialTheme.shapes.small)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        val ecdColor = when {
            result.isEcdSafe -> TealSafe
            else             -> CoralDanger
        }

        MetricCell(
            label = "Max ECD",
            value = NumberFormatter.ppg(result.maxEcd),
            color = ecdColor
        )

        VerticalDivider()

        MetricCell(
            label = "Max APL",
            value = NumberFormatter.psi(result.maxAnnularPressureLoss),
            color = AmberGold
        )

        VerticalDivider()

        MetricCell(
            label = "Bit ΔP",
            value = NumberFormatter.psi(result.bitPressureDrop),
            color = AmberGold
        )

        VerticalDivider()

        MetricCell(
            label = "Surface Pressure",
            value = NumberFormatter.psi(result.totalSurfacePressure),
            color = AmberGold
        )

        VerticalDivider()

        MetricCell(
            label = "HSI",
            value = NumberFormatter.hsi(result.hsi),
            color = when {
                result.hsi in 1.0..1.5 -> TealSafe
                result.hsi > 0         -> AmberGold
                else                   -> TextMuted
            }
        )
    }
}

@Composable
private fun MetricCell(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text  = value,
            style = MaterialTheme.typography.titleSmall,
            color = color
        )
    }
}

@Composable
private fun VerticalDivider() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
            .height(32.dp)
            .background(DividerColor)
            .then(androidx.compose.ui.Modifier.padding(horizontal = 1.dp))
    )
}