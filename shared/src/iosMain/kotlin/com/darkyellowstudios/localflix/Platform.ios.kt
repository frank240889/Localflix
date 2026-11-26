package com.darkyellowstudios.localflix

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.UIKit.UIDevice
import platform.darwin.dispatch_get_main_queue

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual val platformModule: Module = module {
    single(createdAtStart = true) { ConnectivityMonitor(pathMonitorState()) }
}

/** NWPathMonitor; kept in a property so it isn't released while the app runs. */
private val pathMonitor = nw_path_monitor_create()

private fun pathMonitorState(): StateFlow<Boolean> {
    val state = MutableStateFlow(true)
    nw_path_monitor_set_update_handler(pathMonitor) { path ->
        state.value = nw_path_get_status(path) == nw_path_status_satisfied
    }
    nw_path_monitor_set_queue(pathMonitor, dispatch_get_main_queue())
    nw_path_monitor_start(pathMonitor)
    return state
}
