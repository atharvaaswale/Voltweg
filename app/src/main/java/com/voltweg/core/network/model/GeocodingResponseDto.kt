package com.voltweg.core.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.voltweg.core.network.model.GeocodingResponseDto.GeocodingResponseDtoItem


class GeocodingResponseDto : ArrayList<GeocodingResponseDtoItem>(){
    @Serializable
    data class GeocodingResponseDtoItem(
        @SerialName("address")
        val address: Address,
        @SerialName("addresstype")
        val addresstype: String,
        @SerialName("boundingbox")
        val boundingbox: List<String>,
        @SerialName("class")
        val classX: String,
        @SerialName("display_name")
        val displayName: String,
        @SerialName("importance")
        val importance: Double,
        @SerialName("lat")
        val lat: String,
        @SerialName("licence")
        val licence: String,
        @SerialName("lon")
        val lon: String,
        @SerialName("name")
        val name: String,
        @SerialName("osm_id")
        val osmId: Int,
        @SerialName("osm_type")
        val osmType: String,
        @SerialName("place_id")
        val placeId: Int,
        @SerialName("place_rank")
        val placeRank: Int,
        @SerialName("type")
        val type: String
    ) {
        @Serializable
        data class Address(
            @SerialName("city")
            val city: String,
            @SerialName("country")
            val country: String,
            @SerialName("country_code")
            val countryCode: String,
            @SerialName("ISO3166-2-lvl4")
            val iSO31662Lvl4: String
        )
    }
}