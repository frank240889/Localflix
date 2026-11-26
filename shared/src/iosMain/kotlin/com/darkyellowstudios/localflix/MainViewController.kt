package com.darkyellowstudios.localflix

import androidx.compose.ui.window.ComposeUIViewController
import com.darkyellowstudios.localflix.di.initKoin
import platform.UIKit.UIViewController

// Koin starts before the first composition, which already reads from it.
fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}
