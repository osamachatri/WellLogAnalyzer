package com.oussama_chatri.feature.charts2d.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary

@Composable
fun NoResultsPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.BarChart,
                contentDescription = null,
                tint               = DividerColor,
                modifier           = Modifier.size(64.dp)
            )
            Text(
                text  = "No Simulation Results",
                style = MaterialTheme.typography.headlineSmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Run a simulation from the Simulation screen to populate the charts.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}