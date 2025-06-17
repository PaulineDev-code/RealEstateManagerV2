package com.openclassrooms.realestatemanagerv2.domain.model

import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.MediaEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyWithDetails
import com.openclassrooms.realestatemanagerv2.utils.toPropertyStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


data class Property (
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
        //All model to entities coversions are in the 'Extensions.kt' file in the package 'utils'
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
                    PointOfInterest.fromPointOfInterestEntity(it) },
                status = propertyWithDetails.property.status.toPropertyStatus(),
                entryDate = propertyWithDetails.property.entryDate,
                saleDate = propertyWithDetails.property.saleDate,
                agent = Agent.fromAgentEntity(propertyWithDetails.agent)
            )
        }
    }
}

sealed class Media {
    abstract val mediaUrl: String
    abstract val description: String


    //Plutôt faire une fonction d'extension qu'un companion object pour transformer les entités
    companion object {
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

// Sous-classe Photo
data class Photo(
    override val mediaUrl: String,
    override val description: String
) : Media()

// Sous-classe Video
data class Video(
    override val mediaUrl: String,
    override val description: String
) : Media()

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

        //display name from R.string , serializedName
        //fromSerializedName
        //fromValue

        /*fun fromPointOfInterestEntity(entity: PointOfInterestEntity): PointOfInterest {
            return PointOfInterest.valueOf(entity.name)
        }*/
        fun fromSerializedName(serializedName: String): PointOfInterest? {
            return values().find { it.serialName == serializedName }
        }

        fun fromPointOfInterestEntity(entity: PointOfInterestEntity): PointOfInterest {
            return PointOfInterest.values().find { it.displayNameResId == entity.displayNameResId } ?:
            throw IllegalArgumentException("PointOfInterest from entity unknown : ${entity.displayNameResId}")
        }

        }

    }



data class Agent(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String
) { companion object {
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

sealed class PropertyStatus {
    object Available : PropertyStatus()
    object Sold : PropertyStatus()
}
