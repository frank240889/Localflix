package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.data.FakeJellyfinServer
import com.darkyellowstudios.localflix.model.JellyfinSession
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class RepositoriesTest {
    private val server = FakeJellyfinServer()
    private val auth = AuthRepository(server.api, server.store)
    private val library = LibraryRepository(server.api, server.store)

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
        assertFailsWith<IllegalStateException> { library.getMovies() }
        assertFailsWith<IllegalStateException> { library.streamUrl("m1") }
        assertFailsWith<IllegalStateException> { library.hlsUrl("m1") }
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
}
