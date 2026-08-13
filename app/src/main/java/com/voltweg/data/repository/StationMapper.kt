package com.voltweg.data.repository

import com.voltweg.core.database.entity.StationEntity
import com.voltweg.core.network.model.StationResponseDto.StationResponseDtoItem
import com.voltweg.data.ChargerSpeedCategory
import com.voltweg.data.ChargerStatus
import com.voltweg.data.ChargingPoint
import com.voltweg.data.ChargingStation
import com.voltweg.data.ConnectorType
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

object StationMapper {

    fun toEntity(dto: StationResponseDtoItem): StationEntity {
        val addressInfo = dto.addressInfo
        val connectors = dto.connections
        val connectorLabel = dominantConnectorLabel(connectors)
        val totalConnectors = connectors.sumOf { it.quantity }.takeIf { it > 0 }
            ?: dto.numberOfPoints

        return StationEntity(
            id = dto.uUID?.takeIf { it.isNotBlank() } ?: "ocm-${dto.iD}",
            title = addressInfo?.title?.takeIf { it.isNotBlank() } ?: "Charging Station ${dto.iD}",
            address = listOfNotNull(
                addressInfo?.addressLine1?.takeIf { it.isNotBlank() },
                addressInfo?.addressLine2?.takeIf { it.isNotBlank() }
            ).joinToString(", "),
            city = addressInfo?.town?.takeIf { it.isNotBlank() } ?: "Unknown",
            district = addressInfo?.stateOrProvince?.takeIf { it.isNotBlank() } ?: "",
            latitude = addressInfo?.latitude ?: 0.0,
            longitude = addressInfo?.longitude ?: 0.0,
            distanceKm = addressInfo?.distance ?: 0.0,
            connectorLabel = connectorLabel,
            maxPowerKw = connectors.maxOfOrNull { it.powerKW.roundToInt() } ?: 0,
            totalConnectors = totalConnectors,
            availableConnectors = totalConnectors,
            isOperational = dto.statusTypeID == OPERATIONAL_STATUS_TYPE_ID,
            operatorName = "Open Charge Map",
            lastUpdated = relativeTime(dto.dateLastStatusUpdate)
        )
    }

    fun toEntity(dtos: List<StationResponseDtoItem>): List<StationEntity> = dtos.map(::toEntity)

    fun toDomain(entity: StationEntity): ChargingStation {
        val connectorType = when (entity.connectorLabel) {
            "CHAdeMO" -> ConnectorType.CHADEMO
            "Type 2" -> ConnectorType.TYPE2
            else -> ConnectorType.CCS2
        }
        val points = if (entity.totalConnectors > 0) {
            listOf(
                ChargingPoint(
                    id = "${entity.id}-conn",
                    connectorType = connectorType,
                    powerKw = entity.maxPowerKw,
                    status = if (entity.isOperational) ChargerStatus.AVAILABLE else ChargerStatus.NOT_OPERATIONAL,
                    totalPoints = entity.totalConnectors,
                    availablePoints = entity.availableConnectors
                )
            )
        } else {
            emptyList()
        }

        return ChargingStation(
            id = entity.id,
            name = entity.title,
            city = entity.city,
            district = entity.district,
            address = entity.address,
            distanceKm = entity.distanceKm,
            speedCategory = speedCategoryFor(entity.maxPowerKw),
            maxPowerKw = entity.maxPowerKw,
            totalConnectors = entity.totalConnectors,
            availableConnectors = entity.availableConnectors,
            status = if (entity.isOperational) ChargerStatus.OPERATIONAL else ChargerStatus.NOT_OPERATIONAL,
            operatorName = entity.operatorName,
            lastUpdated = entity.lastUpdated,
            points = points
        )
    }

    fun toDomain(entities: List<StationEntity>): List<ChargingStation> = entities.map(::toDomain)

    private fun speedCategoryFor(maxPowerKw: Int): ChargerSpeedCategory = when {
        maxPowerKw >= 100 -> ChargerSpeedCategory.ULTRA_FAST
        maxPowerKw >= 22 -> ChargerSpeedCategory.FAST
        else -> ChargerSpeedCategory.AC
    }

    private fun dominantConnectorLabel(connections: List<StationResponseDtoItem.Connection>): String {
        val mostCommon = connections
            .groupBy { it.connectionTypeID }
            .maxByOrNull { (_, items) -> items.sumOf { it.quantity } }
            ?.key
        return when (mostCommon) {
            CHADEMO_CONNECTION_TYPE_ID -> "CHAdeMO"
            TYPE2_CONNECTION_TYPE_ID -> "Type 2"
            else -> "CCS"
        }
    }

    private fun relativeTime(isoDate: String?): String {
        if (isoDate.isNullOrBlank()) return "Recently"
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val millis = formatter.parse(isoDate.take(19))?.time ?: return "Recently"
            val minutesAgo = (System.currentTimeMillis() - millis) / 60_000L
            when {
                minutesAgo < 1 -> "Just now"
                minutesAgo < 60 -> "${minutesAgo}m ago"
                minutesAgo < 1440 -> "${minutesAgo / 60}h ago"
                else -> "${minutesAgo / 1440}d ago"
            }
        } catch (_: Exception) {
            "Recently"
        }
    }

    // Open Charge Map reference data IDs
    private const val OPERATIONAL_STATUS_TYPE_ID = 50
    private const val CHADEMO_CONNECTION_TYPE_ID = 2
    private const val TYPE2_CONNECTION_TYPE_ID = 25
}
