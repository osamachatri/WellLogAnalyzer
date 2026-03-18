package com.oussama_chatri.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.AppThemeId
import com.oussama_chatri.core.theme.WellLogTheme
import com.oussama_chatri.core.theme.themeById
import com.oussama_chatri.feature.settings.presentation.viewmodel.SettingsViewModel
import org.koin.compose.koinInject

@Composable
fun TopBar(
    screenTitle: String,
    modifier:    Modifier = Modifier,
    actions:     @Composable (() -> Unit)? = null
) {
    val c          = WellLogTheme.colors
    val settingsVm = koinInject<SettingsViewModel>()
    val settings   by settingsVm.settings.collectAsState()
    var showPicker by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(c.cardSurface)
            .padding(horizontal = 20.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text  = "WellLogAnalyzer",
            style = MaterialTheme.typography.titleMedium,
            color = c.accent
        )

        Text(
            text  = screenTitle,
            style = MaterialTheme.typography.titleMedium,
            color = c.textPrimary
        )

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            actions?.invoke()

            // Theme quick-switcher — palette icon + dropdown
            Box {
                IconButton(
                    onClick  = { showPicker = !showPicker },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Palette,
                        contentDescription = "Switch theme",
                        tint               = c.textSecondary,
                        modifier           = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded         = showPicker,
                    onDismissRequest = { showPicker = false },
                    modifier         = Modifier
                        .background(c.cardSurface)
                        .width(220.dp)
                ) {
                    Text(
                        text     = "Select theme",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = c.textMuted,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    HorizontalDivider(
                        color    = c.divider,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    AppThemeId.entries.forEach { themeId ->
                        val preview    = themeById(themeId)
                        val isSelected = settings.themeId == themeId

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // 3-dot colour preview
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(11.dp)
                                                .clip(CircleShape)
                                                .background(preview.background)
                                                .border(0.5.dp, c.divider, CircleShape)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(11.dp)
                                                .clip(CircleShape)
                                                .background(preview.accent)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(11.dp)
                                                .clip(CircleShape)
                                                .background(preview.safe)
                                        )
                                    }
                                    Text(
                                        text  = themeId.displayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) c.accent else c.textSecondary
                                    )
                                    if (isSelected) {
                                        Spacer(Modifier.weight(1f))
                                        Text("●", style = MaterialTheme.typography.labelSmall, color = c.accent)
                                    }
                                }
                            },
                            onClick = {
                                settingsVm.setTheme(themeId)
                                showPicker = false
                            },
                            modifier = Modifier.background(
                                if (isSelected) c.accentDim else Color.Transparent
                            ),
                            colors = MenuDefaults.itemColors(
                                textColor = if (isSelected) c.accent else c.textSecondary
                            )
                        )
                    }
                }
            }

            // Logo avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(c.accent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource("icons/app_icon.png"),
                    contentDescription = "Logo"
                )
            }
        }
    }
}