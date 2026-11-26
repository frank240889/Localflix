package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.data.FakeJellyfinServer
import com.darkyellowstudios.localflix.model.AppError
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class AuthRepositoryTest {
    private val server = FakeJellyfinServer()
    private val auth = AuthRepository(server.api, server.store, server.connectivity)

    @Test
    fun loginNormalisesServerUrlAndSavesSession() = runTest {
        val session = auth.login("  http://jf.local:8096/ ", "franco", "secret")

        assertEquals(FakeJellyfinServer.SESSION, session)
        assertEquals(FakeJellyfinServer.SESSION, auth.currentSession)
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
    fun loginWith401MeansWrongPassword() = runTest {
        server.handler = { respond("", HttpStatusCode.Unauthorized) }

        assertFailsWith<AppError.WrongCredentials> { auth.login("http://jf.local:8096", "franco", "nope") }
        assertNull(auth.currentSession)
    }
}
