package com.darkyellowstudios.localflix.data

import com.darkyellowstudios.localflix.ConnectivityMonitor
import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.remote.createHttpClient
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.darkyellowstudios.localflix.model.JellyfinSession
import com.russhwolf.settings.MapSettings
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * An in-memory Jellyfin server for tests. Records every request and answers the
 * endpoints the app uses with canned JSON.
 */
internal class FakeJellyfinServer {
    val requests = mutableListOf<HttpRequestData>()
    val store = SettingsStore(MapSettings())
    val online = MutableStateFlow(true)
    val connectivity = ConnectivityMonitor(online)

    /** Set to answer (or throw) differently from the canned responses below. */
    var handler: (MockRequestHandleScope.(HttpRequestData) -> HttpResponseData)? = null

    private val engine = MockEngine { request ->
        requests += request
        handler?.let { return@MockEngine it(request) }
        when (request.url.encodedPath) {
            "/Users/AuthenticateByName" -> respondJson(AUTH_RESPONSE)
            "/Items" -> respondJson(ITEMS_RESPONSE)
            else -> respond("", HttpStatusCode.NotFound)
        }
    }

    val api = JellyfinApi(createHttpClient(store, engine), store)

    /** Saves [SESSION] directly, without a login request. */
    fun signIn() = store.saveSession(SESSION)

    companion object {
        val SESSION = JellyfinSession("http://jf.local:8096", "u1", "Franco", "tok123")
        val JSON_HEADERS = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        const val AUTH_RESPONSE =
            """{"User":{"Id":"u1","Name":"Franco"},"AccessToken":"tok123","ServerId":"s1"}"""
        const val ITEMS_RESPONSE =
            """{"Items":[{"Id":"m1","Name":"Arrival","ProductionYear":2016,"Genres":["Drama"]}],"TotalRecordCount":1}"""
    }
}

internal fun MockRequestHandleScope.respondJson(body: String) = respond(body, HttpStatusCode.OK, FakeJellyfinServer.JSON_HEADERS)
