package com.darkyellowstudios.localflix.data.sample

import com.darkyellowstudios.localflix.model.Movie

/** Placeholder catalogue until the home screen loads from the server (Phase 3). */
object SampleMovies {
    val movies = listOf(
        Movie("1", "Neon Horizon", 2024, "PG-13", "2h 10m", 8.4, "SCI-FI", "A journey into the neon future.", "", emptyList()),
        Movie("2", "Velocity", 2023, "R", "1h 45m", 7.9, "ACTION", "Fast cars and high stakes.", "", emptyList()),
        Movie("3", "The Corridor", 2024, "PG-13", "1h 55m", 8.1, "THRILLER", "An endless hallway of mystery.", "", emptyList()),
        Movie("4", "Shattered Glass", 2022, "PG-13", "2h 05m", 7.5, "MYSTERY", "Breaking the silence.", "", emptyList()),
        Movie("5", "Sentient", 2025, "R", "2h 15m", 9.0, "SCI-FI", "When machines wake up.", "", emptyList()),
        Movie("6", "Apex", 2023, "PG-13", "1h 50m", 8.2, "ACTION", "Survival of the fittest.", "", emptyList()),
    )

    fun byId(id: String): Movie? = movies.firstOrNull { it.id == id }
}
