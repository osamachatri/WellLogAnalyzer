package com.oussama_chatri.feature.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*

@Composable
fun ThemePicker(
    selectedId: AppThemeId,
    onSelect:   (AppThemeId) -> Unit,
    modifier:   Modifier = Modifier
) {
    val c = WellLogTheme.colors

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Theme", style = MaterialTheme.typography.labelMedium, color = c.textSecondary)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppThemeId.entries.forEach { themeId ->
                ThemeCard(
                    themeId    = themeId,
                    isSelected = themeId == selectedId,
                    onSelect   = { onSelect(themeId) },
                    modifier   = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(
    themeId:    AppThemeId,
    isSelected: Boolean,
    onSelect:   () -> Unit,
    modifier:   Modifier = Modifier
) {
    val preview  = themeById(themeId)
    val c        = WellLogTheme.colors
    val borderColor = if (isSelected) c.accent else c.divider

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onSelect() }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mini preview of the theme colors
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(MaterialTheme.shapes.small)
                .background(preview.background)
        ) {
            // Sidebar stripe
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .fillMaxHeight()
                    .background(preview.sidebarBg)
            )
            // Card preview
            Box(
                modifier = Modifier
                    .padding(start = 18.dp, top = 6.dp)
                    .width(40.dp)
                    .height(18.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(preview.cardSurface)
            )
            // Accent dot
            Box(
                modifier = Modifier
                    .padding(start = 22.dp, top = 28.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(preview.accent)
            )
            // Teal dot
            Box(
                modifier = Modifier
                    .padding(start = 34.dp, top = 28.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(preview.safe)
            )

            // Selected checkmark
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(c.accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check, null,
                        tint     = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        Text(
            text  = themeId.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) c.accent else c.textSecondary
        )
    }
}