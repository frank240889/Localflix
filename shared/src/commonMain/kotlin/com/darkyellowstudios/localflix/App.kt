package com.darkyellowstudios.localflix

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.darkyellowstudios.localflix.model.Movie
import com.darkyellowstudios.localflix.ui.screens.LibraryScreen
import com.darkyellowstudios.localflix.ui.screens.LoginScreen
import com.darkyellowstudios.localflix.ui.screens.PlayerScreen
import com.darkyellowstudios.localflix.ui.screens.ProfileScreen
import com.darkyellowstudios.localflix.ui.theme.IndustrialCinemaTheme

sealed class Screen {
    data object Login : Screen()
    data object Profile : Screen()
    data object Library : Screen()
    data class Player(val movie: Movie) : Screen()
}

@Composable
@Preview
fun App() {
    IndustrialCinemaTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

        when (val screen = currentScreen) {
            is Screen.Login -> {
                LoginScreen(
                    onLoginSuccess = {
                        currentScreen = Screen.Profile
                    }
                )
            }
            is Screen.Profile -> {
                ProfileScreen(
                    onProfileSelected = {
                        currentScreen = Screen.Library
                    }
                )
            }
            is Screen.Library -> {
                LibraryScreen(
                    onMovieSelected = { movie ->
                        currentScreen = Screen.Player(movie)
                    }
                )
            }
            is Screen.Player -> {
                PlayerScreen(
                    movie = screen.movie,
                    onBack = {
                        currentScreen = Screen.Library
                    }
                )
            }
        }
    }
}
