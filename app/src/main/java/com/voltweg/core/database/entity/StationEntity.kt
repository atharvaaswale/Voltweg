package com.voltweg.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val address: String,
    val city: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val connectorLabel: String,
    val maxPowerKw: Int,
    val totalConnectors: Int,
    val availableConnectors: Int,
    val isOperational: Boolean,
    val operatorName: String,
    val lastUpdated: String,
    val cachedAt: Long = System.currentTimeMillis()
)
