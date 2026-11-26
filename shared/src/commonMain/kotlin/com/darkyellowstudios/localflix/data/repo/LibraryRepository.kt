package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.remote.toMovie
import com.darkyellowstudios.localflix.model.AppError
import com.darkyellowstudios.localflix.model.Movie

class LibraryRepository(
    private val api: JellyfinApi,
    private val auth: AuthRepository,
) {
    /** All movies in the signed-in user's libraries. Throws [AppError] on failure. */
    suspend fun getMovies(): List<Movie> =
        auth.authorized { session -> api.getMovies(session).map { it.toMovie(session.serverUrl) } }

    fun streamUrl(itemId: String): String = api.streamUrl(auth.requireSession(), itemId)

    fun hlsUrl(itemId: String): String = api.hlsUrl(auth.requireSession(), itemId)
}
