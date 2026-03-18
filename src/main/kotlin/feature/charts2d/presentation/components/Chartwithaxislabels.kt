package com.oussama_chatri.feature.charts2d.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.TextMuted

/**
 * Wraps a chart composable and adds rotated Y-axis and bottom X-axis title labels.
 */
@Composable
fun ChartWithAxisLabels(
    xLabel: String,
    yLabel: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Rotated Y-axis label on the left
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = yLabel,
                style    = MaterialTheme.typography.labelSmall,
                color    = TextMuted,
                modifier = Modifier.rotate(-90f)
            )
        }

        // Chart content — occupies the full space; canvas insets handle actual padding
        content()

        // X-axis label at the bottom center
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text  = xLabel,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}