package com.darkyellowstudios.localflix.model

data class Movie(
    val id: String,
    val title: String,
    val year: Int,
    val rating: String,
    val duration: String,
    val score: Double,
    val genre: String,
    val description: String,
    val posterUrl: String,
    val cast: List<CastMember>
)

data class CastMember(
    val name: String,
    val role: String,
    val imageUrl: String
)

data class UserProfile(
    val id: String,
    val name: String,
    val imageUrl: String,
    val isAddProfile: Boolean = false
)
