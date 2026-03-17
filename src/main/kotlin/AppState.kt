package com.oussama_chatri

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.oussama_chatri.core.navigation.Route

class AppState {

    var currentRoute: Route by mutableStateOf(Route.Dashboard)
        private set

    var isDarkTheme: Boolean by mutableStateOf(true)
        private set

    fun navigate(route: Route) {
        currentRoute = route
    }

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }
}