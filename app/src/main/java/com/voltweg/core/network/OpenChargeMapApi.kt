package com.voltweg.core.network

import com.voltweg.core.network.model.StationResponseDto.StationResponseDtoItem
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenChargeMapApi {

    @GET("poi/")
    suspend fun getNearbyStations(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("distance") distance: Double = 10.0,
        @Query("distanceunit") distanceUnit: String = "KM",
        @Query("maxresults") maxResults: Int = 10,
        @Query("compact") compact: Boolean = true,
        @Query("verbose") verbose: Boolean = false
    ): List<StationResponseDtoItem>
}
