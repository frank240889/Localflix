package com.darkyellowstudios.localflix

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.darkyellowstudios.localflix.di.initKoin

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Localflix",
    ) {
        App()
    }
}