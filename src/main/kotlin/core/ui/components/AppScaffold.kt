package com.oussama_chatri.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.navigation.Route
import com.oussama_chatri.core.theme.DividerColor
import com.oussama_chatri.core.theme.SlateDark

@Composable
fun AppScaffold(
    currentRoute: Route,
    screenTitle: String,
    isDark: Boolean,
    onThemeToggle: () -> Unit,
    onRouteSelected: (Route) -> Unit,
    modifier: Modifier = Modifier,
    topBarActions: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    onErrorDismiss: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SideNavBar(
            currentRoute    = currentRoute,
            onRouteSelected = onRouteSelected
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(DividerColor)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TopBar(
                screenTitle   = screenTitle,
                isDark        = isDark,
                onThemeToggle = onThemeToggle,
                actions       = topBarActions
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DividerColor)
                    .padding(0.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                content()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(androidx.compose.ui.Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    ErrorBanner(
                        message   = errorMessage,
                        onDismiss = onErrorDismiss
                    )
                }
            }
        }
    }
}