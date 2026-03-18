package com.oussama_chatri.feature.charts2d.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.TextSecondary
import com.oussama_chatri.feature.charts2d.domain.model.ChartType

@Composable
fun ChartSelectorTabs(
    activeTab: ChartType,
    onTabSelected: (ChartType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        ChartType.entries.forEach { type ->
            val isActive = activeTab == type
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = { onTabSelected(type) },
                    colors  = ButtonDefaults.textButtonColors(
                        contentColor = if (isActive) AmberGold else TextSecondary
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 14.dp,
                        vertical   = 8.dp
                    )
                ) {
                    Text(type.label, style = MaterialTheme.typography.labelMedium)
                }

                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(if (isActive) 40.dp else 0.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(if (isActive) AmberGold else androidx.compose.ui.graphics.Color.Transparent)
                )
            }
        }
    }
}