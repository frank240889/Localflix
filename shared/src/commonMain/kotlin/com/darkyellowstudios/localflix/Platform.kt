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

/** Koin bindings that need platform code: currently the [ConnectivityMonitor]. */
expect val platformModule: Module
