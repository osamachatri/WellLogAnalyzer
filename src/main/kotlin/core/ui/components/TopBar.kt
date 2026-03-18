package com.oussama_chatri.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.theme.WellLogTheme

@Composable
fun TopBar(
    screenTitle: String,
    modifier:    Modifier = Modifier,
    actions:     @Composable (() -> Unit)? = null
) {
    val c = WellLogTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(c.cardSurface)
            .padding(horizontal = 20.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text  = "WellLogAnalyzer",
            style = MaterialTheme.typography.titleMedium,
            color = c.accent
        )

        Text(
            text  = screenTitle,
            style = MaterialTheme.typography.titleMedium,
            color = c.textPrimary
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            actions?.invoke()
            if (actions != null) Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(c.accent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource("icons/app_icon.png"),
                    contentDescription = "Logo"
                )
            }
        }
    }
}