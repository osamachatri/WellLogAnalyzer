package com.oussama_chatri.feature.wellinput.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.*
import com.oussama_chatri.core.ui.components.LabeledTextField
import com.oussama_chatri.core.ui.components.SectionCard
import com.oussama_chatri.feature.wellinput.domain.model.FluidProperties
import com.oussama_chatri.feature.wellinput.domain.model.MudType
import com.oussama_chatri.feature.wellinput.domain.model.RheologyModel

/**
 * Fluid Properties tab content.
 * Three sections: mud density & flow, rheology model, additional properties.
 * Right panel: live rheology curve preview (canvas-drawn).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FluidPropertiesPanel(
    fluid: FluidProperties,
    onMudWeightChange: (String) -> Unit,
    onFlowRateChange: (String) -> Unit,
    onSurfaceTempChange: (String) -> Unit,
    onBhtChange: (String) -> Unit,
    onRheologyModelChange: (RheologyModel) -> Unit,
    onPlasticViscosityChange: (String) -> Unit,
    onYieldPointChange: (String) -> Unit,
    onFlowBehaviorIndexChange: (String) -> Unit,
    onConsistencyIndexChange: (String) -> Unit,
    onMudTypeChange: (MudType) -> Unit,
    onSolidsContentChange: (String) -> Unit,
    onPHChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left: form sections
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Mud Density & Flow
            SectionCard(title = "Mud Density & Flow") {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    LabeledTextField(
                        label         = "Mud Weight",
                        value         = if (fluid.mudWeight == 0.0) "" else fluid.mudWeight.toString(),
                        onValueChange = onMudWeightChange,
                        unit          = "ppg",
                        placeholder   = "10.5",
                        keyboardType  = KeyboardType.Decimal,
                        modifier      = Modifier.weight(1f)
                    )
                    LabeledTextField(
                        label         = "Flow Rate",
                        value         = if (fluid.flowRate == 0.0) "" else fluid.flowRate.toString(),
                        onValueChange = onFlowRateChange,
                        unit          = "gpm",
                        placeholder   = "400",
                        keyboardType  = KeyboardType.Decimal,
                        modifier      = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    LabeledTextField(
                        label         = "Surface Temperature",
                        value         = if (fluid.surfaceTemperature == 0.0) "" else fluid.surfaceTemperature.toString(),
                        onValueChange = onSurfaceTempChange,
                        unit          = "°F",
                        placeholder   = "70",
                        keyboardType  = KeyboardType.Decimal,
                        modifier      = Modifier.weight(1f)
                    )
                    LabeledTextField(
                        label         = "BHT",
                        value         = if (fluid.bottomholeTemperature == 0.0) "" else fluid.bottomholeTemperature.toString(),
                        onValueChange = onBhtChange,
                        unit          = "°F",
                        placeholder   = "200",
                        keyboardType  = KeyboardType.Decimal,
                        modifier      = Modifier.weight(1f)
                    )
                }
            }

            // Section 2: Rheology Model
            SectionCard(title = "Rheology Model") {
                // Segmented button toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(CardElevated)
                        .border(1.dp, DividerColor, MaterialTheme.shapes.small)
                ) {
                    RheologyModel.entries.forEach { model ->
                        val isSelected = fluid.rheologyModel == model
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) AmberGold.copy(alpha = 0.15f)
                                    else androidx.compose.ui.graphics.Color.Transparent
                                )
                                .then(
                                    if (isSelected)
                                        Modifier.border(
                                            1.dp, AmberGold.copy(alpha = 0.5f),
                                            MaterialTheme.shapes.small
                                        )
                                    else Modifier
                                )
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(
                                onClick = { onRheologyModelChange(model) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text  = if (model == RheologyModel.BINGHAM_PLASTIC) "Bingham Plastic" else "Power Law",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) AmberGold else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Conditional rheology inputs
                when (fluid.rheologyModel) {
                    RheologyModel.BINGHAM_PLASTIC -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LabeledTextField(
                                label         = "Plastic Viscosity (PV)",
                                value         = if (fluid.plasticViscosity == 0.0) "" else fluid.plasticViscosity.toString(),
                                onValueChange = onPlasticViscosityChange,
                                unit          = "cP",
                                placeholder   = "18",
                                keyboardType  = KeyboardType.Decimal,
                                modifier      = Modifier.weight(1f)
                            )
                            LabeledTextField(
                                label         = "Yield Point (YP)",
                                value         = if (fluid.yieldPoint == 0.0) "" else fluid.yieldPoint.toString(),
                                onValueChange = onYieldPointChange,
                                unit          = "lb/100ft²",
                                placeholder   = "12",
                                keyboardType  = KeyboardType.Decimal,
                                modifier      = Modifier.weight(1f)
                            )
                        }
                    }
                    RheologyModel.POWER_LAW -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LabeledTextField(
                                label         = "Flow Behavior Index (n)",
                                value         = if (fluid.flowBehaviorIndex == 0.0) "" else fluid.flowBehaviorIndex.toString(),
                                onValueChange = onFlowBehaviorIndexChange,
                                placeholder   = "0.75",
                                keyboardType  = KeyboardType.Decimal,
                                modifier      = Modifier.weight(1f)
                            )
                            LabeledTextField(
                                label         = "Consistency Index (K)",
                                value         = if (fluid.consistencyIndex == 0.0) "" else fluid.consistencyIndex.toString(),
                                onValueChange = onConsistencyIndexChange,
                                unit          = "eq.cP",
                                placeholder   = "200",
                                keyboardType  = KeyboardType.Decimal,
                                modifier      = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Section 3: Additional Properties
            SectionCard(title = "Additional Properties") {
                // Mud type dropdown
                var mudTypeExpanded by remember { mutableStateOf(false) }
                Column {
                    Text(
                        text  = "Mud Type",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded         = mudTypeExpanded,
                        onExpandedChange = { mudTypeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value         = fluid.mudType.displayName,
                            onValueChange = {},
                            readOnly      = true,
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mudTypeExpanded) },
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = AmberGold,
                                unfocusedBorderColor = DividerColor
                            ),
                            modifier      = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded         = mudTypeExpanded,
                            onDismissRequest = { mudTypeExpanded = false },
                            modifier         = Modifier.background(CardSurface)
                        ) {
                            MudType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text    = { Text(type.displayName, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = { onMudTypeChange(type); mudTypeExpanded = false }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    LabeledTextField(
                        label         = "Solids Content",
                        value         = if (fluid.solidsContent == 0.0) "" else fluid.solidsContent.toString(),
                        onValueChange = onSolidsContentChange,
                        unit          = "%",
                        placeholder   = "5.0",
                        keyboardType  = KeyboardType.Decimal,
                        modifier      = Modifier.weight(1f)
                    )
                    LabeledTextField(
                        label         = "pH",
                        value         = if (fluid.pH == 0.0) "" else fluid.pH.toString(),
                        onValueChange = onPHChange,
                        placeholder   = "9.0",
                        keyboardType  = KeyboardType.Decimal,
                        modifier      = Modifier.weight(1f)
                    )
                }
            }
        }

        // Right panel: Rheology Curve preview
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
        ) {
            SectionCard(title = "Rheology Curve") {
                RheologyCurvePreview(
                    fluid    = fluid,
                    modifier = Modifier.fillMaxWidth().height(220.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "Shear stress vs. shear rate based on current rheology parameters.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

/**
 * Canvas-drawn rheology curve (shear stress vs shear rate).
 * Uses a simple line-drawing approach — no external chart library needed here.
 */
