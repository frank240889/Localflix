package com.darkyellowstudios.localflix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.darkyellowstudios.localflix.ui.components.RetryBanner
import com.darkyellowstudios.localflix.ui.navigation.AppNavHost
import com.darkyellowstudios.localflix.ui.theme.IndustrialCinemaTheme
import com.darkyellowstudios.localflix.ui.theme.IndustrialObsidian
import org.koin.compose.koinInject

/** Root composable. Platforms call initKoin (or initKoinAndroid) before showing it. */
@Composable
fun App() {
    IndustrialCinemaTheme {
        // Content stays clear of the status bar, notch, navigation bar and keyboard on every screen.
        Column(
            Modifier.fillMaxSize()
                .background(IndustrialObsidian) // same as the screens, so the bar areas blend in
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            ConnectionBanner()
            Box(Modifier.weight(1f)) { AppNavHost() }
        }
    }
}

/** Shown while the device has no network; what's on screen stays, and it clears by itself. */
@Composable
private fun ConnectionBanner() {
    val online by koinInject<ConnectivityMonitor>().isOnline.collectAsState()
    AnimatedVisibility(visible = !online) {
        RetryBanner("No network. Reconnecting…")
    }
}
