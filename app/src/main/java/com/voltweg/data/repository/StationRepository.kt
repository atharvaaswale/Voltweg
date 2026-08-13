package com.voltweg.data.repository

import com.voltweg.data.ChargingStation
import kotlinx.coroutines.flow.Flow

data class StationResult(
    val stations: List<ChargingStation>,
    val isOffline: Boolean
)

interface StationRepository {

    fun getStations(
        latitude: Double,
        longitude: Double,
        distanceKm: Double = 10.0
    ): Flow<StationResult>
}
