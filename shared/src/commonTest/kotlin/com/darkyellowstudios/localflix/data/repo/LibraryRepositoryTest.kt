package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.data.FakeJellyfinServer
import com.darkyellowstudios.localflix.model.AppError
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LibraryRepositoryTest {
    private val server = FakeJellyfinServer()
    private val library = LibraryRepository(server.api, server.store, server.connectivity)

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
    fun revokedTokenIsUnauthorized() = runTest {
        server.signIn()
        server.handler = { respond("", HttpStatusCode.Unauthorized) }

        assertFailsWith<AppError.Unauthorized> { library.getMovies() }
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
