package com.voltweg.data.repository

import com.voltweg.core.database.dao.StationDao
import com.voltweg.core.network.OpenChargeMapApi
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class StationRepositoryImpl(
    private val dao: StationDao,
    private val api: OpenChargeMapApi,
    private val mapper: StationMapper = StationMapper
) : StationRepository {

    override fun getStations(
        latitude: Double,
        longitude: Double,
        distanceKm: Double
    ): Flow<StationResult> = flow {
        // Single source of truth: emit only cached stations that match the
        // active queried coordinates. Never emit fallback or dummy stations.
        emitCached(latitude, longitude, distanceKm, isOffline = false)

        // Refresh from the network in parallel; upsert on success.
        try {
            val remote = api.getNearbyStations(
                latitude = latitude,
                longitude = longitude,
                distance = distanceKm
            )
            val entities = remote.map(mapper::toEntity)
            dao.upsertStations(entities)
            emit(StationResult(entities.map(mapper::toDomain), isOffline = false))
        } catch (_: IOException) {
            // No network: fall back to the last cached snapshot for these coordinates.
            emitCached(latitude, longitude, distanceKm, isOffline = true)
        } catch (_: HttpException) {
            // API error: fall back to the last cached snapshot for these coordinates.
            emitCached(latitude, longitude, distanceKm, isOffline = true)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun FlowCollector<StationResult>.emitCached(
        latitude: Double,
        longitude: Double,
        distanceKm: Double,
        isOffline: Boolean
    ) {
        val cached = dao.getAllCachedStations().first()
            .filter { entity -> distanceInKm(latitude, longitude, entity.latitude, entity.longitude) <= distanceKm }
        emit(StationResult(cached.map(mapper::toDomain), isOffline = isOffline))
    }

    private fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
        return earthRadiusKm * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }
}
