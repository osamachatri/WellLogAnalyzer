package com.oussama_chatri.feature.wellinput.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import com.oussama_chatri.feature.wellinput.domain.model.BitParameters
import com.oussama_chatri.feature.wellinput.domain.model.FluidProperties
import com.oussama_chatri.feature.wellinput.domain.usecase.ValidationResult
import kotlin.math.pow

@Composable
fun BitParametersPanel(
    bitParameters: BitParameters,
    fluidProperties: FluidProperties,
    validationResult: ValidationResult?,
    onBitSizeChange: (String) -> Unit,
    onNozzleCountChange: (String) -> Unit,
    onNozzleSizeChange: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Derived calculations
    val flowArea      = bitParameters.totalFlowArea
    val flowRate      = fluidProperties.flowRate
    val mudWeight     = fluidProperties.mudWeight

    // Nozzle velocity: V_n = Q / (3.117 × A_n)  [ft/s]
    val nozzleVelocity = if (flowArea > 0) flowRate / (3.117 * flowArea) else 0.0

    // Bit pressure drop: ΔP = ρ × V_n² / (12032)  [psi]  (ppg, ft/s)
    val bitPressureDrop = if (flowArea > 0 && mudWeight > 0)
        (mudWeight * nozzleVelocity.pow(2)) / 1120.0 else 0.0

    // Hydraulic horsepower: HHP = Q × ΔP / 1714
    val hydraulicHp = if (flowRate > 0) flowRate * bitPressureDrop / 1714.0 else 0.0

    // Impact force: F = 0.000516 × ρ × Q × V_n  [lbf]
    val impactForce = if (mudWeight > 0 && flowRate > 0 && nozzleVelocity > 0)
        0.000516 * mudWeight * flowRate * nozzleVelocity else 0.0

    // HSI: hydraulic horsepower per square inch of bit area
    val bitArea = bitParameters.bitArea
    val hsi = if (bitArea > 0) hydraulicHp / bitArea else 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionCard(title = "Bit Configuration") {
                    LabeledTextField(
                        label         = "Bit Size",
                        value         = if (bitParameters.bitSize == 0.0) "" else bitParameters.bitSize.toString(),
                        onValueChange = onBitSizeChange,
                        unit          = "in",
                        placeholder   = "8.500",
                        keyboardType  = KeyboardType.Decimal,
                        errorMessage  = validationResult?.errors
                            ?.filterIsInstance<com.oussama_chatri.feature.wellinput.domain.usecase.ValidationError.BitSizeNotDefined>()
                            ?.firstOrNull()?.message
                    )
                    Spacer(Modifier.height(12.dp))
                    LabeledTextField(
                        label         = "Number of Nozzles",
                        value         = if (bitParameters.nozzleCount == 0) "" else bitParameters.nozzleCount.toString(),
                        onValueChange = onNozzleCountChange,
                        placeholder   = "3",
                        keyboardType  = KeyboardType.Number,
                        errorMessage  = validationResult?.errors
                            ?.filterIsInstance<com.oussama_chatri.feature.wellinput.domain.usecase.ValidationError.NozzleCountInvalid>()
                            ?.firstOrNull()?.message
                    )
                }

                SectionCard(title = "Nozzle Sizes") {
                    val count = bitParameters.nozzleCount.coerceIn(1, 8)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        for (i in 0 until count) {
                            val currentVal = bitParameters.nozzleSizes.getOrNull(i)
                            LabeledTextField(
                                label         = "Nozzle ${i + 1}",
                                value         = if (currentVal == null || currentVal == 0.0) "" else currentVal.toString(),
                                onValueChange = { onNozzleSizeChange(i, it) },
                                unit          = "1/32 in",
                                placeholder   = "12",
                                keyboardType  = KeyboardType.Decimal
                            )
                        }
                        if (bitParameters.nozzleCount <= 0) {
                            Text(
                                text  = "Set nozzle count above to configure nozzle sizes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionCard(title = "Calculated Results") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LabeledTextField(
                            label      = "Total Flow Area",
                            value      = String.format("%.4f", flowArea),
                            onValueChange = {},
                            unit       = "in²",
                            isReadOnly = true
                        )
                        LabeledTextField(
                            label      = "Nozzle Velocity",
                            value      = String.format("%.1f", nozzleVelocity),
                            onValueChange = {},
                            unit       = "ft/s",
                            isReadOnly = true
                        )
                        LabeledTextField(
                            label      = "Bit Pressure Drop",
                            value      = String.format("%.1f", bitPressureDrop),
                            onValueChange = {},
                            unit       = "psi",
                            isReadOnly = true
                        )
                        LabeledTextField(
                            label      = "Hydraulic Horsepower",
                            value      = String.format("%.1f", hydraulicHp),
                            onValueChange = {},
                            unit       = "hp",
                            isReadOnly = true
                        )
                        LabeledTextField(
                            label      = "Impact Force",
                            value      = String.format("%.0f", impactForce),
                            onValueChange = {},
                            unit       = "lbf",
                            isReadOnly = true
                        )

                        // HSI with colour indicator
                        val hsiColor = when {
                            hsi <= 0.0  -> TextMuted
                            hsi < 1.0   -> AmberWarning
                            hsi <= 1.5  -> TealSafe
                            else        -> CoralDanger
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text  = "HSI",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text  = "(calculated)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.small)
                                    .background(CardElevated)
                                    .border(1.dp, DividerColor, MaterialTheme.shapes.small)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text     = String.format("%.3f", hsi),
                                    style    = MaterialTheme.typography.bodyMedium,
                                    color    = hsiColor,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text  = "hp/in²",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // ── API RP 13D tip card ──────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(TealSafe.copy(alpha = 0.08f))
                        .border(1.dp, TealSafe.copy(alpha = 0.25f), MaterialTheme.shapes.medium)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint     = TealSafe,
                        modifier = Modifier.size(18.dp).padding(top = 1.dp)
                    )
                    Text(
                        text  = "Nozzle sizing tip — API RP 13D recommends an HSI between " +
                                "1.0 and 1.5 hp/in² for most formations to optimise hole cleaning and ROP.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TealSafe.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}