package com.darkyellowstudios.localflix.data.remote

import com.darkyellowstudios.localflix.data.remote.dto.ItemsResult
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class MappersTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun mapsJellyfinItemToMovie() {
        val body = """
            {"Items":[{
              "Id":"abc","Name":"Arrival","ProductionYear":2016,"OfficialRating":"PG-13",
              "CommunityRating":7.9,"RunTimeTicks":69600000000,"Overview":"Linguist meets aliens.",
              "Genres":["Science Fiction","Drama"],"UnusedField":true,
              "People":[
                {"Id":"p1","Name":"Amy Adams","Role":"Louise Banks","Type":"Actor"},
                {"Id":"p2","Name":"Denis Villeneuve","Type":"Director"}
              ]
            }]}
        """.trimIndent()

        val movie = json.decodeFromString<ItemsResult>(body).items.single()
            .toMovie("http://192.168.1.10:8096")

        assertEquals("abc", movie.id)
        assertEquals("Arrival", movie.title)
        assertEquals(2016, movie.year)
        assertEquals("1h 56m", movie.duration)
        assertEquals("Science Fiction", movie.genre)
        assertEquals("http://192.168.1.10:8096/Items/abc/Images/Primary", movie.posterUrl)
        assertEquals(listOf("Amy Adams"), movie.cast.map { it.name })
        assertEquals("http://192.168.1.10:8096/Items/p1/Images/Primary", movie.cast.single().imageUrl)
    }

    @Test
    fun formatsShortRuntimesInMinutes() {
        assertEquals("45m", formatTicks(45L * 600_000_000))
    }
}
