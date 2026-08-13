package com.voltweg.core.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.voltweg.core.network.model.StationResponseDto.StationResponseDtoItem

@Serializable
class StationResponseDto : ArrayList<StationResponseDtoItem>(){
    @Serializable
    data class StationResponseDtoItem(
        @SerialName("AddressInfo")
        val addressInfo: AddressInfo? = null,
        @SerialName("Connections")
        val connections: List<Connection> = emptyList(),
        @SerialName("DataProviderID")
        val dataProviderID: Int = 0,
        @SerialName("DataProvidersReference")
        val dataProvidersReference: String? = null,
        @SerialName("DataQualityLevel")
        val dataQualityLevel: Int = 0,
        @SerialName("DateCreated")
        val dateCreated: String? = null,
        @SerialName("DateLastStatusUpdate")
        val dateLastStatusUpdate: String? = null,
        @SerialName("DateLastVerified")
        val dateLastVerified: String? = null,
        @SerialName("GeneralComments")
        val generalComments: String? = null,
        @SerialName("ID")
        val iD: Int = 0,
        @SerialName("IsRecentlyVerified")
        val isRecentlyVerified: Boolean = false,
        @SerialName("MetadataValues")
        val metadataValues: List<MetadataValue> = emptyList(),
        @SerialName("NumberOfPoints")
        val numberOfPoints: Int = 0,
        @SerialName("OperatorID")
        val operatorID: Int = 0,
        @SerialName("OperatorsReference")
        val operatorsReference: String? = null,
        @SerialName("StatusTypeID")
        val statusTypeID: Int = 0,
        @SerialName("SubmissionStatusTypeID")
        val submissionStatusTypeID: Int = 0,
        @SerialName("UUID")
        val uUID: String? = null,
        @SerialName("UsageCost")
        val usageCost: String? = null,
        @SerialName("UsageTypeID")
        val usageTypeID: Int = 0
    ) {
        @Serializable
        data class AddressInfo(
            @SerialName("AccessComments")
            val accessComments: String? = null,
            @SerialName("AddressLine1")
            val addressLine1: String? = null,
            @SerialName("AddressLine2")
            val addressLine2: String? = null,
            @SerialName("ContactEmail")
            val contactEmail: String? = null,
            @SerialName("ContactTelephone1")
            val contactTelephone1: String? = null,
            @SerialName("ContactTelephone2")
            val contactTelephone2: String? = null,
            @SerialName("CountryID")
            val countryID: Int = 0,
            @SerialName("Distance")
            val distance: Double = 0.0,
            @SerialName("DistanceUnit")
            val distanceUnit: Int = 0,
            @SerialName("ID")
            val iD: Int = 0,
            @SerialName("Latitude")
            val latitude: Double = 0.0,
            @SerialName("Longitude")
            val longitude: Double = 0.0,
            @SerialName("Postcode")
            val postcode: String? = null,
            @SerialName("RelatedURL")
            val relatedURL: String? = null,
            @SerialName("StateOrProvince")
            val stateOrProvince: String? = null,
            @SerialName("Title")
            val title: String? = null,
            @SerialName("Town")
            val town: String? = null
        )

        @Serializable
        data class Connection(
            @SerialName("Amps")
            val amps: Int = 0,
            @SerialName("ConnectionTypeID")
            val connectionTypeID: Int = 0,
            @SerialName("CurrentTypeID")
            val currentTypeID: Int = 0,
            @SerialName("ID")
            val iD: Int = 0,
            @SerialName("LevelID")
            val levelID: Int = 0,
            @SerialName("PowerKW")
            val powerKW: Double = 0.0,
            @SerialName("Quantity")
            val quantity: Int = 0,
            @SerialName("StatusTypeID")
            val statusTypeID: Int = 0,
            @SerialName("Voltage")
            val voltage: Int = 0
        )

        @Serializable
        data class MetadataValue(
            @SerialName("ID")
            val iD: Int = 0,
            @SerialName("ItemValue")
            val itemValue: String? = null,
            @SerialName("MetadataFieldID")
            val metadataFieldID: Int = 0
        )
    }
}
