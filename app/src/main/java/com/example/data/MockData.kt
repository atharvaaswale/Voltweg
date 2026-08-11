package com.example.data

object MockData {
    val sampleStations = listOf(
        ChargingStation(
            id = "alexanderplatz_hub",
            name = "Alexanderplatz Hub",
            city = "Berlin, Germany",
            district = "Mitte, Berlin",
            address = "Alexanderstraße 1, 10178 Berlin, Germany",
            distanceKm = 1.2,
            isFavorite = true,
            speedCategory = ChargerSpeedCategory.ULTRA_FAST,
            maxPowerKw = 150,
            totalConnectors = 6,
            availableConnectors = 4,
            status = ChargerStatus.AVAILABLE,
            operatorName = "Vattenfall InCharge",
            operatorSupport = "+49 800 1234567",
            lastUpdated = "12 min ago",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBcNEXzCCa85Bef6uX3s0G1xVz8LhEn7b9oExavSfcNDdpHDBSOIk2vyaq8knsNrc6ob6hD4Il4W-_9mXU7bTb_pjJVWCKktaHQ-HkW7oGzySWCThQi4LXX55Zq2qqVoryQJAGLZJo1Nz9QggolM30ZIWDVkrnd1sXG4NqJ1p3mKI36suJJwWJUWKvyVrVN5sqQinZY3kFuo6XEBXu5jSN9ORNbrRjo5PxU6FzfqZBMxA5hF2zIGA8",
            points = listOf(
                ChargingPoint("p1", ConnectorType.CCS2, 150, ChargerStatus.AVAILABLE, 3, 3),
                ChargingPoint("p2", ConnectorType.TYPE2, 22, ChargerStatus.AVAILABLE, 1, 1),
                ChargingPoint("p3", ConnectorType.CCS2, 150, ChargerStatus.OCCUPIED, 2, 0)
            ),
            mapXRatio = 0.50f,
            mapYRatio = 0.52f
        ),
        ChargingStation(
            id = "mitte_supercharger",
            name = "Mitte Supercharger",
            city = "Berlin, Germany",
            district = "Mitte, Berlin",
            address = "Friedrichstraße 120, 10117 Berlin, Germany",
            distanceKm = 0.8,
            isFavorite = false,
            speedCategory = ChargerSpeedCategory.ULTRA_FAST,
            maxPowerKw = 150,
            totalConnectors = 6,
            availableConnectors = 6,
            status = ChargerStatus.AVAILABLE,
            operatorName = "Tesla / Public Network",
            operatorSupport = "+49 800 9876543",
            lastUpdated = "5 min ago",
            points = listOf(
                ChargingPoint("p4", ConnectorType.CCS2, 150, ChargerStatus.AVAILABLE, 6, 6)
            ),
            mapXRatio = 0.38f,
            mapYRatio = 0.35f
        ),
        ChargingStation(
            id = "friedrichshain_fast",
            name = "Friedrichshain Fast",
            city = "Berlin, Germany",
            district = "Friedrichshain, Berlin",
            address = "Warschauer Str. 34, 10243 Berlin, Germany",
            distanceKm = 1.2,
            isFavorite = false,
            speedCategory = ChargerSpeedCategory.FAST,
            maxPowerKw = 50,
            totalConnectors = 6,
            availableConnectors = 0,
            status = ChargerStatus.OCCUPIED,
            operatorName = "Allego GmbH",
            operatorSupport = "+49 800 5554321",
            lastUpdated = "18 min ago",
            points = listOf(
                ChargingPoint("p5", ConnectorType.CCS2, 50, ChargerStatus.OCCUPIED, 4, 0),
                ChargingPoint("p6", ConnectorType.TYPE2, 22, ChargerStatus.OCCUPIED, 2, 0)
            ),
            mapXRatio = 0.68f,
            mapYRatio = 0.42f
        ),
        ChargingStation(
            id = "kreuzberg_hub",
            name = "Kreuzberg Hub",
            city = "Berlin, Germany",
            district = "Kreuzberg, Berlin",
            address = "Oranienstraße 50, 10969 Berlin, Germany",
            distanceKm = 2.5,
            isFavorite = false,
            speedCategory = ChargerSpeedCategory.ULTRA_FAST,
            maxPowerKw = 300,
            totalConnectors = 12,
            availableConnectors = 12,
            status = ChargerStatus.AVAILABLE,
            operatorName = "Ionity GmbH",
            operatorSupport = "+49 800 7778899",
            lastUpdated = "2 min ago",
            points = listOf(
                ChargingPoint("p7", ConnectorType.CCS2, 300, ChargerStatus.AVAILABLE, 12, 12)
            ),
            mapXRatio = 0.32f,
            mapYRatio = 0.68f
        ),
        ChargingStation(
            id = "potsdamer_platz_underground",
            name = "Potsdamer Platz Underground",
            city = "Berlin, Germany",
            district = "Tiergarten, Berlin",
            address = "Linkstraße 4, 10785 Berlin, Germany",
            distanceKm = 2.5,
            isFavorite = false,
            speedCategory = ChargerSpeedCategory.ULTRA_FAST,
            maxPowerKw = 350,
            totalConnectors = 2,
            availableConnectors = 0,
            status = ChargerStatus.OCCUPIED,
            operatorName = "EnBW Mobility",
            operatorSupport = "+49 800 3332211",
            lastUpdated = "30 min ago",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCYC4qTARJcxpHN-eaCXEJGNzTM79adyz5bgwRSK-BZ6jQHK3rZKCxcdKCg0iEgS9HYWJMxlQ-tapy0OeRgEdzhAvjJlhambXwK0KZpi9f3jVADl1W4HcsUfm8fscXqeggdrb7ACT-Hs-sA85oglCMzpKY0sOazXxjr8WFnesInaFW2Vqu5f3cbWJPWNDP97ZS6dGVUeiOx3yNKq5X3PysvdGIPF0u_YewmdouSAa6hC2CyTVNLPbk",
            points = listOf(
                ChargingPoint("p8", ConnectorType.CCS2, 350, ChargerStatus.OCCUPIED, 2, 0)
            ),
            mapXRatio = 0.22f,
            mapYRatio = 0.48f
        ),
        ChargingStation(
            id = "kudamm_west",
            name = "Kudamm West",
            city = "Berlin, Germany",
            district = "Charlottenburg, Berlin",
            address = "Kurfürstendamm 194, 10707 Berlin, Germany",
            distanceKm = 4.2,
            isFavorite = true,
            speedCategory = ChargerSpeedCategory.FAST,
            maxPowerKw = 50,
            totalConnectors = 4,
            availableConnectors = 0,
            status = ChargerStatus.FULL,
            operatorName = "Berliner Stadtwerke",
            operatorSupport = "+49 800 1122334",
            lastUpdated = "1 hour ago",
            points = listOf(
                ChargingPoint("p9", ConnectorType.CCS2, 50, ChargerStatus.FULL, 4, 0)
            ),
            mapXRatio = 0.15f,
            mapYRatio = 0.58f
        ),
        ChargingStation(
            id = "olympiapark",
            name = "Olympiapark Fast",
            city = "Berlin, Germany",
            district = "Westend, Berlin",
            address = "Olympischer Platz 3, 14053 Berlin, Germany",
            distanceKm = 5.8,
            isFavorite = false,
            speedCategory = ChargerSpeedCategory.ULTRA_FAST,
            maxPowerKw = 150,
            totalConnectors = 4,
            availableConnectors = 2,
            status = ChargerStatus.AVAILABLE,
            operatorName = "Vattenfall InCharge",
            operatorSupport = "+49 800 1234567",
            lastUpdated = "8 min ago",
            mapXRatio = 0.10f,
            mapYRatio = 0.25f
        )
    )

    val recentSearches = listOf("Berlin", "Munich", "Hamburg")
}
