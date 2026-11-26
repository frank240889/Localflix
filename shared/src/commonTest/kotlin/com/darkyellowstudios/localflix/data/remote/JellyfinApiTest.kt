package com.darkyellowstudios.localflix.data.remote

import com.darkyellowstudios.localflix.data.FakeJellyfinServer
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JellyfinApiTest {
    private val server = FakeJellyfinServer()
    private val session = FakeJellyfinServer.SESSION

    @Test
    fun authenticateByNamePostsCredentialsWithoutToken() = runTest {
        val result = server.api.authenticateByName("http://jf.local:8096", "franco", "secret")

        assertEquals("u1", result.user.id)
        assertEquals("tok123", result.accessToken)

        val request = server.requests.single()
        assertEquals(HttpMethod.Post, request.method)
        assertEquals("http://jf.local:8096/Users/AuthenticateByName", request.url.toString())
        assertEquals("""{"Username":"franco","Pw":"secret"}""", request.body.toByteArray().decodeToString())

        val auth = request.headers[HttpHeaders.Authorization].orEmpty()
        assertTrue(auth.startsWith("MediaBrowser "), auth)
        assertTrue("""DeviceId="${server.store.deviceId}"""" in auth, auth)
        assertFalse("Token=" in auth, auth)
    }

    @Test
    fun getMoviesQueriesUserMoviesAndSendsToken() = runTest {
        server.store.saveSession(session)

        val movies = server.api.getMovies(session)

        assertEquals(listOf("Arrival"), movies.map { it.name })
        val request = server.requests.single()
        assertEquals(HttpMethod.Get, request.method)
        assertEquals("/Items", request.url.encodedPath)
        val params = request.url.parameters
        assertEquals("u1", params["userId"])
        assertEquals("Movie", params["IncludeItemTypes"])
        assertEquals("true", params["Recursive"])
        assertEquals("SortName", params["SortBy"])
        assertEquals("Overview,Genres,People", params["Fields"])
        assertTrue("""Token="tok123"""" in request.headers[HttpHeaders.Authorization].orEmpty())
    }
}
