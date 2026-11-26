package com.darkyellowstudios.localflix.data

import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.remote.createHttpClient
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.russhwolf.settings.MapSettings
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

/**
 * An in-memory Jellyfin server for tests. Records every request and answers the
 * endpoints the app uses with canned JSON.
 */
internal class FakeJellyfinServer {
    val requests = mutableListOf<HttpRequestData>()
    val store = SettingsStore(MapSettings())

    private val engine = MockEngine { request ->
        requests += request
        when (request.url.encodedPath) {
            "/Users/AuthenticateByName" -> respondJson(AUTH_RESPONSE)
            "/Items" -> respondJson(ITEMS_RESPONSE)
            else -> respond("", HttpStatusCode.NotFound)
        }
    }

    val api = JellyfinApi(createHttpClient(store, engine), store)

    private fun io.ktor.client.engine.mock.MockRequestHandleScope.respondJson(body: String) =
        respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))

    companion object {
        const val AUTH_RESPONSE =
            """{"User":{"Id":"u1","Name":"Franco"},"AccessToken":"tok123","ServerId":"s1"}"""
        const val ITEMS_RESPONSE =
            """{"Items":[{"Id":"m1","Name":"Arrival","ProductionYear":2016,"Genres":["Drama"]}],"TotalRecordCount":1}"""
    }
}
