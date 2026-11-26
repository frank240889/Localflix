package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.data.FakeJellyfinServer
import com.darkyellowstudios.localflix.model.AppError
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class LibraryRepositoryTest {
    private val server = FakeJellyfinServer()
    private val auth = AuthRepository(server.api, server.store, server.connectivity)
    private val library = LibraryRepository(server.api, auth)

    @Test
    fun needsASignedInUser() = runTest {
        assertFailsWith<AppError.Unauthorized> { library.getMovies() }
        assertFailsWith<AppError.Unauthorized> { library.streamUrl("m1") }
        assertFailsWith<AppError.Unauthorized> { library.hlsUrl("m1") }
        assertEquals(emptyList(), server.requests)
    }

    @Test
    fun mapsMoviesForSignedInUser() = runTest {
        server.signIn()

        val movie = library.getMovies().single()

        assertEquals("m1", movie.id)
        assertEquals("Arrival", movie.title)
        assertEquals("http://jf.local:8096/Items/m1/Images/Primary", movie.posterUrl)
    }

    @Test
    fun revokedTokenSignsOutAndFiresSessionExpired() = runTest {
        server.signIn()
        server.handler = { respond("", HttpStatusCode.Unauthorized) }
        val expired = async(start = CoroutineStart.UNDISPATCHED) { auth.sessionExpired.first() }

        assertFailsWith<AppError.Unauthorized> { library.getMovies() }

        expired.await() // fails by test timeout if sessionExpired never fires
        assertNull(auth.currentSession)
        assertEquals("http://jf.local:8096", server.store.serverUrl)
    }

    @Test
    fun connectionFailureDependsOnConnectivity() = runTest {
        server.signIn()
        server.handler = { throw IOException("connection refused") }

        assertFailsWith<AppError.ServerUnreachable> { library.getMovies() }

        server.online.value = false
        assertFailsWith<AppError.NoNetwork> { library.getMovies() }
    }
}
