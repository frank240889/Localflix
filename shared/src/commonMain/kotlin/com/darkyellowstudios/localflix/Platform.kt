package com.darkyellowstudios.localflix

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

/**
 * Whether the device has any network (Wi-Fi, Ethernet, cellular). A home Jellyfin server
 * doesn't need internet access, so this deliberately ignores "validated internet".
 */
class ConnectivityMonitor(val isOnline: StateFlow<Boolean>)

/**
 * Holds the platform's multicast lock while discovery listens for replies. Android drops
 * broadcast/multicast packets to save power unless one is held; elsewhere it does nothing.
 */
class MulticastLock(val acquire: () -> AutoCloseable) {
    companion object {
        val None = MulticastLock { AutoCloseable {} }
    }
}

/** Koin bindings that need platform code: the [ConnectivityMonitor] and, on Android, the [MulticastLock]. */
expect val platformModule: Module
