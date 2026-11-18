package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.darkyellowstudios.localflix.model.JellyfinSession

class AuthRepository(
    private val api: JellyfinApi,
    private val store: SettingsStore,
) {
    val currentSession: JellyfinSession?
        get() = store.session()

    /** Signs in with username/password and saves the session. Throws on failure. */
    suspend fun login(serverUrl: String, username: String, password: String): JellyfinSession {
        val baseUrl = serverUrl.trim().trimEnd('/')
        val result = api.authenticateByName(baseUrl, username, password)
        return JellyfinSession(
            serverUrl = baseUrl,
            userId = result.user.id,
            userName = result.user.name,
            accessToken = result.accessToken,
        ).also(store::saveSession)
    }

    fun logout() = store.clearSession()
}
