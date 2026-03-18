package com.oussama_chatri.feature.reports.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.LabeledTextField
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.feature.reports.domain.model.ReportConfig
import com.oussama_chatri.feature.reports.domain.model.ReportSection

@Composable
fun ReportConfigPanel(
    config:           ReportConfig,
    onTitleChange:    (String) -> Unit,
    onEngineerChange: (String) -> Unit,
    onCompanyChange:  (String) -> Unit,
    onDateChange:     (String) -> Unit,
    onToggleSection:  (ReportSection) -> Unit,
    onSelectAll:      () -> Unit,
    onClearAll:       () -> Unit,
    modifier:         Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Report details
        SectionCard(title = "Report Details") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabeledTextField(
                    label         = "Report Title",
                    value         = config.reportTitle,
                    onValueChange = onTitleChange,
                    placeholder   = "Hydraulics Analysis Report"
                )
                LabeledTextField(
                    label         = "Engineer Name",
                    value         = config.engineerName,
                    onValueChange = onEngineerChange,
                    placeholder   = "Your name"
                )
                LabeledTextField(
                    label         = "Company Name",
                    value         = config.companyName,
                    onValueChange = onCompanyChange,
                    placeholder   = "Your company"
                )
                LabeledTextField(
                    label         = "Date",
                    value         = config.date,
                    onValueChange = onDateChange,
                    placeholder   = "Mar 18, 2026"
                )
            }
        }

        // Section selector
        SectionCard(title = "Include Sections") {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                // Select all / clear
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onSelectAll,
                        colors  = ButtonDefaults.textButtonColors(contentColor = AmberGold)
                    ) { Text("Select All", style = MaterialTheme.typography.labelSmall) }

                    TextButton(
                        onClick = onClearAll,
                        colors  = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                    ) { Text("Clear All", style = MaterialTheme.typography.labelSmall) }
                }

                HorizontalDivider(color = DividerColor)
                Spacer(Modifier.height(4.dp))

                ReportSection.entries.forEach { section ->
                    SectionToggleRow(
                        label     = section.displayName,
                        checked   = section in config.sections,
                        onToggle  = { onToggleSection(section) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionToggleRow(
    label:    String,
    checked:  Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(if (checked) AmberGold else TextMuted.copy(alpha = 0.3f))
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (checked) TextSecondary else TextMuted
            )
        }
        Checkbox(
            checked         = checked,
            onCheckedChange = { onToggle() },
            colors          = CheckboxDefaults.colors(
                checkedColor   = AmberGold,
                checkmarkColor = NavyDeep
            ),
            modifier = Modifier.size(20.dp)
        )
    }
}