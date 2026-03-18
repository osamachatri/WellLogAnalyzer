package com.oussama_chatri.feature.viewer3d.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.CardSurface
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.core.util.NumberFormatter
import com.oussama_chatri.feature.viewer3d.domain.model.GeologyLayer
import com.oussama_chatri.feature.viewer3d.domain.model.Wellbore3DModel
import com.oussama_chatri.feature.viewer3d.presentation.util.lithologyColor

@Composable
fun GeologyLayerLegend(
    model: Wellbore3DModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(CardSurface.copy(alpha = 0.92f))
            .border(1.dp, DividerColor, MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text  = "Depth Legend",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(4.dp))

        model.geologyLayers.forEach { layer ->
            LegendRow(layer)
        }

        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.HorizontalDivider(color = DividerColor)
        Spacer(Modifier.height(4.dp))

        StatRow("Total Depth",   NumberFormatter.feet(model.totalDepth))
        StatRow("Max Reach",     NumberFormatter.feet(model.maxHorizontalReach))
        StatRow("Max Deviation", "${String.format("%.1f", model.maxInclination)}°")
    }
}

@Composable
private fun LegendRow(layer: GeologyLayer) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(lithologyColor(layer.lithology))
        )
        Text(
            text     = layer.name.ifBlank { layer.lithology.displayName },
            style    = MaterialTheme.typography.bodySmall,
            color    = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text  = "${layer.topDepth.toInt()}–${layer.bottomDepth.toInt()} ft",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}