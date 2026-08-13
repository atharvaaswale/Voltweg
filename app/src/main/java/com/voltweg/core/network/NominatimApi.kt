package com.voltweg.core.network

import com.voltweg.core.network.model.GeocodingItemDto
import com.voltweg.core.network.model.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NominatimApi {
    @Headers("User-Agent: Voltweg/1.0 (Android Mobile App)")
    @GET("search")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 5
    ): List<GeocodingItemDto>
}
