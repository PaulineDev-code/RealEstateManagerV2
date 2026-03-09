package com.openclassrooms.realestatemanagerv2.domain.model

import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.MediaEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyWithDetails
import com.openclassrooms.realestatemanagerv2.utils.toPropertyStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Core domain model representing a real estate property.
 *
 * This entity is the single source of truth for property data across the application.
 * It is used by the Domain layer for business logic and the UI layer for display.
 *
 * @property id Unique identifier (UUID) for the property.
 * @property type Category of the property (e.g., Manor, House, Flat).
 * @property price Cost of the property in the base currency.
 * @property area Total surface area in square meters.
 * @property numberOfRooms Total number of rooms.
 * @property description Detailed text description of the property.
 * @property media List of [Media] items (Photos or Videos) associated with the property.
 * @property address Full physical address string.
 * @property latitude Geographic latitude for map positioning.
 * @property longitude Geographic longitude for map positioning.
 * @property nearbyPointsOfInterest List of local amenities ([PointOfInterest]) close to the property.
 * @property status Current market status ([PropertyStatus.Available] or [PropertyStatus.Sold]).
 * @property entryDate Timestamp (millis) when the property was added to the market.
 * @property saleDate Optional timestamp (millis) when the property was sold.
 * @property agent The [Agent] assigned to manage this property.
 */
data class Property(
    val id: String,
    val type: String,
    val price: Double,
    val area: Double,
    val numberOfRooms: Int,
    val description: String,
    val media: List<Media>,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val nearbyPointsOfInterest: List<PointOfInterest>,
    val status: PropertyStatus,
    val entryDate: Long,
    val saleDate: Long?,
    val agent: Agent
) {
    companion object {
        /**
         * Factory method to map a Data layer [PropertyWithDetails] entity to a Domain [Property] model.
         */
        //All model to entities conversions are in the 'Extensions.kt' file in the package 'utils'
        fun fromPropertyWithDetails(propertyWithDetails: PropertyWithDetails): Property {

            return Property(
                id = propertyWithDetails.property.id,
                type = propertyWithDetails.property.type,
                price = propertyWithDetails.property.price,
                area = propertyWithDetails.property.area,
                numberOfRooms = propertyWithDetails.property.numberOfRooms,
                description = propertyWithDetails.property.description,
                media = propertyWithDetails.media.map { Media.fromMediaEntity(it) },
                address = propertyWithDetails.property.address,
                latitude = propertyWithDetails.property.latitude,
                longitude = propertyWithDetails.property.longitude,
                nearbyPointsOfInterest = propertyWithDetails.pointsOfInterest.map {
                    PointOfInterest.fromPointOfInterestEntity(it)
                },
                status = propertyWithDetails.property.status.toPropertyStatus(),
                entryDate = propertyWithDetails.property.entryDate,
                saleDate = propertyWithDetails.property.saleDate,
                agent = Agent.fromAgentEntity(propertyWithDetails.agent)
            )
        }
    }
}

/**
 * Sealed class representing media files associated with a property.
 * Can be either a [Photo] or a [Video].
 */
sealed class Media {
    abstract val mediaUrl: String
    abstract val description: String

    companion object {

        /**
         * Maps a database [MediaEntity] to its corresponding domain [Media] type.
         */
        fun fromMediaEntity(mediaEntity: MediaEntity): Media {
            return when (mediaEntity.type) {
                "photo" -> Photo(
                    mediaUrl = mediaEntity.mediaUrl,
                    description = mediaEntity.description
                )

                "video" -> Video(
                    mediaUrl = mediaEntity.mediaUrl,
                    description = mediaEntity.description
                )

                else -> throw IllegalArgumentException("Type de média inconnu : ${mediaEntity.type}")
            }
        }
    }
}

/**
 * Domain model for a photo file.
 */
data class Photo(
    override val mediaUrl: String,
    override val description: String
) : Media()

/**
 * Domain model for a video file.
 */
