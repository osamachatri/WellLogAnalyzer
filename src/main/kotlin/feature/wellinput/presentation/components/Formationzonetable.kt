package com.oussama_chatri.feature.wellinput.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.LabeledTextField
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.feature.wellinput.domain.model.FormationZone
import com.oussama_chatri.feature.wellinput.domain.model.Lithology

/**
 * Formation Zones tab — full-width editable table + right-side geology column preview.
 */
@Composable
fun FormationZoneTable(
    zones: List<FormationZone>,
    totalDepth: Double,
    onAddZone: () -> Unit,
    onUpdateZone: (Int, FormationZone) -> Unit,
    onRemoveZone: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingIndex  by remember { mutableStateOf<Int?>(null) }
    var editingZone   by remember { mutableStateOf<FormationZone?>(null) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left: table
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionCard(title = "Formation Zones") {
                // Header
                FormationZoneTableHeader()
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 6.dp))

                if (zones.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "No formation zones defined. Add one below.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                } else {
                    zones.forEachIndexed { index, zone ->
                        val isEvenRow = index % 2 == 0
                        FormationZoneRow(
                            number   = index + 1,
                            zone     = zone,
                            isEven   = isEvenRow,
                            onEdit   = {
                                editingIndex = index
                                editingZone  = zone
                            },
                            onDelete = { onRemoveZone(index) }
                        )
                        if (index < zones.lastIndex) {
                            HorizontalDivider(color = DividerColor.copy(alpha = 0.3f))
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick  = onAddZone,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add Formation Zone")
                }
            }
        }

        // Right: geology column preview
        Column(
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight()
        ) {
            SectionCard(title = "Formation Column") {
                // Total depth badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(AmberDim)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text  = "Total Depth: ${if (totalDepth > 0) "${totalDepth.toLong()} ft" else "—"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = AmberGold
                    )
                }
                Spacer(Modifier.height(12.dp))

                if (zones.isEmpty() || totalDepth <= 0.0) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "Add zones to see\nthe geology column",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    GeologyColumnPreview(zones = zones, totalDepth = totalDepth)
                }
            }
        }
    }

    // Edit dialog
    if (editingZone != null && editingIndex != null) {
        FormationZoneEditDialog(
            zone      = editingZone!!,
            onConfirm = { updated ->
                onUpdateZone(editingIndex!!, updated)
                editingZone  = null
                editingIndex = null
            },
            onDismiss = {
                editingZone  = null
                editingIndex = null
            }
        )
    }
}

@Composable
private fun FormationZoneTableHeader() {
    val cols = listOf(
        "#"            to 0.04f,
        "Zone Name"    to 0.18f,
        "Top (ft)"     to 0.10f,
        "Bottom (ft)"  to 0.10f,
        "PP (ppg)"     to 0.10f,
        "FG (ppg)"     to 0.10f,
        "Lithology"    to 0.20f,
        ""             to 0.08f
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        cols.forEach { (label, weight) ->
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
private fun FormationZoneRow(
    number: Int,
    zone: FormationZone,
    isEven: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isEven) CardElevated.copy(alpha = 0.3f) else Color.Transparent)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("$number",
            style = MaterialTheme.typography.bodySmall, color = TextMuted,
            modifier = Modifier.weight(0.04f))
        Text(zone.zoneName.ifBlank { "—" },
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.18f))
        Text("${zone.topDepth.toLong()}",
            style = MaterialTheme.typography.bodySmall, color = TextSecondary,
            modifier = Modifier.weight(0.10f))
        Text("${zone.bottomDepth.toLong()}",
            style = MaterialTheme.typography.bodySmall, color = TextSecondary,
            modifier = Modifier.weight(0.10f))
        Text(String.format("%.1f", zone.porePressureGradient),
            style = MaterialTheme.typography.bodySmall, color = ChartBlue,
            modifier = Modifier.weight(0.10f))
        Text(String.format("%.1f", zone.fractureGradient),
            style = MaterialTheme.typography.bodySmall, color = CoralDanger.copy(alpha = 0.8f),
            modifier = Modifier.weight(0.10f))

        // Lithology chip
        Box(modifier = Modifier.weight(0.20f)) {
            LithologyChip(zone.lithology)
        }

        // Actions
        Row(modifier = Modifier.weight(0.08f)) {
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
fun LithologyChip(lithology: Lithology, modifier: Modifier = Modifier) {
    val (fg, bg) = lithologyColors(lithology)
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text  = lithology.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = fg
        )
    }
}

