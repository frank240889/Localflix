package com.darkyellowstudios.localflix.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Jellyfin REST API shapes — only the fields the app uses.

@Serializable
internal data class AuthenticateByNameRequest(
    @SerialName("Username") val username: String,
    @SerialName("Pw") val pw: String,
)

@Serializable
internal data class AuthenticationResult(
    @SerialName("User") val user: UserDto,
    @SerialName("AccessToken") val accessToken: String,
)

@Serializable
internal data class UserDto(
    @SerialName("Id") val id: String,
    @SerialName("Name") val name: String,
)

@Serializable
internal data class ItemsResult(
    @SerialName("Items") val items: List<BaseItemDto> = emptyList(),
)

@Serializable
internal data class BaseItemDto(
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
internal data class PersonDto(
    @SerialName("Id") val id: String,
    @SerialName("Name") val name: String = "",
    @SerialName("Role") val role: String? = null,
    @SerialName("Type") val type: String? = null,
)
