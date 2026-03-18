package com.oussama_chatri.feature.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.StatusBadge
import com.oussama_chatri.core.ui.components.WellStatus
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RecentRunsTable(
    projects: List<ProjectSummary>,
    onOpen: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        colors    = CardDefaults.cardColors(containerColor = CardSurface),
        shape     = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = "Recent Projects",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(12.dp))

            // Header
            TableHeader()
            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 6.dp))

            if (projects.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Water, null,
                            tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No well projects yet. Create one to get started.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    projects.forEachIndexed { index, project ->
                        ProjectRow(
                            project  = project,
                            isEven   = index % 2 == 0,
                            onOpen   = { onOpen(project.id) },
                            onDelete = { onDelete(project.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeader() {
    val cols = listOf(
        "Well Name"   to 0.28f,
        "Depth (ft)"  to 0.13f,
        "Sims"        to 0.08f,
        "Last Run"    to 0.18f,
        "Max ECD"     to 0.12f,
        "Status"      to 0.13f,
        "Actions"     to 0.08f
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        cols.forEach { (label, weight) ->
            Text(
                text     = label,
                style    = MaterialTheme.typography.labelSmall,
                color    = TextSecondary,
                modifier = Modifier.weight(weight)
            )
        }
    }
}

@Composable
private fun ProjectRow(
    project: ProjectSummary,
    isEven: Boolean,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val bg = if (isEven) CardElevated.copy(alpha = 0.25f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Well name
        Text(
            project.wellName.ifBlank { "—" },
            style    = MaterialTheme.typography.bodySmall,
            color    = TextPrimary,
            maxLines = 1,
            modifier = Modifier.weight(0.28f)
        )
        // Depth
        Text(
            project.totalDepth.takeIf { it > 0 }?.let { "${it.toLong()}" } ?: "—",
            style    = MaterialTheme.typography.bodySmall,
            color    = TextSecondary,
            modifier = Modifier.weight(0.13f)
        )
        // Sim count
        Text(
            "${project.simulationCount}",
            style    = MaterialTheme.typography.bodySmall,
            color    = TextSecondary,
            modifier = Modifier.weight(0.08f)
        )
        // Last run
        Text(
            project.lastRunTimestamp?.let { formatShortDate(it) } ?: "Never",
            style    = MaterialTheme.typography.bodySmall,
            color    = TextSecondary,
            modifier = Modifier.weight(0.18f)
        )
        // Max ECD
        Text(
            project.maxEcd?.let { String.format("%.2f ppg", it) } ?: "—",
            style    = MaterialTheme.typography.bodySmall,
            color    = if (project.isEcdSafe == true) TealSafe else if (project.isEcdSafe == false) CoralDanger else TextMuted,
            modifier = Modifier.weight(0.12f)
        )
        // Status badge
        Box(modifier = Modifier.weight(0.13f)) {
            val status = when (project.isEcdSafe) {
                true  -> WellStatus.Safe
                false -> WellStatus.Warning
                null  -> WellStatus.NotRun
            }
            StatusBadge(status = status)
        }
        // Actions
        Row(modifier = Modifier.weight(0.08f)) {
            IconButton(onClick = onOpen, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.FolderOpen, "Open",
                    tint = AmberGold, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, "Delete",
                    tint = CoralDanger.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

private fun formatShortDate(epochMs: Long): String {
    val fmt = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return Instant.ofEpochMilli(epochMs)
        .atZone(ZoneId.systemDefault())
        .format(fmt)
}