package com.darkyellowstudios.localflix.di

import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.repo.AuthRepository
import com.darkyellowstudios.localflix.data.repo.LibraryRepository
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertSame

class AppModuleTest {

    /** Fails if a dependency is missing from [appModule], instead of crashing at app launch. */
    @Test
    fun everyDependencyResolves() {
        val app = koinApplication {
            // Swap real on-device storage for an in-memory map; everything else is the real graph.
            modules(appModule, module { single<Settings> { MapSettings() } })
        }
        try {
            val koin = app.koin
            koin.get<SettingsStore>()
            koin.get<HttpClient>()
            koin.get<JellyfinApi>()
            koin.get<AuthRepository>()
            koin.get<LibraryRepository>()
            assertSame(koin.get<AuthRepository>(), koin.get<AuthRepository>())
        } finally {
            app.close()
        }
    }
}