data class Video(
    override val mediaUrl: String,
    override val description: String
) : Media()


/**
 * Enum defining the various types of amenities or Points of Interest.
 * Each entry is mapped to a localized string resource.
 */
@Serializable
enum class PointOfInterest(val serialName: String, val displayNameResId: Int) {
    @SerialName("NURSERY_SCHOOL")
    NURSERY_SCHOOL("NURSERY_SCHOOL", R.string.nursery_school),

    @SerialName("PRIMARY_SCHOOL")
    PRIMARY_SCHOOL("PRIMARY_SCHOOL", R.string.primary_school),

    @SerialName("MIDDLE_SCHOOL")
    MIDDLE_SCHOOL("MIDDLE_SCHOOL", R.string.middle_school),

    @SerialName("HIGH_SCHOOL")
    HIGH_SCHOOL("HIGH_SCHOOL", R.string.high_school),

    @SerialName("TRAIN_STATION")
    TRAIN_STATION("TRAIN_STATION", R.string.train_station),

    @SerialName("BUS_STATION")
    BUS_STATION("BUS_STATION", R.string.bus_station),

    @SerialName("AIRPORT")
    AIRPORT("AIRPORT", R.string.airport),

    @SerialName("HOSPITAL")
    HOSPITAL("HOSPITAL", R.string.hospital),

    @SerialName("PHARMACY")
    PHARMACY("PHARMACY", R.string.pharmacy),

    @SerialName("SUPERMARKET")
    SUPERMARKET("SUPERMARKET", R.string.supermarket),

    @SerialName("POST_OFFICE")
    POST_OFFICE("POST_OFFICE", R.string.post_office),

    @SerialName("GAS_STATION")
    GAS_STATION("GAS_STATION", R.string.gas_station),

    @SerialName("PARK")
    PARK("PARK", R.string.park),

    @SerialName("CINEMA")
    CINEMA("CINEMA", R.string.cinema),

    @SerialName("GYM_CLUB")
    GYM_CLUB("GYM_CLUB", R.string.gym_club),

    @SerialName("SWIMMING_POOL")
    SWIMMING_POOL("SWIMMING_POOL", R.string.swimming_pool),

    @SerialName("RESTAURANT")
    RESTAURANT("RESTAURANT", R.string.restaurant),

    @SerialName("BAR")
    BAR("BAR", R.string.bar),

    @SerialName("BAKERY")
    BAKERY("BAKERY", R.string.bakery),

    @SerialName("POLICE_STATION")
    POLICE_STATION("POLICE_STATION", R.string.police_station),

    @SerialName("CITY_HALL")
    CITY_HALL("CITY_HALL", R.string.city_hall),

    @SerialName("LIBRARY")
    LIBRARY("LIBRARY", R.string.library),

    @SerialName("MUSEUM")
    MUSEUM("MUSEUM", R.string.museum);


    companion object {

        fun fromSerializedName(serializedName: String): PointOfInterest? {
            return entries.find { it.serialName == serializedName }
        }

        fun fromPointOfInterestEntity(entity: PointOfInterestEntity): PointOfInterest {
            return entries.find { it.displayNameResId == entity.displayNameResId }
                ?: throw IllegalArgumentException("PointOfInterest from entity unknown : ${entity.displayNameResId}")
        }

    }
}


/**
 * Domain model for a real estate agent.
 */
data class Agent(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String
) {
    companion object {
        fun fromAgentEntity(agentEntity: AgentEntity): Agent {
            return Agent(
                id = agentEntity.id,
                name = agentEntity.name,
                phoneNumber = agentEntity.phoneNumber,
                email = agentEntity.email
            )
        }
    }
}

/**
 * Represents the lifecycle status of a property listing.
 */
sealed class PropertyStatus {

    /** The property is currently listed and available for purchase. */
    object Available : PropertyStatus()

    /** The property has been sold and is no longer available. */
    object Sold : PropertyStatus()
}