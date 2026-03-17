package com.oussama_chatri.feature.wellinput.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.LabeledTextField
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.feature.wellinput.domain.model.DrillString
import com.oussama_chatri.feature.wellinput.domain.model.DrillStringSection
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile
import com.oussama_chatri.feature.wellinput.domain.usecase.ValidationResult
import java.util.UUID

@Composable
fun DrillStringPanel(
    profile: WellProfile,
    validationResult: ValidationResult?,
    onWellNameChange: (String) -> Unit,
    onTotalDepthChange: (String) -> Unit,
    onCasingOdChange: (String) -> Unit,
    onCasingIdChange: (String) -> Unit,
    onDrillPipeOdChange: (String) -> Unit,
    onDrillPipeIdChange: (String) -> Unit,
    onDrillCollarOdChange: (String) -> Unit,
    onDrillCollarLengthChange: (String) -> Unit,
    onAddSection: (DrillStringSection) -> Unit,
    onUpdateSection: (Int, DrillStringSection) -> Unit,
    onRemoveSection: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSectionDialog by remember { mutableStateOf(false) }
    var editingSection by remember { mutableStateOf<Pair<Int, DrillStringSection>?>(null) }

    val ds = profile.drillString

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Well Identity
        SectionCard(title = "Well Identity") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledTextField(
                    label        = "Well Name",
                    value        = profile.wellName,
                    onValueChange = onWellNameChange,
                    placeholder  = "e.g. Well-07A",
                    errorMessage = validationResult?.errors
                        ?.filterIsInstance<com.oussama_chatri.feature.wellinput.domain.usecase.ValidationError.WellNameEmpty>()
                        ?.firstOrNull()?.message,
                    modifier     = Modifier.weight(1f)
                )
                LabeledTextField(
                    label        = "Total Depth",
                    value        = if (profile.totalDepth == 0.0) "" else profile.totalDepth.toString(),
                    onValueChange = onTotalDepthChange,
                    unit         = "ft",
                    placeholder  = "0.0",
                    keyboardType = KeyboardType.Decimal,
                    errorMessage = validationResult?.errors
                        ?.filterIsInstance<com.oussama_chatri.feature.wellinput.domain.usecase.ValidationError.TotalDepthInvalid>()
                        ?.firstOrNull()?.message,
                    modifier     = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledTextField(
                    label         = "Casing OD",
                    value         = if (profile.casingOd == 0.0) "" else profile.casingOd.toString(),
                    onValueChange = onCasingOdChange,
                    unit          = "in",
                    placeholder   = "0.000",
                    keyboardType  = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f)
                )
                LabeledTextField(
                    label         = "Casing ID",
                    value         = if (profile.casingId == 0.0) "" else profile.casingId.toString(),
                    onValueChange = onCasingIdChange,
                    unit          = "in",
                    placeholder   = "0.000",
                    keyboardType  = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f)
                )
            }
        }

        // Drill String Geometry
        SectionCard(title = "Drill String Geometry") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledTextField(
                    label         = "Drill Pipe OD",
                    value         = if (ds.drillPipeOd == 0.0) "" else ds.drillPipeOd.toString(),
                    onValueChange = onDrillPipeOdChange,
                    unit          = "in",
                    keyboardType  = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f)
                )
                LabeledTextField(
                    label         = "Drill Pipe ID",
                    value         = if (ds.drillPipeId == 0.0) "" else ds.drillPipeId.toString(),
                    onValueChange = onDrillPipeIdChange,
                    unit          = "in",
                    keyboardType  = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledTextField(
                    label         = "Drill Collar OD",
                    value         = if (ds.drillCollarOd == 0.0) "" else ds.drillCollarOd.toString(),
                    onValueChange = onDrillCollarOdChange,
                    unit          = "in",
                    keyboardType  = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f)
                )
                LabeledTextField(
                    label         = "Drill Collar Length",
                    value         = if (ds.drillCollarLength == 0.0) "" else ds.drillCollarLength.toString(),
                    onValueChange = onDrillCollarLengthChange,
                    unit          = "ft",
                    keyboardType  = KeyboardType.Decimal,
                    modifier      = Modifier.weight(1f)
                )
            }
        }

        // Drill String Sections Table
        SectionCard(title = "Drill String Sections") {
            // Header row
            DrillStringSectionHeader()
            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))

            if (ds.sections.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "No sections added yet. Click \"Add Section\" below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            } else {
                ds.sections.forEachIndexed { index, section ->
                    DrillStringSectionRow(
                        index   = index + 1,
                        section = section,
                        onEdit  = { editingSection = Pair(index, section); showSectionDialog = true },
                        onDelete = { onRemoveSection(index) }
                    )
                    if (index < ds.sections.lastIndex) {
                        HorizontalDivider(color = DividerColor.copy(alpha = 0.4f))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { editingSection = null; showSectionDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.6f))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add Section")
            }
        }
    }

    // Section edit dialog
    if (showSectionDialog) {
        DrillStringSectionDialog(
            initial  = editingSection?.second,
            onConfirm = { section ->
                val existingIndex = editingSection?.first
                if (existingIndex != null) onUpdateSection(existingIndex, section)
                else onAddSection(section)
                showSectionDialog = false
                editingSection = null
            },
            onDismiss = { showSectionDialog = false; editingSection = null }
        )
    }
}

