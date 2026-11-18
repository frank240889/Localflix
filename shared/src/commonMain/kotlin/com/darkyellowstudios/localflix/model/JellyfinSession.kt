package com.darkyellowstudios.localflix.model

/** A signed-in user on a specific Jellyfin server. */
data class JellyfinSession(
    val serverUrl: String,
    val userId: String,
    val userName: String,
    val accessToken: String,
)
