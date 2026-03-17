package com.oussama_chatri.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.AmberGold
import com.oussama_chatri.core.theme.CoralDanger
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.TextMuted
import com.oussama_chatri.core.theme.TextSecondary

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    unit: String? = null,
    placeholder: String = "",
    errorMessage: String? = null,
    isReadOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    val isError = errorMessage != null
    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = AmberGold,
        unfocusedBorderColor = DividerColor,
        errorBorderColor     = CoralDanger,
        focusedLabelColor    = AmberGold,
        cursorColor          = AmberGold,
        disabledBorderColor  = DividerColor.copy(alpha = 0.4f),
        disabledTextColor    = TextMuted,
        disabledLabelColor   = TextMuted,
    )

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text  = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isError) CoralDanger else TextSecondary
            )
            if (isReadOnly) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text  = "(calculated)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value         = value,
            onValueChange = if (isReadOnly) { _ -> } else onValueChange,
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = singleLine,
            enabled       = enabled && !isReadOnly,
            readOnly      = isReadOnly,
            isError       = isError,
            colors        = colors,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            placeholder   = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text  = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            },
            trailingIcon  = {
                if (unit != null) {
                    Text(
                        text     = unit,
                        style    = MaterialTheme.typography.labelMedium,
                        color    = TextSecondary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = if (isReadOnly) TextSecondary
                else MaterialTheme.colorScheme.onSurface
            )
        )

        if (isError && errorMessage != null) {
            Spacer(Modifier.height(3.dp))
            Text(
                text  = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = CoralDanger,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}