@Composable
private fun DrillStringSectionHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Section" to 0.35f, "OD (in)" to 0.15f, "ID (in)" to 0.15f,
            "Length (ft)" to 0.15f, "Wt (lb/ft)" to 0.15f, "" to 0.05f)
            .forEach { (label, weight) ->
                Text(
                    text     = label,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = TextSecondary,
                    modifier = Modifier.weight(weight)
                )
            }
    }
}

@Composable
private fun DrillStringSectionRow(
    index: Int,
    section: DrillStringSection,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(section.name.ifBlank { "Section $index" },
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.35f))
        Text(String.format("%.3f", section.od),
            style = MaterialTheme.typography.bodySmall, color = TextSecondary,
            modifier = Modifier.weight(0.15f))
        Text(String.format("%.3f", section.id),
            style = MaterialTheme.typography.bodySmall, color = TextSecondary,
            modifier = Modifier.weight(0.15f))
        Text(String.format("%.1f", section.length),
            style = MaterialTheme.typography.bodySmall, color = TextSecondary,
            modifier = Modifier.weight(0.15f))
        Text(String.format("%.2f", section.weightPerFoot),
            style = MaterialTheme.typography.bodySmall, color = TextSecondary,
            modifier = Modifier.weight(0.15f))
        Row(modifier = Modifier.weight(0.05f)) {
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, null, tint = TextMuted, modifier = Modifier.size(14.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, null, tint = CoralDanger.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun DrillStringSectionDialog(
    initial: DrillStringSection?,
    onConfirm: (DrillStringSection) -> Unit,
    onDismiss: () -> Unit
) {
    var name          by remember { mutableStateOf(initial?.name ?: "") }
    var od            by remember { mutableStateOf(initial?.od?.toString() ?: "") }
    var id            by remember { mutableStateOf(initial?.id?.toString() ?: "") }
    var length        by remember { mutableStateOf(initial?.length?.toString() ?: "") }
    var weightPerFoot by remember { mutableStateOf(initial?.weightPerFoot?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title  = { Text(if (initial == null) "Add Section" else "Edit Section") },
        text   = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabeledTextField("Section Name", name, { name = it }, placeholder = "e.g. DP Section 1")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledTextField("OD", od, { od = it }, unit = "in", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    LabeledTextField("ID", id, { id = it }, unit = "in", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledTextField("Length", length, { length = it }, unit = "ft", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    LabeledTextField("Weight", weightPerFoot, { weightPerFoot = it }, unit = "lb/ft", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(DrillStringSection(
                        name          = name,
                        od            = od.toDoubleOrNull() ?: 0.0,
                        id            = id.toDoubleOrNull() ?: 0.0,
                        length        = length.toDoubleOrNull() ?: 0.0,
                        weightPerFoot = weightPerFoot.toDoubleOrNull() ?: 0.0
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = NavyDeep)
            ) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = CardSurface
    )
}