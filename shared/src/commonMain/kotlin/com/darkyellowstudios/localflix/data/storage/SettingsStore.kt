package com.darkyellowstudios.localflix.data.storage

import com.darkyellowstudios.localflix.model.JellyfinSession
import com.russhwolf.settings.Settings
import kotlin.random.Random

/**
 * On-device key/value storage (SharedPreferences on Android, NSUserDefaults on iOS,
 * Preferences on desktop, localStorage on web).
 *
 * TODO(Phase 6): move the access token to Keychain / encrypted storage.
 */
class SettingsStore(private val settings: Settings) {

    /** Stable per install, so the server sees one device instead of a new one each launch. */
    val deviceId: String
        get() = settings.getStringOrNull(KEY_DEVICE_ID)
            ?: newDeviceId().also { settings.putString(KEY_DEVICE_ID, it) }

    fun session(): JellyfinSession? {
        return JellyfinSession(
            serverUrl = settings.getStringOrNull(KEY_SERVER_URL) ?: return null,
            userId = settings.getStringOrNull(KEY_USER_ID) ?: return null,
            userName = settings.getStringOrNull(KEY_USER_NAME) ?: return null,
            accessToken = settings.getStringOrNull(KEY_ACCESS_TOKEN) ?: return null,
        )
    }

    fun saveSession(session: JellyfinSession) {
        settings.putString(KEY_SERVER_URL, session.serverUrl)
        settings.putString(KEY_USER_ID, session.userId)
        settings.putString(KEY_USER_NAME, session.userName)
        settings.putString(KEY_ACCESS_TOKEN, session.accessToken)
    }

    /** Signs out but keeps the server, so the next launch can reconnect without discovery. */
    fun clearSession() {
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_USER_NAME)
        settings.remove(KEY_ACCESS_TOKEN)
    }

    val serverUrl: String?
        get() = settings.getStringOrNull(KEY_SERVER_URL)

    private fun newDeviceId(): String =
        Random.nextBytes(16).joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }

    private companion object {
        const val KEY_DEVICE_ID = "device_id"
        const val KEY_SERVER_URL = "server_url"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_ACCESS_TOKEN = "access_token"
    }
}
