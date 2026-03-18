package com.oussama_chatri.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oussama_chatri.core.navigation.Route
import com.oussama_chatri.core.theme.WellLogTheme

@Composable
fun AppScaffold(
    currentRoute:    Route,
    screenTitle:     String,
    onRouteSelected: (Route) -> Unit,
    modifier:        Modifier = Modifier,
    topBarActions:   @Composable (() -> Unit)? = null,
    errorMessage:    String? = null,
    onErrorDismiss:  () -> Unit = {},
    content:         @Composable () -> Unit
) {
    val c = WellLogTheme.colors

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(c.background)
    ) {
        SideNavBar(
            currentRoute    = currentRoute,
            onRouteSelected = onRouteSelected
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(c.divider)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(c.surface)
        ) {
            TopBar(
                screenTitle = screenTitle,
                actions     = topBarActions
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(c.divider)
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
                        .align(Alignment.BottomCenter)
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