package com.oussama_chatri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.oussama_chatri.core.navigation.AppNavigation
import com.oussama_chatri.core.theme.AppTheme
import com.oussama_chatri.core.ui.components.AppScaffold
import com.oussama_chatri.feature.settings.presentation.viewmodel.SettingsViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    val appState   = remember { AppState() }
    val settingsVm = koinInject<SettingsViewModel>()
    val settings   by settingsVm.settings.collectAsState()

    LaunchedEffect(settings.themeId) {
        appState.setTheme(settings.themeId)
    }

    AppTheme(themeId = appState.currentThemeId) {
        AppScaffold(
            currentRoute    = appState.currentRoute,
            screenTitle     = appState.currentRoute.label,
            onRouteSelected = appState::navigate,
        ) {
            AppNavigation(appState = appState)
        }
    }
}