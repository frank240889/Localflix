package com.darkyellowstudios.localflix

import androidx.compose.ui.window.ComposeUIViewController
import com.darkyellowstudios.localflix.di.initKoin

fun MainViewController() = ComposeUIViewController {
    App()
}.also { initKoin() }
