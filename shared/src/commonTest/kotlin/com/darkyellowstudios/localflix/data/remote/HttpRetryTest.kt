package com.darkyellowstudios.localflix.data.remote

import com.darkyellowstudios.localflix.data.FakeJellyfinServer
import com.darkyellowstudios.localflix.data.respondJson
import com.darkyellowstudios.localflix.model.AppError
import com.darkyellowstudios.localflix.model.apiCall
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The HTTP client retries GETs on server errors, and never retries a POST. */
class HttpRetryTest {
    private val server = FakeJellyfinServer()
    private val session = FakeJellyfinServer.SESSION

    @Test
    fun serverErrorIsRetriedThenReported() = runTest {
        server.handler = { respond("", HttpStatusCode.ServiceUnavailable) }

        val error = assertFailsWith<AppError.ServerError> {
            server.connectivity.apiCall { server.api.getMovies(session) }
        }

        assertEquals(503, error.status)
        assertEquals(3, server.requests.size) // first try + 2 retries
    }

    @Test
    fun getRecoversWhenARetrySucceeds() = runTest {
        var calls = 0
        server.handler = {
            if (++calls == 1) respond("", HttpStatusCode.BadGateway) else respondJson(FakeJellyfinServer.ITEMS_RESPONSE)
        }

        assertEquals("Arrival", server.api.getMovies(session).single().name)
        assertEquals(2, calls)
    }

    @Test
    fun postIsNotRetried() = runTest {
        server.handler = { respond("", HttpStatusCode.InternalServerError) }

        assertFailsWith<AppError.ServerError> {
            server.connectivity.apiCall { server.api.authenticateByName(session.serverUrl, "franco", "secret") }
        }
        assertEquals(1, server.requests.size)
    }
}
