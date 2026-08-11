package com.example.data

enum class ConnectorType(val displayName: String) {
    CCS2("CCS 2"),
    TYPE2("Type 2"),
    CHADEMO("CHAdeMO")
}

enum class ChargerStatus(val displayName: String) {
    AVAILABLE("Available"),
    OPERATIONAL("Operational"),
    OCCUPIED("Occupied"),
    FULL("Full"),
    NOT_OPERATIONAL("Not operational"),
    UNKNOWN("Unknown")
}

enum class ChargerSpeedCategory(val displayName: String) {
    AC("AC"),
    FAST("Fast"),
    ULTRA_FAST("Ultra-fast")
}

data class ChargingPoint(
    val id: String,
    val connectorType: ConnectorType,
    val powerKw: Int,
    val status: ChargerStatus,
    val totalPoints: Int,
    val availablePoints: Int
)

data class ChargingStation(
    val id: String,
    val name: String,
    val city: String,
    val district: String,
    val address: String,
    val distanceKm: Double,
    val isFavorite: Boolean = false,
    val speedCategory: ChargerSpeedCategory,
    val maxPowerKw: Int,
    val totalConnectors: Int,
    val availableConnectors: Int,
    val status: ChargerStatus,
    val operatorName: String = "Vattenfall InCharge",
    val operatorSupport: String = "+49 800 1234567",
    val lastUpdated: String = "12 min ago",
    val imageUrl: String? = null,
    val points: List<ChargingPoint> = emptyList(),
    // Normalized map coordinates for custom canvas drawing (0.0 to 1.0)
    val mapXRatio: Float = 0.5f,
    val mapYRatio: Float = 0.5f
)
