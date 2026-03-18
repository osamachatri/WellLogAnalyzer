package com.oussama_chatri.feature.reports.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*

@Composable
fun ExportFormatCard(
    icon:        ImageVector,
    iconTint:    Color,
    formatName:  String,
    description: String,
    buttonLabel: String,
    isExporting: Boolean,
    onExport:    () -> Unit,
    modifier:    Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(CardSurface)
            .border(1.dp, DividerColor, MaterialTheme.shapes.medium)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(iconTint.copy(alpha = 0.12f))
                .border(1.dp, iconTint.copy(alpha = 0.3f), MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = iconTint,
                modifier           = Modifier.size(28.dp)
            )
        }

        Text(
            text  = formatName,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )

        Text(
            text  = description,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        if (isExporting) {
            LinearProgressIndicator(
                modifier   = Modifier.fillMaxWidth(),
                color      = AmberGold,
                trackColor = DividerColor
            )
        } else {
            Button(
                onClick  = onExport,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = AmberGold,
                    contentColor   = NavyDeep
                )
            ) {
                Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(buttonLabel, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}