package com.oussama_chatri.feature.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun QuickStatsRow(
    projects: List<ProjectSummary>,
    modifier: Modifier = Modifier
) {
    val totalWells     = projects.size
    val totalSims      = projects.sumOf { it.simulationCount }
    val lastExport     = projects.mapNotNull { it.lastExportTimestamp }.maxOrNull()
    val activeWell     = projects.firstOrNull { it.lastRunTimestamp != null }
        ?: projects.firstOrNull()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier    = Modifier.weight(1f),
            icon        = Icons.Default.Water,
            value       = "$totalWells",
            label       = "Total Wells",
            sublabel    = "projects saved"
        )
        StatCard(
            modifier    = Modifier.weight(1f),
            icon        = Icons.Default.PlayCircle,
            value       = "$totalSims",
            label       = "Simulations Run",
            sublabel    = "all time"
        )
        StatCard(
            modifier    = Modifier.weight(1f),
            icon        = Icons.Default.FileDownload,
            value       = lastExport?.let { formatDate(it) } ?: "—",
            label       = "Last Export",
            sublabel    = projects.firstOrNull { it.lastExportTimestamp != null }
                ?.lastExportFormat ?: "No exports yet"
        )
        StatCard(
            modifier    = Modifier.weight(1f),
            icon        = Icons.Default.Hive,
            value       = activeWell?.wellName ?: "—",
            label       = "Active Well",
            sublabel    = activeWell?.lastRunTimestamp
                ?.let { "Last run: ${formatDate(it)}" } ?: "Not yet run",
            valueStyle  = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    sublabel: String,
    modifier: Modifier = Modifier,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineMedium
) {
    Card(
        modifier  = modifier,
        colors    = CardDefaults.cardColors(containerColor = CardSurface),
        shape     = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(AmberDim),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = AmberGold,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text  = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text     = value,
                    style    = valueStyle,
                    color    = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text  = sublabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

private fun formatDate(epochMs: Long): String {
    val fmt = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return Instant.ofEpochMilli(epochMs)
        .atZone(ZoneId.systemDefault())
        .format(fmt)
}