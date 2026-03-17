package com.oussama_chatri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.oussama_chatri.core.navigation.AppNavigation
import com.oussama_chatri.core.theme.AppTheme
import com.oussama_chatri.core.ui.components.AppScaffold

@Composable
fun App() {
    val appState = remember { AppState() }

    AppTheme(isDark = appState.isDarkTheme) {
        AppScaffold(
            currentRoute    = appState.currentRoute,
            screenTitle     = appState.currentRoute.label,
            isDark          = appState.isDarkTheme,
            onThemeToggle   = appState::toggleTheme,
            onRouteSelected = appState::navigate,
        ) {
            AppNavigation(currentRoute = appState.currentRoute)
        }
    }
}