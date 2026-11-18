package com.darkyellowstudios.localflix.data.remote

import com.darkyellowstudios.localflix.data.remote.dto.BaseItemDto
import com.darkyellowstudios.localflix.model.CastMember
import com.darkyellowstudios.localflix.model.Movie

internal fun BaseItemDto.toMovie(serverUrl: String) = Movie(
    id = id,
    title = name,
    year = productionYear ?: 0,
    rating = officialRating ?: "",
    duration = runTimeTicks?.let(::formatTicks) ?: "",
    score = communityRating ?: 0.0,
    genre = genres.firstOrNull() ?: "",
    description = overview ?: "",
    posterUrl = imageUrl(serverUrl, id),
    cast = people.filter { it.type == "Actor" }.map {
        CastMember(
            name = it.name,
            role = it.role ?: "",
            imageUrl = imageUrl(serverUrl, it.id),
        )
    },
)

internal fun imageUrl(serverUrl: String, itemId: String, type: String = "Primary") =
    "$serverUrl/Items/$itemId/Images/$type"

/** Jellyfin ticks are 100 ns: 10,000,000 per second. */
internal fun formatTicks(ticks: Long): String {
    val totalMinutes = ticks / 600_000_000
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
