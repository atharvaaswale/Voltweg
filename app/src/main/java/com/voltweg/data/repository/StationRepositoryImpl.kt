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
        // Single source of truth: emit the cached snapshot immediately.
        emitCached(isOffline = false)

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
            // No network: fall back to the last cached snapshot.
            emitCached(isOffline = true)
        } catch (_: HttpException) {
            // API error: fall back to the last cached snapshot.
            emitCached(isOffline = true)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun FlowCollector<StationResult>.emitCached(isOffline: Boolean) {
        val cached = dao.getAllCachedStations().first()
        emit(StationResult(cached.map(mapper::toDomain), isOffline = isOffline))
    }
}
