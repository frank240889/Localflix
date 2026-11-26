package com.darkyellowstudios.localflix

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.module.Module
import org.koin.dsl.module

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

// Paused target: assume online until a real monitor is needed here.
actual val platformModule: Module = module {
    single { ConnectivityMonitor(MutableStateFlow(true)) }
}
