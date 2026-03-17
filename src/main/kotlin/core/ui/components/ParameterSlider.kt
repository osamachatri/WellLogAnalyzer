package com.oussama_chatri.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.TextSecondary

@Composable
fun ParameterSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    unit: String = "",
    steps: Int = 0,
    displayValue: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text     = label,
                style    = MaterialTheme.typography.labelMedium,
                color    = TextSecondary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text  = displayValue ?: "${String.format("%.1f", value)} $unit",
                style = MaterialTheme.typography.labelMedium,
                color = AmberGold
            )
        }

        Spacer(Modifier.height(4.dp))

        Slider(
            value         = value,
            onValueChange = onValueChange,
            valueRange    = valueRange,
            steps         = steps,
            modifier      = Modifier.fillMaxWidth(),
            colors        = SliderDefaults.colors(
                thumbColor           = AmberGold,
                activeTrackColor     = AmberGold,
                inactiveTrackColor   = DividerColor,
                activeTickColor      = AmberGold,
                inactiveTickColor    = DividerColor
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text  = "${valueRange.start.let { if (it == it.toLong().toFloat()) it.toLong().toString() else String.format("%.1f", it) }} $unit",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(Modifier.weight(1f))
            Text(
                text  = "${valueRange.endInclusive.let { if (it == it.toLong().toFloat()) it.toLong().toString() else String.format("%.1f", it) }} $unit",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}