package com.oussama_chatri.feature.dashboard.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ActivityLogCard(
    projects: List<ProjectSummary>,
    modifier: Modifier = Modifier
) {
    // Build a flat activity log from project metadata — most-recent first
    val events = remember(projects) { buildActivityLog(projects) }

    Card(
        modifier  = modifier,
        colors    = CardDefaults.cardColors(containerColor = CardSurface),
        shape     = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text  = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(10.dp))

            if (events.isEmpty()) {
                Text(
                    "No activity yet.",
                    style    = MaterialTheme.typography.bodySmall,
                    color    = TextMuted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    events.take(8).forEach { event ->
                        ActivityEventRow(event)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityEventRow(event: ActivityEvent) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector        = event.icon,
            contentDescription = null,
            tint               = event.tint,
            modifier           = Modifier.size(14.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title,
                style    = MaterialTheme.typography.bodySmall,
                color    = TextSecondary,
                maxLines = 1)
            Text(event.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted)
        }
    }
}

private data class ActivityEvent(
    val title: String,
    val timestamp: String,
    val icon: ImageVector,
    val tint: androidx.compose.ui.graphics.Color,
    val epochMs: Long
)

private fun buildActivityLog(projects: List<ProjectSummary>): List<ActivityEvent> {
    val fmt = DateTimeFormatter.ofPattern("MMM d, HH:mm")
    fun fmtEpoch(ms: Long) = Instant.ofEpochMilli(ms)
        .atZone(ZoneId.systemDefault()).format(fmt)

    val events = mutableListOf<ActivityEvent>()
    projects.forEach { p ->
        p.lastRunTimestamp?.let { ts ->
            events += ActivityEvent(
                title     = "Simulation run — ${p.wellName}",
                timestamp = fmtEpoch(ts),
                icon      = Icons.Default.PlayCircle,
                tint      = TealSafe,
                epochMs   = ts
            )
        }
        p.lastExportTimestamp?.let { ts ->
            events += ActivityEvent(
                title     = "${p.lastExportFormat ?: "Report"} exported — ${p.wellName}",
                timestamp = fmtEpoch(ts),
                icon      = Icons.Default.FileDownload,
                tint      = AmberGold,
                epochMs   = ts
            )
        }
        events += ActivityEvent(
            title     = "Project created — ${p.wellName}",
            timestamp = fmtEpoch(p.createdAt),
            icon      = Icons.Default.Add,
            tint      = TextMuted,
            epochMs   = p.createdAt
        )
    }
    return events.sortedByDescending { it.epochMs }
}