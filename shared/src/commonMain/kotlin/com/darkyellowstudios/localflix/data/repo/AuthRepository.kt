package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.ConnectivityMonitor
import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.darkyellowstudios.localflix.model.AppError
import com.darkyellowstudios.localflix.model.JellyfinSession
import com.darkyellowstudios.localflix.model.apiCall
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AuthRepository(
    private val api: JellyfinApi,
    private val store: SettingsStore,
    private val connectivity: ConnectivityMonitor,
) {
    val currentSession: JellyfinSession?
        get() = store.session()

    private val expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Emits when the server rejects the saved token; the app then returns to Who's watching. */
    val sessionExpired: SharedFlow<Unit> = expired

    internal fun requireSession(): JellyfinSession = store.session() ?: throw AppError.Unauthorized()

    /** Runs an authenticated call; a 401 clears the session and fires [sessionExpired]. */
    internal suspend fun <T> authorized(block: suspend (JellyfinSession) -> T): T {
        val session = requireSession()
        return try {
            connectivity.apiCall { block(session) }
        } catch (e: AppError.Unauthorized) {
            store.clearSession()
            expired.tryEmit(Unit)
            throw e
        }
    }

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
