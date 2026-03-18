package com.oussama_chatri.feature.charts2d.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.feature.charts2d.domain.model.ChartSeries

@Composable
fun ChartLegendItem(
    series: ChartSeries,
    isVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable { onToggle() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .alpha(if (isVisible) 1f else 0.4f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (series.isDashed) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Box(
                    modifier = Modifier
                        .size(width = 8.dp, height = 2.dp)
                        .background(series.color)
                )
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = 2.dp)
                        .background(series.color)
                )
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = 2.dp)
                        .background(series.color)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 2.dp)
                    .background(series.color)
            )
        }

        Text(
            text  = series.label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}