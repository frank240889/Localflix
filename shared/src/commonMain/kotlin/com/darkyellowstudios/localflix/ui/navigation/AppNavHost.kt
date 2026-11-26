package com.darkyellowstudios.localflix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.darkyellowstudios.localflix.data.sample.SampleMovies
import com.darkyellowstudios.localflix.ui.screens.LibraryScreen
import com.darkyellowstudios.localflix.ui.screens.LoginScreen
import com.darkyellowstudios.localflix.ui.screens.PlayerScreen
import com.darkyellowstudios.localflix.ui.screens.ProfileScreen
import com.darkyellowstudios.localflix.ui.screens.SplashScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.Splash) {
        composable<Route.Splash> {
            // TODO(Phase 1): restore the saved server/session or start discovery
            SplashScreen(
                onReady = {
                    navController.navigate(Route.Login) {
                        popUpTo<Route.Splash> { inclusive = true }
                    }
                },
            )
        }
        composable<Route.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.Profiles) {
                        popUpTo<Route.Login> { inclusive = true }
                    }
                },
            )
        }
        composable<Route.Profiles> {
            ProfileScreen(
                onProfileSelected = { navController.navigate(Route.Home) },
            )
        }
        composable<Route.Home> {
            LibraryScreen(
                onMovieSelected = { movie -> navController.navigate(Route.Details(movie.id)) },
            )
        }
        composable<Route.Details> { entry ->
            val movie = SampleMovies.byId(entry.toRoute<Route.Details>().movieId)
            if (movie != null) {
                PlayerScreen(movie = movie, onBack = { navController.popBackStack() })
            }
        }
    }
}
