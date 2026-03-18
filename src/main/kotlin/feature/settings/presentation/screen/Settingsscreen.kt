package com.oussama_chatri.feature.settings.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.WellLogTheme
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.feature.settings.domain.model.AppSettings
import com.oussama_chatri.feature.settings.presentation.components.*
import com.oussama_chatri.feature.settings.presentation.viewmodel.SettingsViewModel
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = koinInject()
    val settings by viewModel.settings.collectAsState()
    val c = WellLogTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Appearance
        SectionCard(title = "Appearance") {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                ThemePicker(
                    selectedId = settings.themeId,
                    onSelect   = viewModel::setTheme
                )
            }
        }

        // Units & Display
        SectionCard(title = "Units & Display") {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

                UnitSystemSelector(
                    selected = settings.unitSystem,
                    onSelect = viewModel::setUnitSystem
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Decimal Precision",
                        style = MaterialTheme.typography.labelMedium,
                        color = c.textSecondary
                    )
                    Row(
                        verticalAlignment      = Alignment.CenterVertically,
                        horizontalArrangement  = Arrangement.spacedBy(12.dp)
                    ) {
                        Slider(
                            value         = settings.decimalPrecision.toFloat(),
                            onValueChange = { viewModel.setDecimalPrecision(it.toInt()) },
                            valueRange    = 0f..6f,
                            steps         = 5,
                            colors        = SliderDefaults.colors(
                                thumbColor       = c.accent,
                                activeTrackColor = c.accent
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${settings.decimalPrecision} dp",
                            style = MaterialTheme.typography.labelMedium,
                            color = c.accent,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }
            }
        }

        // File Paths
        SectionCard(title = "File Paths") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                DefaultPathPicker(
                    label    = "Default Export Folder",
                    path     = settings.defaultExportPath,
                    onChange = viewModel::setDefaultExportPath
                )
                DefaultPathPicker(
                    label    = "Default Project Folder",
                    path     = settings.defaultProjectPath,
                    onChange = viewModel::setDefaultProjectPath
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Auto-save Interval",
                        style = MaterialTheme.typography.labelMedium,
                        color = c.textSecondary
                    )
                    val options = listOf(0 to "Off", 5 to "5 min", 15 to "15 min", 30 to "30 min")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        options.forEach { (minutes, label) ->
                            val selected = settings.autoSaveIntervalMinutes == minutes
                            FilterChip(
                                selected = selected,
                                onClick  = { viewModel.setAutoSaveInterval(minutes) },
                                label    = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor  = c.accentDim,
                                    selectedLabelColor      = c.accent,
                                    selectedLeadingIconColor= c.accent
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled          = true,
                                    selected         = selected,
                                    borderColor      = c.divider,
                                    selectedBorderColor = c.accent
                                )
                            )
                        }
                    }
                }
            }
        }

        // About
        SectionCard(title = "About") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AboutRow("App Version",  "WellLogAnalyzer v1.0.0")
                AboutRow("Platform",     "Jetpack Compose Desktop · Kotlin 2.1")
                AboutRow("Theme Engine", "4 built-in themes — persisted locally")
            }
        }

        // Bottom actions
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = viewModel::resetToDefaults,
                colors  = ButtonDefaults.textButtonColors(contentColor = c.textMuted)
            ) {
                Text("Reset to Defaults", style = MaterialTheme.typography.labelMedium)
            }
            Text(
                "Changes are saved automatically",
                style = MaterialTheme.typography.labelSmall,
                color = c.textMuted
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    val c = WellLogTheme.colors
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = c.textSecondary)
        Text(value, style = MaterialTheme.typography.labelMedium, color = c.textPrimary)
    }
}