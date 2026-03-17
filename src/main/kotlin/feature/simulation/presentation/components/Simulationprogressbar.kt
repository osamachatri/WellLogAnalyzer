package com.oussama_chatri.feature.simulation.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.feature.simulation.domain.model.PressurePoint
import com.oussama_chatri.feature.simulation.domain.model.SimulationStatus

@Composable
fun SimulationProgressBar(
    status: SimulationStatus.Running,
    liveProfile: List<PressurePoint>,
    modifier: Modifier = Modifier
) {
    val animatedFraction by animateFloatAsState(
        targetValue    = status.progressFraction,
        animationSpec  = tween(durationMillis = 300),
        label          = "sim_progress"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text  = "Computing depth ${status.currentDepth.toInt()} / ${status.totalDepth.toInt()} ft",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text  = "${(animatedFraction * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = AmberGold
            )
        }

        LinearProgressIndicator(
            progress         = { animatedFraction },
            modifier         = Modifier.fillMaxWidth().height(6.dp),
            color            = AmberGold,
            trackColor       = DividerColor,
            strokeCap        = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Live mini ECD chart
        if (liveProfile.size >= 2) {
            LiveEcdMiniChart(
                profile  = liveProfile,
                modifier = Modifier.fillMaxWidth().height(140.dp)
            )
        }
    }
}

@Composable
private fun LiveEcdMiniChart(
    profile: List<PressurePoint>,
    modifier: Modifier = Modifier
) {
    val maxDepth = profile.maxOf { it.depth }.coerceAtLeast(1.0)
    val minEcd   = profile.minOf { it.ecd }.let { (it - 0.5).coerceAtLeast(0.0) }
    val maxEcd   = profile.maxOf { it.ecd } + 0.5

    Box(
        modifier = modifier
            .background(NavyDeep, MaterialTheme.shapes.small)
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Subtle grid
            repeat(4) { i ->
                val y = h * (i + 1) / 5f
                drawLine(DividerColor.copy(alpha = 0.3f), Offset(0f, y), Offset(w, y), 1f)
            }

            // ECD line
            val ecdPath = Path()
            profile.forEachIndexed { i, point ->
                val x = ((point.depth / maxDepth) * w).toFloat()
                val y = (((maxEcd - point.ecd) / (maxEcd - minEcd)) * h).toFloat().coerceIn(0f, h)
                if (i == 0) ecdPath.moveTo(x, y) else ecdPath.lineTo(x, y)
            }
            drawPath(ecdPath, TealSafe, style = Stroke(width = 2f))

            // Pore pressure reference line (dashed effect via short segments)
            val ppGrad = profile.lastOrNull()?.porePressure ?: return@Canvas
            val ppY = (((maxEcd - ppGrad) / (maxEcd - minEcd)) * h).toFloat().coerceIn(0f, h)
            var x = 0f
            while (x < w) {
                drawLine(
                    color       = ChartBlue.copy(alpha = 0.5f),
                    start       = Offset(x, ppY),
                    end         = Offset((x + 8f).coerceAtMost(w), ppY),
                    strokeWidth = 1.5f
                )
                x += 16f
            }
        }

        Text(
            text     = "ECD (live)",
            style    = MaterialTheme.typography.labelSmall,
            color    = TealSafe,
            modifier = Modifier.align(Alignment.TopStart).padding(2.dp)
        )
    }
}