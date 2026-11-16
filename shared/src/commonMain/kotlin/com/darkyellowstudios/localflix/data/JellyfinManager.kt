package com.darkyellowstudios.localflix.data

import com.darkyellowstudios.localflix.model.CastMember
import com.darkyellowstudios.localflix.model.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.random.Random

/**
 * Talks to a Jellyfin server over its REST API (works on every KMP target).
 *
 * Flow: [login] -> [getMovies] -> play [streamUrl] in a platform video player.
 */
object JellyfinManager {
    private const val CLIENT_NAME = "Localflix"
    private const val CLIENT_VERSION = "1.0.0"
    private const val DEVICE_NAME = "Localflix KMP"

    // TODO: persist this per install so the server doesn't see a new device each launch.
    private val deviceId = Random.nextLong().toULong().toString(16)

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        defaultRequest {
            header(HttpHeaders.Authorization, authHeader())
        }
    }

    var session: JellyfinSession? = null
        private set

    private fun authHeader(): String {
        val token = session?.accessToken?.let { """, Token="$it"""" } ?: ""
        return """MediaBrowser Client="$CLIENT_NAME", Device="$DEVICE_NAME", """ +
            """DeviceId="$deviceId", Version="$CLIENT_VERSION"$token"""
    }

    /** Signs in with username/password and stores the session. Throws on failure. */
    suspend fun login(serverUrl: String, username: String, password: String): JellyfinSession {
        val baseUrl = serverUrl.trim().trimEnd('/')
        val result: AuthenticationResult = client.post("$baseUrl/Users/AuthenticateByName") {
            contentType(ContentType.Application.Json)
            setBody(AuthenticateByNameRequest(username = username, pw = password))
        }.body()
        return JellyfinSession(
            serverUrl = baseUrl,
            userId = result.user.id,
            userName = result.user.name,
            accessToken = result.accessToken,
        ).also { session = it }
    }

    fun logout() {
        session = null
    }

    /** All movies in the user's libraries, mapped to the app's [Movie] model. */
    suspend fun getMovies(): List<Movie> {
        val s = requireSession()
        val result: ItemsResult = client.get("${s.serverUrl}/Items") {
            parameter("userId", s.userId)
            parameter("IncludeItemTypes", "Movie")
            parameter("Recursive", true)
            parameter("SortBy", "SortName")
            parameter("Fields", "Overview,Genres,People")
        }.body()
        return result.items.map { it.toMovie(s) }
    }

    /** Direct-play URL (original file, no transcoding). Pass to a platform video player. */
    fun streamUrl(itemId: String): String {
        val s = requireSession()
        return "${s.serverUrl}/Videos/$itemId/stream?static=true&api_key=${s.accessToken}"
    }

    /** HLS URL; the server transcodes when the device can't play the original format. */
    fun hlsUrl(itemId: String): String {
        val s = requireSession()
        return "${s.serverUrl}/Videos/$itemId/master.m3u8" +
            "?MediaSourceId=$itemId&DeviceId=$deviceId&api_key=${s.accessToken}"
    }

    fun imageUrl(itemId: String, type: String = "Primary"): String {
        val s = requireSession()
        return "${s.serverUrl}/Items/$itemId/Images/$type"
    }

    private fun requireSession(): JellyfinSession =
        checkNotNull(session) { "Not logged in to a Jellyfin server" }

    private fun BaseItemDto.toMovie(s: JellyfinSession) = Movie(
        id = id,
        title = name,
        year = productionYear ?: 0,
        rating = officialRating ?: "",
        duration = runTimeTicks?.let(::formatTicks) ?: "",
        score = communityRating ?: 0.0,
        genre = genres.firstOrNull() ?: "",
        description = overview ?: "",
        posterUrl = "${s.serverUrl}/Items/$id/Images/Primary",
        cast = people.filter { it.type == "Actor" }.map {
            CastMember(
                name = it.name,
                role = it.role ?: "",
                imageUrl = "${s.serverUrl}/Items/${it.id}/Images/Primary",
            )
        },
    )

    // Jellyfin ticks are 100 ns: 10,000,000 per second.
    private fun formatTicks(ticks: Long): String {
        val totalMinutes = ticks / 600_000_000
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }
}

data class JellyfinSession(
    val serverUrl: String,
    val userId: String,
    val userName: String,
    val accessToken: String,
)

// --- Jellyfin API DTOs (only the fields we use) ---

@Serializable
private data class AuthenticateByNameRequest(
    @SerialName("Username") val username: String,
    @SerialName("Pw") val pw: String,
)

@Serializable
private data class AuthenticationResult(
    @SerialName("User") val user: UserDto,
    @SerialName("AccessToken") val accessToken: String,
)

@Serializable
private data class UserDto(
    @SerialName("Id") val id: String,
    @SerialName("Name") val name: String,
)

@Serializable
private data class ItemsResult(
    @SerialName("Items") val items: List<BaseItemDto> = emptyList(),
)

@Serializable
private data class BaseItemDto(
    @SerialName("Id") val id: String,
    @SerialName("Name") val name: String = "",
    @SerialName("ProductionYear") val productionYear: Int? = null,
    @SerialName("OfficialRating") val officialRating: String? = null,
    @SerialName("CommunityRating") val communityRating: Double? = null,
    @SerialName("RunTimeTicks") val runTimeTicks: Long? = null,
    @SerialName("Overview") val overview: String? = null,
    @SerialName("Genres") val genres: List<String> = emptyList(),
    @SerialName("People") val people: List<PersonDto> = emptyList(),
)

@Serializable
private data class PersonDto(
    @SerialName("Id") val id: String,
    @SerialName("Name") val name: String = "",
    @SerialName("Role") val role: String? = null,
    @SerialName("Type") val type: String? = null,
)
