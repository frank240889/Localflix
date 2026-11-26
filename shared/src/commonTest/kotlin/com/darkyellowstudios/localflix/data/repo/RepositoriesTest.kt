package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.data.FakeJellyfinServer
import com.darkyellowstudios.localflix.model.AppError
import com.darkyellowstudios.localflix.model.JellyfinSession
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.io.IOException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class RepositoriesTest {
    private val server = FakeJellyfinServer()
    private val auth = AuthRepository(server.api, server.store, server.connectivity)
    private val library = LibraryRepository(server.api, server.store, server.connectivity)

    @Test
    fun loginNormalisesServerUrlAndSavesSession() = runTest {
        val session = auth.login("  http://jf.local:8096/ ", "franco", "secret")

        val expected = JellyfinSession("http://jf.local:8096", "u1", "Franco", "tok123")
        assertEquals(expected, session)
        assertEquals(expected, auth.currentSession)
        assertEquals("http://jf.local:8096/Users/AuthenticateByName", server.requests.single().url.toString())
    }

    @Test
    fun logoutClearsSessionButKeepsServer() = runTest {
        auth.login("http://jf.local:8096", "franco", "secret")

        auth.logout()

        assertNull(auth.currentSession)
        assertEquals("http://jf.local:8096", server.store.serverUrl)
    }

    @Test
    fun libraryNeedsASignedInUser() = runTest {
        assertFailsWith<AppError.Unauthorized> { library.getMovies() }
        assertFailsWith<AppError.Unauthorized> { library.streamUrl("m1") }
        assertFailsWith<AppError.Unauthorized> { library.hlsUrl("m1") }
        assertEquals(emptyList(), server.requests)
    }

    @Test
    fun libraryMapsMoviesForSignedInUser() = runTest {
        auth.login("http://jf.local:8096", "franco", "secret")

        val movie = library.getMovies().single()

        assertEquals("m1", movie.id)
        assertEquals("Arrival", movie.title)
        assertEquals("http://jf.local:8096/Items/m1/Images/Primary", movie.posterUrl)
    }

    @Test
    fun loginWith401MeansWrongPassword() = runTest {
        server.handler = { respond("", HttpStatusCode.Unauthorized) }

        assertFailsWith<AppError.WrongCredentials> { auth.login("http://jf.local:8096", "franco", "nope") }
        assertNull(auth.currentSession)
    }

    @Test
    fun revokedTokenIsUnauthorized() = runTest {
        auth.login("http://jf.local:8096", "franco", "secret")
        server.handler = { respond("", HttpStatusCode.Unauthorized) }

        assertFailsWith<AppError.Unauthorized> { library.getMovies() }
    }

    @Test
    fun serverErrorIsRetriedThenReported() = runTest {
        auth.login("http://jf.local:8096", "franco", "secret")
        server.requests.clear()
        server.handler = { respond("", HttpStatusCode.ServiceUnavailable) }

        val error = assertFailsWith<AppError.ServerError> { library.getMovies() }

        assertEquals(503, error.status)
        assertEquals(3, server.requests.size) // first try + 2 retries
    }

    @Test
    fun getRecoversWhenARetrySucceeds() = runTest {
        auth.login("http://jf.local:8096", "franco", "secret")
        var calls = 0
        server.handler = {
            if (++calls == 1) respond("", HttpStatusCode.BadGateway)
            else respond(FakeJellyfinServer.ITEMS_RESPONSE, HttpStatusCode.OK, jsonHeaders)
        }

        assertEquals("Arrival", library.getMovies().single().title)
        assertEquals(2, calls)
    }

    @Test
    fun loginIsNotRetried() = runTest {
        server.handler = { respond("", HttpStatusCode.InternalServerError) }

        assertFailsWith<AppError.ServerError> { auth.login("http://jf.local:8096", "franco", "secret") }
        assertEquals(1, server.requests.size)
    }

    @Test
    fun connectionFailureDependsOnConnectivity() = runTest {
        auth.login("http://jf.local:8096", "franco", "secret")
        server.handler = { throw IOException("connection refused") }

        assertFailsWith<AppError.ServerUnreachable> { library.getMovies() }

        server.online.value = false
        assertFailsWith<AppError.NoNetwork> { library.getMovies() }
    }

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
}
