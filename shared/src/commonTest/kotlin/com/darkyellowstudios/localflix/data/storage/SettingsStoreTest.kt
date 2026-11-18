package com.darkyellowstudios.localflix.data.storage

import com.darkyellowstudios.localflix.model.JellyfinSession
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettingsStoreTest {
    private val settings = MapSettings()
    private val store = SettingsStore(settings)

    @Test
    fun deviceIdIsGeneratedOnceAndReused() {
        val first = store.deviceId
        assertEquals(32, first.length)
        assertEquals(first, store.deviceId)
        assertEquals(first, SettingsStore(settings).deviceId)
    }

    @Test
    fun clearSessionKeepsTheServer() {
        store.saveSession(JellyfinSession("http://jf.local:8096", "u1", "Franco", "token"))
        assertEquals("u1", store.session()?.userId)

        store.clearSession()

        assertNull(store.session())
        assertEquals("http://jf.local:8096", store.serverUrl)
    }
}
