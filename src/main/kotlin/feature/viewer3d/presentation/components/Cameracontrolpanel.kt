package com.oussama_chatri.feature.viewer3d.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.CardSurface
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.NavyDeep
import com.oussama_chatri.feature.viewer3d.domain.model.CameraPreset

@Composable
fun CameraControlPanel(
    activePreset: CameraPreset,
    onPreset: (CameraPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(CardSurface.copy(alpha = 0.92f))
            .border(1.dp, DividerColor, MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text  = "Camera:",
            style = MaterialTheme.typography.labelSmall,
            color = com.oussama_chatri.core.theme.TextMuted
        )

        CameraPreset.entries.forEach { preset ->
            val isActive = preset == activePreset
            OutlinedButton(
                onClick  = { onPreset(preset) },
                colors   = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isActive) AmberGold.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Transparent,
                    contentColor   = if (isActive) AmberGold else com.oussama_chatri.core.theme.TextSecondary
                ),
                border   = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isActive) AmberGold.copy(alpha = 0.6f) else DividerColor
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 10.dp,
                    vertical   = 4.dp
                )
            ) {
                Text(
                    text  = preset.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}