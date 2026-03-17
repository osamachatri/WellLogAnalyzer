package com.oussama_chatri

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.oussama_chatri.core.navigation.Route
import com.oussama_chatri.feature.wellinput.domain.model.WellProfile

class AppState {

    var currentRoute: Route by mutableStateOf(Route.Dashboard)
        private set

    var isDarkTheme: Boolean by mutableStateOf(true)
        private set

    var activeProfile: WellProfile? by mutableStateOf(null)
        private set

    fun navigate(route: Route) {
        currentRoute = route
    }

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

    // Stores the profile and switches to the Simulation screen in one call
    fun navigateToSimulation(profile: WellProfile) {
        activeProfile = profile
        currentRoute  = Route.Simulation
    }
}