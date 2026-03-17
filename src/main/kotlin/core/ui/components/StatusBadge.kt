package com.oussama_chatri.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.CoralDanger
import com.oussama_chatri.core.theme.AmberWarning
import com.oussama_chatri.core.theme.StatusNotRun
import com.oussama_chatri.core.theme.TealSafe

enum class WellStatus { Safe, Warning, Danger, NotRun }

@Composable
fun StatusBadge(
    status: WellStatus,
    label: String? = null,
    modifier: Modifier = Modifier
) {
    val (foreground, background) = when (status) {
        WellStatus.Safe    -> TealSafe    to TealSafe.copy(alpha = 0.15f)
        WellStatus.Warning -> AmberWarning to AmberWarning.copy(alpha = 0.15f)
        WellStatus.Danger  -> CoralDanger to CoralDanger.copy(alpha = 0.15f)
        WellStatus.NotRun  -> StatusNotRun to StatusNotRun.copy(alpha = 0.20f)
    }

    val displayLabel = label ?: when (status) {
        WellStatus.Safe    -> "Safe"
        WellStatus.Warning -> "Warning"
        WellStatus.Danger  -> "Danger"
        WellStatus.NotRun  -> "Not Run"
    }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(background)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = displayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = foreground
        )
    }
}