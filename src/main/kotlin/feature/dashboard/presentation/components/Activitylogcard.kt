package com.oussama_chatri.feature.dashboard.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val colorCardSurface = CardSurface
    val colorTextSec     = TextSecondary
    val colorTextMuted   = TextMuted
    val colorTeal        = TealSafe
    val colorAmber       = AmberGold

    // Pass captured colors into the plain fun so it doesn't need a Compose context
    val events = remember(projects) {
        buildActivityLog(projects, tealSafe = colorTeal, amberGold = colorAmber, textMuted = colorTextMuted)
    }

    Card(
        modifier  = modifier,
        colors    = CardDefaults.cardColors(containerColor = colorCardSurface),
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
                color = colorTextSec
            )
            Spacer(Modifier.height(10.dp))

            if (events.isEmpty()) {
                Text(
                    "No activity yet.",
                    style    = MaterialTheme.typography.bodySmall,
                    color    = colorTextMuted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    events.take(8).forEach { event ->
                        ActivityEventRow(event, textSecondary = colorTextSec, textMuted = colorTextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityEventRow(
    event: ActivityEvent,
    textSecondary: Color,
    textMuted: Color,
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector        = event.icon,
            contentDescription = null,
            tint               = event.tint,
            modifier           = Modifier.size(14.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                event.title,
                style    = MaterialTheme.typography.bodySmall,
                color    = textSecondary,
                maxLines = 1
            )
            Text(
                event.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = textMuted
            )
        }
    }
}

private data class ActivityEvent(
    val title: String,
    val timestamp: String,
    val icon: ImageVector,
    val tint: Color,
    val epochMs: Long
)

// ── Plain fun — colors passed as parameters, no @Composable context needed ──
private fun buildActivityLog(
    projects: List<ProjectSummary>,
    tealSafe: Color,
    amberGold: Color,
    textMuted: Color,
): List<ActivityEvent> {
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
                tint      = tealSafe,
                epochMs   = ts
            )
        }
        p.lastExportTimestamp?.let { ts ->
            events += ActivityEvent(
                title     = "${p.lastExportFormat ?: "Report"} exported — ${p.wellName}",
                timestamp = fmtEpoch(ts),
                icon      = Icons.Default.FileDownload,
                tint      = amberGold,
                epochMs   = ts
            )
        }
        events += ActivityEvent(
            title     = "Project created — ${p.wellName}",
            timestamp = fmtEpoch(p.createdAt),
            icon      = Icons.Default.Add,
            tint      = textMuted,
            epochMs   = p.createdAt
        )
    }
    return events.sortedByDescending { it.epochMs }
}