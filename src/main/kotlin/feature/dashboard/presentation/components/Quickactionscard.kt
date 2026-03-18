package com.oussama_chatri.feature.dashboard.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*

@Composable
fun QuickActionsCard(
    onNewWell: () -> Unit,
    onOpenLastReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        colors    = CardDefaults.cardColors(containerColor = CardSurface),
        shape     = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text  = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(2.dp))

            QuickActionButton(
                label    = "New Well Project",
                icon     = Icons.Default.Add,
                filled   = true,
                onClick  = onNewWell
            )
            QuickActionButton(
                label    = "Export Last Report",
                icon     = Icons.Default.FileDownload,
                filled   = false,
                onClick  = onOpenLastReport
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (filled) {
        Button(
            onClick  = onClick,
            modifier = modifier.fillMaxWidth().height(44.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = AmberGold,
                contentColor   = NavyDeep
            ),
            shape    = MaterialTheme.shapes.small
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    } else {
        OutlinedButton(
            onClick  = onClick,
            modifier = modifier.fillMaxWidth().height(44.dp),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
            border   = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f)),
            shape    = MaterialTheme.shapes.small
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}