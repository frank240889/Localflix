package com.darkyellowstudios.localflix.data.repo

import com.darkyellowstudios.localflix.data.remote.JellyfinApi
import com.darkyellowstudios.localflix.data.remote.toMovie
import com.darkyellowstudios.localflix.data.storage.SettingsStore
import com.darkyellowstudios.localflix.model.JellyfinSession
import com.darkyellowstudios.localflix.model.Movie

class LibraryRepository(
    private val api: JellyfinApi,
    private val store: SettingsStore,
) {
    /** All movies in the signed-in user's libraries. */
    suspend fun getMovies(): List<Movie> {
        val session = requireSession()
        return api.getMovies(session).map { it.toMovie(session.serverUrl) }
    }

    fun streamUrl(itemId: String): String = api.streamUrl(requireSession(), itemId)

    fun hlsUrl(itemId: String): String = api.hlsUrl(requireSession(), itemId)

    private fun requireSession(): JellyfinSession =
        checkNotNull(store.session()) { "Not signed in to a Jellyfin server" }
}
