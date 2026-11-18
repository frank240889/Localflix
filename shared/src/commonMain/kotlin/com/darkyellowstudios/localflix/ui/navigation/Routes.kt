package com.darkyellowstudios.localflix.ui.navigation

import kotlinx.serialization.Serializable

/** Type-safe navigation destinations. A Player route arrives with playback (Phase 5). */
sealed interface Route {
    @Serializable data object Splash : Route
    @Serializable data object Login : Route
    @Serializable data object Profiles : Route
    @Serializable data object Home : Route
    @Serializable data class Details(val movieId: String) : Route
}