@Composable
private fun RheologyCurvePreview(
    fluid: FluidProperties,
    modifier: Modifier = Modifier
) {
    val hasData = when (fluid.rheologyModel) {
        RheologyModel.BINGHAM_PLASTIC -> fluid.plasticViscosity > 0 || fluid.yieldPoint > 0
        RheologyModel.POWER_LAW       -> fluid.consistencyIndex > 0
    }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(NavyDeep),
        contentAlignment = Alignment.Center
    ) {
        if (!hasData) {
            Text(
                text  = "Enter rheology parameters\nto preview the curve",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val w = size.width
                val h = size.height
                val steps = 50
                val maxRate = 1000f  // 1/s

                // Compute shear-stress points
                val points = (0..steps).map { i ->
                    val rate = i * maxRate / steps
                    val stress = when (fluid.rheologyModel) {
                        RheologyModel.BINGHAM_PLASTIC ->
                            (fluid.yieldPoint + fluid.plasticViscosity * rate / 100.0).toFloat()
                        RheologyModel.POWER_LAW ->
                            (fluid.consistencyIndex * Math.pow(rate.toDouble() / 100.0, fluid.flowBehaviorIndex)).toFloat()
                    }
                    rate to stress
                }

                val maxStress = points.maxOf { it.second }.coerceAtLeast(1f)

                // Grid lines
                val gridPaint = androidx.compose.ui.graphics.Paint().apply {
                    color = DividerColor.copy(alpha = 0.4f)
                }
                for (i in 1..4) {
                    val y = h * (1f - i / 4f)
                    drawLine(DividerColor.copy(alpha = 0.3f),
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end   = androidx.compose.ui.geometry.Offset(w, y),
                        strokeWidth = 1f)
                }

                // Curve
                for (i in 0 until points.lastIndex) {
                    val (r1, s1) = points[i]
                    val (r2, s2) = points[i + 1]
                    drawLine(
                        color       = TealSafe,
                        start       = androidx.compose.ui.geometry.Offset(r1 / maxRate * w, h - s1 / maxStress * h),
                        end         = androidx.compose.ui.geometry.Offset(r2 / maxRate * w, h - s2 / maxStress * h),
                        strokeWidth = 2f
                    )
                }
            }
        }
    }
}