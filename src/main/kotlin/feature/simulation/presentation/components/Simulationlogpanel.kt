package com.oussama_chatri.feature.simulation.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oussama_chatri.core.theme.*
import kotlinx.coroutines.launch

@Composable
fun SimulationLogPanel(
    logLines: List<String>,
    modifier: Modifier = Modifier
) {
    val listState   = rememberLazyListState()
    val scope       = rememberCoroutineScope()

    // Auto-scroll to the latest log line whenever a new one arrives
    LaunchedEffect(logLines.size) {
        if (logLines.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(logLines.lastIndex)
            }
        }
    }

    Column(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(Color(0xFF080F18))
            .padding(12.dp)
    ) {
        Text(
            text  = "Simulation Log",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            state   = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(logLines) { line ->
                LogLine(line)
            }
        }
    }
}

@Composable
private fun LogLine(line: String) {
    val color = when {
        line.contains("FAILED") || line.contains("ERROR") -> CoralDanger
        line.contains("⚠")                               -> AmberWarning
        line.contains("complete") || line.contains("✓")  -> TealSafe
        line.startsWith("[") && line.contains("]")       -> Color(0xFF82C882)
        else                                              -> TextSecondary
    }

    Text(
        text       = line,
        fontFamily = FontFamily.Monospace,
        fontSize   = 10.sp,
        lineHeight = 14.sp,
        color      = color
    )
}