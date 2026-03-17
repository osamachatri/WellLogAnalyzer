package com.oussama_chatri

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.oussama_chatri.core.di.AppModule
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(AppModule)
    }

    application {
        val windowState = rememberWindowState(
            width  = 1440.dp,
            height = 900.dp
        )

        Window(
            onCloseRequest = ::exitApplication,
            title          = "WellLogAnalyzer",
            state          = windowState,
            resizable      = true,
            icon = painterResource("icons/app_icon.png")
        ) {
            App()
        }
    }
}