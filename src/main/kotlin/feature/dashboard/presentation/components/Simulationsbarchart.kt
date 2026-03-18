package com.oussama_chatri.feature.dashboard.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.feature.dashboard.domain.model.ProjectSummary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun SimulationsBarChart(
    projects: List<ProjectSummary>,
    modifier: Modifier = Modifier
) {
    // Count simulations per day for the last 14 days
    val dailyCounts = remember(projects) { buildDailyCounts(projects) }
    val maxCount    = dailyCounts.maxOfOrNull { it.second } ?: 1

    Card(
        modifier  = modifier,
        colors    = CardDefaults.cardColors(containerColor = CardSurface),
        shape     = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text  = "Simulations This Month",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(12.dp))

            if (dailyCounts.all { it.second == 0 }) {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No simulations yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    val barCount   = dailyCounts.size
                    val gap        = 4.dp.toPx()
                    val totalGap   = gap * (barCount - 1)
                    val barWidth   = (size.width - totalGap) / barCount
                    val chartH     = size.height - 20.dp.toPx() // leave bottom room for labels

                    dailyCounts.forEachIndexed { i, (_, count) ->
                        val fraction  = count.toFloat() / maxCount.coerceAtLeast(1)
                        val barH      = (chartH * fraction).coerceAtLeast(if (count > 0) 4.dp.toPx() else 0f)
                        val x         = i * (barWidth + gap)
                        val y         = chartH - barH

                        drawRoundRect(
                            color        = if (count > 0) AmberGold else DividerColor,
                            topLeft      = Offset(x, y),
                            size         = Size(barWidth, barH),
                            cornerRadius = CornerRadius(3.dp.toPx())
                        )
                    }
                }

                // Day labels (every other day)
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dailyCounts.forEachIndexed { i, (date, _) ->
                        if (i % 3 == 0) {
                            Text(
                                text  = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        } else {
                            Spacer(Modifier.width(1.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun buildDailyCounts(projects: List<ProjectSummary>): List<Pair<LocalDate, Int>> {
    val today   = LocalDate.now()
    val zone    = ZoneId.systemDefault()

    // Map each run to its local date
    val runDates = projects
        .filter { it.lastRunTimestamp != null && it.simulationCount > 0 }
        .flatMap { p ->
            // Use lastRunTimestamp as a proxy — one count per project per day
            listOf(
                Instant.ofEpochMilli(p.lastRunTimestamp!!)
                    .atZone(zone).toLocalDate()
            )
        }

    return (0 until 14).map { daysBack ->
        val day   = today.minusDays(daysBack.toLong())
        val count = runDates.count { it == day }
        day to count
    }.reversed()
}