package com.darkyellowstudios.localflix

import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.WifiManager
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual val platformModule: Module = module {
    single(createdAtStart = true) { ConnectivityMonitor(androidContext().getSystemService(ConnectivityManager::class.java).onlineState()) }
    single {
        val wifi = androidContext().getSystemService(WifiManager::class.java)
        MulticastLock {
            wifi.createMulticastLock("localflix-discovery").run {
                setReferenceCounted(false)
                acquire()
                AutoCloseable(::release)
            }
        }
    }
}

/** Follows the default network; lives as long as the app (Koin singleton). */
private fun ConnectivityManager.onlineState(): StateFlow<Boolean> {
    val state = MutableStateFlow(activeNetwork != null)
    registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) { state.value = true }
        override fun onLost(network: Network) { state.value = false }
    })
    return state
}