fun lithologyColors(lithology: Lithology): Pair<Color, Color> = when (lithology) {
    Lithology.SHALE      -> Color(0xFF94A3B8) to Color(0xFF607080).copy(alpha = 0.25f)
    Lithology.SANDSTONE  -> Color(0xFFF4A917) to Color(0xFFF4A917).copy(alpha = 0.20f)
    Lithology.LIMESTONE  -> Color(0xFFD9CDB4) to Color(0xFFD9CDB4).copy(alpha = 0.20f)
    Lithology.SALT       -> Color(0xFFB0C8D8) to Color(0xFFB0C8D8).copy(alpha = 0.20f)
    Lithology.DOLOMITE   -> Color(0xFFB8A88A) to Color(0xFFB8A88A).copy(alpha = 0.20f)
    Lithology.ANHYDRITE  -> Color(0xFFCB9CF2) to Color(0xFFCB9CF2).copy(alpha = 0.20f)
}

/** Vertical stacked bar visualising geology column proportional to depth range. */
@Composable
private fun GeologyColumnPreview(
    zones: List<FormationZone>,
    totalDepth: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        zones.forEach { zone ->
            val fraction  = ((zone.bottomDepth - zone.topDepth) / totalDepth).coerceIn(0.0, 1.0)
            val minHeight = 28.dp
            val height    = (fraction * 320).dp.coerceAtLeast(minHeight)
            val (_, bg)   = lithologyColors(zone.lithology)
            val (fg, _)   = lithologyColors(zone.lithology)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .background(bg.copy(alpha = 0.5f))
                    .border(0.5.dp, DividerColor.copy(alpha = 0.4f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colour swatch
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .fillMaxHeight()
                        .background(bg.copy(alpha = 0.9f))
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = zone.zoneName.ifBlank { zone.lithology.displayName },
                        style = MaterialTheme.typography.labelSmall,
                        color = fg,
                        maxLines = 1
                    )
                    Text(
                        text  = "${zone.topDepth.toLong()}–${zone.bottomDepth.toLong()} ft",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormationZoneEditDialog(
    zone: FormationZone,
    onConfirm: (FormationZone) -> Unit,
    onDismiss: () -> Unit
) {
    var zoneName    by remember { mutableStateOf(zone.zoneName) }
    var topDepth    by remember { mutableStateOf(if (zone.topDepth == 0.0) "" else zone.topDepth.toString()) }
    var bottomDepth by remember { mutableStateOf(if (zone.bottomDepth == 0.0) "" else zone.bottomDepth.toString()) }
    var pp          by remember { mutableStateOf(if (zone.porePressureGradient == 0.0) "" else zone.porePressureGradient.toString()) }
    var fg          by remember { mutableStateOf(if (zone.fractureGradient == 0.0) "" else zone.fractureGradient.toString()) }
    var lithology   by remember { mutableStateOf(zone.lithology) }
    var lithoExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CardSurface,
        title = { Text("Edit Formation Zone") },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabeledTextField("Zone Name", zoneName, { zoneName = it }, placeholder = "e.g. Sand A")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledTextField("Top Depth", topDepth, { topDepth = it },
                        unit = "ft", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    LabeledTextField("Bottom Depth", bottomDepth, { bottomDepth = it },
                        unit = "ft", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledTextField("Pore Pressure", pp, { pp = it },
                        unit = "ppg", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    LabeledTextField("Frac Gradient", fg, { fg = it },
                        unit = "ppg", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                }

                // Lithology selector
                Column {
                    Text("Lithology", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = lithoExpanded,
                        onExpandedChange = { lithoExpanded = it }
                    ) {
                        OutlinedTextField(
                            value         = lithology.displayName,
                            onValueChange = {},
                            readOnly      = true,
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lithoExpanded) },
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = AmberGold,
                                unfocusedBorderColor = DividerColor
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded         = lithoExpanded,
                            onDismissRequest = { lithoExpanded = false },
                            modifier         = Modifier.background(CardSurface)
                        ) {
                            Lithology.entries.forEach { l ->
                                DropdownMenuItem(
                                    text    = { Text(l.displayName, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = { lithology = l; lithoExpanded = false }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(zone.copy(
                        zoneName             = zoneName,
                        topDepth             = topDepth.toDoubleOrNull()    ?: zone.topDepth,
                        bottomDepth          = bottomDepth.toDoubleOrNull() ?: zone.bottomDepth,
                        porePressureGradient = pp.toDoubleOrNull()          ?: zone.porePressureGradient,
                        fractureGradient     = fg.toDoubleOrNull()          ?: zone.fractureGradient,
                        lithology            = lithology
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = NavyDeep)
            ) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}