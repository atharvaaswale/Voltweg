package com.voltweg.core.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferenceDataDto(
    @SerialName("ChargePoint")
    val chargePoint: ChargePoint,
    @SerialName("ChargerTypes")
    val chargerTypes: List<ChargerType>,
    @SerialName("CheckinStatusTypes")
    val checkinStatusTypes: List<CheckinStatusType>,
    @SerialName("ConnectionTypes")
    val connectionTypes: List<ConnectionType>,
    @SerialName("Countries")
    val countries: List<Country>,
    @SerialName("CurrentTypes")
    val currentTypes: List<CurrentType>,
    @SerialName("DataProviders")
    val dataProviders: List<DataProvider>,
    @SerialName("DataTypes")
    val dataTypes: List<DataType>,
    @SerialName("MetadataGroups")
    val metadataGroups: List<MetadataGroup>,
    @SerialName("Operators")
    val operators: List<Operator>,
    @SerialName("StatusTypes")
    val statusTypes: List<StatusType>,
    @SerialName("SubmissionStatusTypes")
    val submissionStatusTypes: List<SubmissionStatusType>,
    @SerialName("UsageTypes")
    val usageTypes: List<UsageType>,
    @SerialName("UserComment")
    val userComment: UserComment,
    @SerialName("UserCommentTypes")
    val userCommentTypes: List<UserCommentType>,
    @SerialName("UserProfile")
    val userProfile: String
) {
    @Serializable
    data class ChargePoint(
        @SerialName("AddressInfo")
        val addressInfo: AddressInfo,
        @SerialName("Connections")
        val connections: List<Connection>,
        @SerialName("DataProvider")
        val dataProvider: DataProvider,
        @SerialName("DataProviderID")
        val dataProviderID: String,
        @SerialName("DataProvidersReference")
        val dataProvidersReference: String,
        @SerialName("DataQualityLevel")
        val dataQualityLevel: Int,
        @SerialName("DateCreated")
        val dateCreated: String,
        @SerialName("DateLastConfirmed")
        val dateLastConfirmed: String,
        @SerialName("DateLastStatusUpdate")
        val dateLastStatusUpdate: String,
        @SerialName("DateLastVerified")
        val dateLastVerified: String,
        @SerialName("DatePlanned")
        val datePlanned: String,
        @SerialName("GeneralComments")
        val generalComments: String,
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsRecentlyVerified")
        val isRecentlyVerified: Boolean,
        @SerialName("MediaItems")
        val mediaItems: String,
        @SerialName("MetadataValues")
        val metadataValues: String,
        @SerialName("NumberOfPoints")
        val numberOfPoints: Int,
        @SerialName("OperatorID")
        val operatorID: String,
        @SerialName("OperatorInfo")
        val operatorInfo: OperatorInfo,
        @SerialName("OperatorsReference")
        val operatorsReference: String,
        @SerialName("ParentChargePointID")
        val parentChargePointID: String,
        @SerialName("PercentageSimilarity")
        val percentageSimilarity: String,
        @SerialName("StatusType")
        val statusType: StatusType,
        @SerialName("StatusTypeID")
        val statusTypeID: String,
        @SerialName("SubmissionStatus")
        val submissionStatus: String,
        @SerialName("SubmissionStatusTypeID")
        val submissionStatusTypeID: String,
        @SerialName("UUID")
        val uUID: String,
        @SerialName("UsageCost")
        val usageCost: String,
        @SerialName("UsageType")
        val usageType: UsageType,
        @SerialName("UsageTypeID")
        val usageTypeID: String,
        @SerialName("UserComments")
        val userComments: String
    ) {
        @Serializable
        data class AddressInfo(
            @SerialName("AccessComments")
            val accessComments: String,
            @SerialName("AddressLine1")
            val addressLine1: String,
            @SerialName("AddressLine2")
            val addressLine2: String,
            @SerialName("ContactEmail")
            val contactEmail: String,
            @SerialName("ContactTelephone1")
            val contactTelephone1: String,
            @SerialName("ContactTelephone2")
            val contactTelephone2: String,
            @SerialName("Country")
            val country: String,
            @SerialName("CountryID")
            val countryID: String,
            @SerialName("Distance")
            val distance: String,
            @SerialName("DistanceUnit")
            val distanceUnit: Int,
            @SerialName("ID")
            val iD: Int,
            @SerialName("Latitude")
            val latitude: Int,
            @SerialName("Longitude")
            val longitude: Int,
            @SerialName("Postcode")
            val postcode: String,
            @SerialName("RelatedURL")
            val relatedURL: String,
            @SerialName("StateOrProvince")
            val stateOrProvince: String,
            @SerialName("Title")
            val title: String,
            @SerialName("Town")
            val town: String
        )

        @Serializable
        data class Connection(
            @SerialName("Amps")
            val amps: String,
            @SerialName("Comments")
            val comments: String,
            @SerialName("ConnectionType")
            val connectionType: String,
            @SerialName("ConnectionTypeID")
            val connectionTypeID: String,
            @SerialName("CurrentType")
            val currentType: String,
            @SerialName("CurrentTypeID")
            val currentTypeID: String,
            @SerialName("ID")
            val iD: Int,
            @SerialName("Level")
            val level: String,
            @SerialName("LevelID")
            val levelID: String,
            @SerialName("PowerKW")
            val powerKW: String,
            @SerialName("Quantity")
            val quantity: String,
            @SerialName("Reference")
            val reference: String,
            @SerialName("StatusType")
            val statusType: String,
            @SerialName("StatusTypeID")
            val statusTypeID: String,
            @SerialName("Voltage")
            val voltage: String
        )

        @Serializable
        data class DataProvider(
            @SerialName("Comments")
            val comments: String,
            @SerialName("DataProviderStatusType")
            val dataProviderStatusType: String,
            @SerialName("DateLastImported")
            val dateLastImported: String,
            @SerialName("ID")
            val iD: Int,
            @SerialName("IsApprovedImport")
            val isApprovedImport: Boolean,
            @SerialName("IsOpenDataLicensed")
            val isOpenDataLicensed: Boolean,
            @SerialName("IsRestrictedEdit")
            val isRestrictedEdit: Boolean,
            @SerialName("License")
            val license: String,
            @SerialName("Title")
            val title: String,
            @SerialName("WebsiteURL")
            val websiteURL: String
        )

        @Serializable
        data class OperatorInfo(
            @SerialName("AddressInfo")
            val addressInfo: String,
            @SerialName("BookingURL")
            val bookingURL: String,
            @SerialName("Comments")
            val comments: String,
            @SerialName("ContactEmail")
            val contactEmail: String,
            @SerialName("FaultReportEmail")
            val faultReportEmail: String,
            @SerialName("ID")
            val iD: Int,
            @SerialName("IsPrivateIndividual")
            val isPrivateIndividual: Boolean,
            @SerialName("IsRestrictedEdit")
            val isRestrictedEdit: Boolean,
            @SerialName("PhonePrimaryContact")
            val phonePrimaryContact: String,
            @SerialName("PhoneSecondaryContact")
            val phoneSecondaryContact: String,
            @SerialName("Title")
            val title: String,
            @SerialName("WebsiteURL")
            val websiteURL: String
        )

        @Serializable
        data class StatusType(
            @SerialName("ID")
            val iD: Int,
            @SerialName("IsOperational")
            val isOperational: Boolean,
            @SerialName("IsUserSelectable")
            val isUserSelectable: Boolean,
            @SerialName("Title")
            val title: String
        )

        @Serializable
        data class UsageType(
            @SerialName("ID")
            val iD: Int,
            @SerialName("IsAccessKeyRequired")
            val isAccessKeyRequired: Boolean,
            @SerialName("IsMembershipRequired")
            val isMembershipRequired: Boolean,
            @SerialName("IsPayAtLocation")
            val isPayAtLocation: Boolean,
            @SerialName("Title")
            val title: String
        )
    }

    @Serializable
    data class ChargerType(
        @SerialName("Comments")
        val comments: String,
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsFastChargeCapable")
        val isFastChargeCapable: Boolean,
        @SerialName("Title")
        val title: String
    )

    @Serializable
    data class CheckinStatusType(
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsAutomatedCheckin")
        val isAutomatedCheckin: Boolean,
        @SerialName("IsPositive")
        val isPositive: Boolean,
        @SerialName("Title")
        val title: String
    )

    @Serializable
    data class ConnectionType(
        @SerialName("FormalName")
        val formalName: String,
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsDiscontinued")
        val isDiscontinued: Boolean,
        @SerialName("IsObsolete")
        val isObsolete: Boolean,
        @SerialName("Title")
        val title: String
    )

    @Serializable
    data class Country(
        @SerialName("ContinentCode")
        val continentCode: String,
        @SerialName("ID")
        val iD: Int,
        @SerialName("ISOCode")
        val iSOCode: String,
        @SerialName("Title")
        val title: String
    )

    @Serializable
    data class CurrentType(
        @SerialName("Description")
        val description: String,
        @SerialName("ID")
        val iD: Int,
        @SerialName("Title")
        val title: String
    )

    @Serializable
    data class DataProvider(
        @SerialName("Comments")
        val comments: String,
        @SerialName("DataProviderStatusType")
        val dataProviderStatusType: DataProviderStatusType,
        @SerialName("DateLastImported")
        val dateLastImported: String,
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsApprovedImport")
        val isApprovedImport: Boolean,
        @SerialName("IsOpenDataLicensed")
        val isOpenDataLicensed: Boolean,
        @SerialName("IsRestrictedEdit")
        val isRestrictedEdit: Boolean,
        @SerialName("License")
        val license: String,
        @SerialName("Title")
        val title: String,
        @SerialName("WebsiteURL")
        val websiteURL: String
    ) {
        @Serializable
        data class DataProviderStatusType(
            @SerialName("ID")
            val iD: Int,
            @SerialName("IsProviderEnabled")
            val isProviderEnabled: Boolean,
            @SerialName("Title")
            val title: String
        )
    }

    @Serializable
    data class DataType(
        @SerialName("ID")
        val iD: Int,
        @SerialName("Title")
        val title: String
    )

    @Serializable
    data class MetadataGroup(
        @SerialName("DataProviderID")
        val dataProviderID: Int,
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsPublicInterest")
        val isPublicInterest: Boolean,
        @SerialName("IsRestrictedEdit")
        val isRestrictedEdit: Boolean,
        @SerialName("MetadataFields")
        val metadataFields: List<MetadataField>,
        @SerialName("Title")
        val title: String
    ) {
        @Serializable
        data class MetadataField(
            @SerialName("DataType")
            val dataType: String,
            @SerialName("DataTypeID")
            val dataTypeID: Int,
            @SerialName("ID")
            val iD: Int,
            @SerialName("MetadataFieldOptions")
            val metadataFieldOptions: List<MetadataFieldOption>,
            @SerialName("MetadataGroupID")
            val metadataGroupID: Int,
            @SerialName("Title")
            val title: String
        ) {
            @Serializable
            data class MetadataFieldOption(
                @SerialName("ID")
                val iD: Int,
                @SerialName("MetadataFieldID")
                val metadataFieldID: Int,
                @SerialName("Title")
                val title: String
            )
        }
    }

    @Serializable
    data class Operator(
        @SerialName("AddressInfo")
        val addressInfo: String,
        @SerialName("BookingURL")
        val bookingURL: String,
        @SerialName("Comments")
        val comments: String,
        @SerialName("ContactEmail")
        val contactEmail: String,
        @SerialName("FaultReportEmail")
        val faultReportEmail: String,
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsPrivateIndividual")
        val isPrivateIndividual: Boolean,
        @SerialName("IsRestrictedEdit")
        val isRestrictedEdit: Boolean,
        @SerialName("PhonePrimaryContact")
        val phonePrimaryContact: String,
        @SerialName("PhoneSecondaryContact")
        val phoneSecondaryContact: String,
        @SerialName("Title")
        val title: String,
        @SerialName("WebsiteURL")
        val websiteURL: String
    )

    @Serializable
    data class StatusType(
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsOperational")
        val isOperational: Boolean,
        @SerialName("IsUserSelectable")
        val isUserSelectable: Boolean,
        @SerialName("Title")
        val title: String
    )

    @Serializable
    data class SubmissionStatusType(
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsLive")
        val isLive: Boolean,
        @SerialName("Title")
        val title: String
    )

    @Serializable
    data class UsageType(
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsAccessKeyRequired")
        val isAccessKeyRequired: Boolean,
        @SerialName("IsMembershipRequired")
        val isMembershipRequired: Boolean,
        @SerialName("IsPayAtLocation")
        val isPayAtLocation: Boolean,
        @SerialName("Title")
        val title: String
    )

    @Serializable
    data class UserComment(
        @SerialName("ChargePointID")
        val chargePointID: Int,
        @SerialName("CheckinStatusType")
        val checkinStatusType: CheckinStatusType,
        @SerialName("CheckinStatusTypeID")
        val checkinStatusTypeID: String,
        @SerialName("Comment")
        val comment: String,
        @SerialName("CommentType")
        val commentType: CommentType,
        @SerialName("CommentTypeID")
        val commentTypeID: String,
        @SerialName("DateCreated")
        val dateCreated: String,
        @SerialName("ID")
        val iD: Int,
        @SerialName("IsActionedByEditor")
        val isActionedByEditor: Boolean,
        @SerialName("Rating")
        val rating: String,
        @SerialName("RelatedURL")
        val relatedURL: String,
        @SerialName("User")
        val user: String,
        @SerialName("UserName")
        val userName: String
    ) {
        @Serializable
        data class CheckinStatusType(
            @SerialName("ID")
            val iD: Int,
            @SerialName("IsAutomatedCheckin")
            val isAutomatedCheckin: Boolean,
            @SerialName("IsPositive")
            val isPositive: Boolean,
            @SerialName("Title")
            val title: String
        )

        @Serializable
        data class CommentType(
            @SerialName("ID")
            val iD: Int,
            @SerialName("Title")
            val title: String
        )
    }

    @Serializable
    data class UserCommentType(
        @SerialName("ID")
        val iD: Int,
        @SerialName("Title")
        val title: String
    )
}