package com.oussama_chatri

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import com.oussama_chatri.core.navigation.AppNavigation
import com.oussama_chatri.core.theme.AppTheme
import com.oussama_chatri.core.ui.components.AppScaffold
import com.oussama_chatri.core.ui.components.SplashScreen
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

    var showSplash by remember { mutableStateOf(true) }

    AppTheme(themeId = appState.currentThemeId) {

        AnimatedVisibility(
            visible = !showSplash,
            enter   = fadeIn(animationSpec  = tween(durationMillis = 500)),
            exit    = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            AppScaffold(
                currentRoute    = appState.currentRoute,
                screenTitle     = appState.currentRoute.label,
                onRouteSelected = appState::navigate,
            ) {
                AppNavigation(appState = appState)
            }
        }

        AnimatedVisibility(
            visible = showSplash,
            enter   = fadeIn(animationSpec  = tween(durationMillis = 300)),
            exit    = fadeOut(animationSpec = tween(durationMillis = 600))
        ) {
            SplashScreen(onFinished = { showSplash = false })
        }
    }
}