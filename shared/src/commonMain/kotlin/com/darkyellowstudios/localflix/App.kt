package com.darkyellowstudios.localflix

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.darkyellowstudios.localflix.ui.navigation.AppNavHost
import com.darkyellowstudios.localflix.ui.theme.IndustrialCinemaTheme

/** Root composable. Platforms call initKoin (or initKoinAndroid) before showing it. */
@Composable
@Preview
fun App() {
    IndustrialCinemaTheme {
        AppNavHost()
    }
}
