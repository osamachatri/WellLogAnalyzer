package com.oussama_chatri.feature.viewer3d.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.CardSurface
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.feature.viewer3d.domain.model.GeologyLayer
import com.oussama_chatri.feature.viewer3d.domain.model.ViewerSettings
import com.oussama_chatri.feature.viewer3d.presentation.util.lithologyColor

@Composable
fun LayerVisibilityPanel(
    layers: List<GeologyLayer>,
    settings: ViewerSettings,
    onToggleLayer: (String) -> Unit,
    onToggleWellTube: () -> Unit,
    onToggleEcdMap: () -> Unit,
    onToggleLabels: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(CardSurface.copy(alpha = 0.92f))
            .border(1.dp, DividerColor, MaterialTheme.shapes.medium)
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text  = "Layer Visibility",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Fixed scene elements
        ToggleRow(
            label     = "Well Tube",
            checked   = settings.showWellTube,
            dotColor  = AmberGold,
            onToggle  = onToggleWellTube
        )
        ToggleRow(
            label     = "ECD Color Map",
            checked   = settings.showEcdColorMap,
            dotColor  = Color(0xFF2EC4B6),
            onToggle  = onToggleEcdMap
        )
        ToggleRow(
            label     = "Formation Labels",
            checked   = settings.showFormationLabels,
            dotColor  = TextMuted,
            onToggle  = onToggleLabels
        )

        if (layers.isNotEmpty()) {
            HorizontalDivider(
                color    = DividerColor,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            Text(
                text  = "Formations",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            layers.forEach { layer ->
                ToggleRow(
                    label    = layer.name.ifBlank { layer.lithology.displayName },
                    checked  = settings.layerVisibility[layer.id] != false,
                    dotColor = lithologyColor(layer.lithology),
                    onToggle = { onToggleLayer(layer.id) }
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    dotColor: Color,
    onToggle: () -> Unit
) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(if (checked) dotColor else TextMuted.copy(alpha = 0.4f))
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (checked) TextSecondary else TextMuted
            )
        }

        Switch(
            checked          = checked,
            onCheckedChange  = { onToggle() },
            modifier         = Modifier.size(width = 36.dp, height = 20.dp),
            colors           = SwitchDefaults.colors(
                checkedThumbColor       = dotColor,
                checkedTrackColor       = dotColor.copy(alpha = 0.3f),
                uncheckedThumbColor     = TextMuted,
                uncheckedTrackColor     = DividerColor
            )
        )
    }
}