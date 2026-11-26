package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.ConnectivityMonitor
import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.darkyellowstudios.localflix.model.AppError
import com.darkyellowstudios.localflix.model.JellyfinSession
import com.darkyellowstudios.localflix.model.apiCall

class AuthRepository(
    private val api: JellyfinApi,
    private val store: SettingsStore,
    private val connectivity: ConnectivityMonitor,
) {
    val currentSession: JellyfinSession?
        get() = store.session()

    /** Signs in with username/password and saves the session. Throws [AppError] on failure. */
    suspend fun login(serverUrl: String, username: String, password: String): JellyfinSession {
        val baseUrl = serverUrl.trim().trimEnd('/')
        val result = try {
            connectivity.apiCall { api.authenticateByName(baseUrl, username, password) }
        } catch (e: AppError.Unauthorized) {
            throw AppError.WrongCredentials(e) // a 401 here means the password, not a stale token
        }
        return JellyfinSession(
            serverUrl = baseUrl,
            userId = result.user.id,
            userName = result.user.name,
            accessToken = result.accessToken,
        ).also(store::saveSession)
    }

    fun logout() = store.clearSession()
}
