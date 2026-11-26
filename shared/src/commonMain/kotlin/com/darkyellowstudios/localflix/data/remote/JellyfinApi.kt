package com.darkyellowstudios.localflix.data.remote

import com.darkyellowstudios.localflix.data.remote.dto.AuthenticateByNameRequest
import com.darkyellowstudios.localflix.data.remote.dto.AuthenticationResult
import com.darkyellowstudios.localflix.data.remote.dto.BaseItemDto
import com.darkyellowstudios.localflix.data.remote.dto.ItemsResult
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.darkyellowstudios.localflix.model.JellyfinSession
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

/** Thin wrapper over the Jellyfin REST API. Works on every KMP target. */
class JellyfinApi internal constructor(
    private val client: HttpClient,
    private val store: SettingsStore,
) {
    internal suspend fun authenticateByName(
        serverUrl: String,
        username: String,
        password: String,
    ): AuthenticationResult = client.post("$serverUrl/Users/AuthenticateByName") {
        contentType(ContentType.Application.Json)
        setBody(AuthenticateByNameRequest(username = username, pw = password))
    }.body()

    internal suspend fun getMovies(session: JellyfinSession): List<BaseItemDto> =
        client.get("${session.serverUrl}/Items") {
            parameter("userId", session.userId)
            parameter("IncludeItemTypes", "Movie")
            parameter("Recursive", true)
            parameter("SortBy", "SortName")
            parameter("Fields", "Overview,Genres,People")
        }.body<ItemsResult>().items

    /** Direct-play URL (original file, no transcoding). */
    fun streamUrl(session: JellyfinSession, itemId: String): String =
        "${session.serverUrl}/Videos/$itemId/stream?static=true&api_key=${session.accessToken}"

    /** HLS URL; the server transcodes when the device can't play the original format. */
    fun hlsUrl(session: JellyfinSession, itemId: String): String =
        "${session.serverUrl}/Videos/$itemId/master.m3u8" +
            "?MediaSourceId=$itemId&DeviceId=${store.deviceId}&api_key=${session.accessToken}"
}

/**
 * HTTP client that sends Jellyfin's `MediaBrowser` auth header on every request.
 * [engine] is null in the app (platform default engine); tests pass a `MockEngine`.
 */
internal fun createHttpClient(store: SettingsStore, engine: HttpClientEngine? = null): HttpClient {
    val config: HttpClientConfig<*>.() -> Unit = {
        expectSuccess = true // non-2xx responses throw, so repositories can map them to AppError
        install(HttpTimeout) {
            connectTimeoutMillis = 5_000
            requestTimeoutMillis = 15_000
        }
        install(HttpRequestRetry) {
            // Only GETs are safe to repeat; a failed login or progress report is not retried.
            maxRetries = 2
            retryIf { request, response -> request.method == HttpMethod.Get && response.status.value >= 500 }
            // A timed-out request is not retried, so the user waits at most one timeout.
            retryOnExceptionIf { request, cause ->
                request.method == HttpMethod.Get &&
                    cause !is CancellationException && cause !is HttpRequestTimeoutException
            }
            exponentialDelay(baseDelayMs = 300)
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        defaultRequest {
            header(HttpHeaders.Authorization, authHeader(store.deviceId, store.session()?.accessToken))
        }
    }
    return if (engine == null) HttpClient(config) else HttpClient(engine, config)
}

internal fun authHeader(deviceId: String, token: String?): String {
    val tokenPart = token?.let { """, Token="$it"""" } ?: ""
    return """MediaBrowser Client="$CLIENT_NAME", Device="$DEVICE_NAME", """ +
        """DeviceId="$deviceId", Version="$CLIENT_VERSION"$tokenPart"""
}

private const val CLIENT_NAME = "Localflix"
private const val CLIENT_VERSION = "1.0.0"
private const val DEVICE_NAME = "Localflix KMP"
