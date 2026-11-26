package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.ConnectivityMonitor
import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.remote.toMovie
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.darkyellowstudios.localflix.model.AppError
import com.darkyellowstudios.localflix.model.JellyfinSession
import com.darkyellowstudios.localflix.model.Movie
import com.darkyellowstudios.localflix.model.apiCall

class LibraryRepository(
    private val api: JellyfinApi,
    private val store: SettingsStore,
    private val connectivity: ConnectivityMonitor,
) {
    /** All movies in the signed-in user's libraries. Throws [AppError] on failure. */
    suspend fun getMovies(): List<Movie> {
        val session = requireSession()
        return connectivity.apiCall { api.getMovies(session) }.map { it.toMovie(session.serverUrl) }
    }

    fun streamUrl(itemId: String): String = api.streamUrl(requireSession(), itemId)

    fun hlsUrl(itemId: String): String = api.hlsUrl(requireSession(), itemId)

    private fun requireSession(): JellyfinSession = store.session() ?: throw AppError.Unauthorized()
}
