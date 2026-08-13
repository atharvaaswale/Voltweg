package com.voltweg.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingItemDto(
    @SerialName("place_id")
    val placeId: Long? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("lat")
    val lat: String? = null,
    @SerialName("lon")
    val lon: String? = null,
    @SerialName("addresstype")
    val addresstype: String? = null,
    @SerialName("class")
    val classX: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("importance")
    val importance: Double? = null,
    @SerialName("licence")
    val licence: String? = null,
    @SerialName("osm_id")
    val osmId: Long? = null,
    @SerialName("osm_type")
    val osmType: String? = null,
    @SerialName("place_rank")
    val placeRank: Int? = null,
    @SerialName("address")
    val address: AddressDto? = null,
    @SerialName("boundingbox")
    val boundingbox: List<String>? = null
) {
    @Serializable
    data class AddressDto(
        @SerialName("city")
        val city: String? = null,
        @SerialName("town")
        val town: String? = null,
        @SerialName("village")
        val village: String? = null,
        @SerialName("state")
        val state: String? = null,
        @SerialName("country")
        val country: String? = null,
        @SerialName("country_code")
        val countryCode: String? = null,
        @SerialName("postcode")
        val postcode: String? = null
    )
}

// Aliases to ensure backward-compatibility across all modules
typealias GeocodingResponseDtoItem = GeocodingItemDto
typealias GeocodingResponseDto = List<GeocodingItemDto>
