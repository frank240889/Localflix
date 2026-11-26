package com.darkyellowstudios.localflix

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.module.Module
import org.koin.dsl.module

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

// Paused target: assume online until a real monitor is needed here.
actual val platformModule: Module = module {
    single { ConnectivityMonitor(MutableStateFlow(true)) }
}
