package com.darkyellowstudios.localflix.di

import com.darkyellowstudios.localflix.ConnectivityMonitor
import com.darkyellowstudios.localflix.MulticastLock
import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.remote.ServerDiscovery
import com.darkyellowstudios.localflix.platformModule
import com.darkyellowstudios.localflix.data.remote.createHttpClient
import com.darkyellowstudios.localflix.data.repo.AuthRepository
import com.darkyellowstudios.localflix.data.repo.LibraryRepository
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

val appModule = module {
    single { Settings() }
    single { SettingsStore(get()) }
    single { createHttpClient(get()) }
    single { JellyfinApi(get(), get()) }
    single { AuthRepository(get(), get(), get()) }
    single { LibraryRepository(get(), get()) }
    // Only Android binds a real MulticastLock (platformModule)
    single { ServerDiscovery(getOrNull() ?: MulticastLock.None) }
}

/**
 * Starts Koin once. Safe to call again (e.g. on Activity recreation or when iOS
 * rebuilds the view controller); later calls are ignored.
 */
fun initKoin(config: KoinAppDeclaration = {}) {
    if (KoinPlatform.getKoinOrNull() != null) return
    val koin = startKoin {
        config()
        modules(appModule, platformModule)
    }.koin
    println("Localflix deviceId=${koin.get<SettingsStore>().deviceId} online=${koin.get<ConnectivityMonitor>().isOnline.value}")
}
