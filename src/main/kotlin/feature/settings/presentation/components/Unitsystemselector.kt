package com.oussama_chatri.feature.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.WellLogTheme
import com.oussama_chatri.feature.settings.domain.model.UnitSystem

@Composable
fun UnitSystemSelector(
    selected: UnitSystem,
    onSelect: (UnitSystem) -> Unit,
    modifier: Modifier = Modifier
) {
    val c = WellLogTheme.colors

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Unit System", style = MaterialTheme.typography.labelMedium, color = c.textSecondary)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            UnitSystem.entries.forEach { system ->
                val isSelected = system == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.small)
                        .background(if (isSelected) c.accentDim else c.cardSurface)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) c.accent else c.divider,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable { onSelect(system) }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = system.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) c.accent else c.textSecondary
                    )
                }
            }
        }
    }
}