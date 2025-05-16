package com.openclassrooms.realestatemanagerv2.utils

import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.MediaEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyLocalEntity
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Video
import com.openclassrooms.realestatemanagerv2.ui.models.SelectablePointOfInterest
import java.util.UUID
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


//For better architecture, Entities should not have access to Model, so Model has companion objects
//to convert entities to model
//Features Model to entity, String to property status and points of interest to selectable conversions
// + field validations for properties
fun List<Property>.mapToPropertyLocalEntities(): List<PropertyLocalEntity> {
    return map { it.toPropertyLocalEntity() }
}

fun Property.toPropertyLocalEntity(): PropertyLocalEntity {
    return PropertyLocalEntity(
        id = id,
        type = type,
        price = price,
        area = area,
        numberOfRooms = numberOfRooms,
        description = description,
        address = address,
        status = status.toReadableString(),
        entryDate = entryDate,
        saleDate = saleDate,
        agentId = agent.id
    )
}

fun Property.mapToMediaEntities(): List<MediaEntity> {
    return media.map { it.toMediaEntity(id) }
}

private fun Media.toMediaEntity(propertyLocalId: String): MediaEntity {
    val type = when (this) {
        is Photo -> "photo"
        is Video -> "video"
    }
    return MediaEntity(
        id = UUID.randomUUID().toString(),
        type = type,
        mediaUrl = mediaUrl,
        description = description,
        propertyLocalId = propertyLocalId,
    )
}

fun Property.mapToPointOfInterestEntities(): List<PointOfInterestEntity> {
    return nearbyPointsOfInterest.map { it.toPointOfInterestEntity() }
}

private fun PointOfInterest.toPointOfInterestEntity(): PointOfInterestEntity {
    return PointOfInterestEntity(
        id = UUID.randomUUID().toString(),
        name = this.name
    )
}

fun Agent.toAgentEntity(): AgentEntity {
    return AgentEntity(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        email = email
    )
}

//CONVERT PROPERTY STATUS TO STRING OR STRING TO PROPERTY STATUS
fun PropertyStatus.toReadableString(): String {
    return when (this) {
        is PropertyStatus.Available -> "Available"
        is PropertyStatus.Sold -> "Sold"
    }
}

fun String.toPropertyStatus(): PropertyStatus {
    return when (this) {
        "Available" -> PropertyStatus.Available
        "Sold" -> PropertyStatus.Sold
        else -> throw IllegalArgumentException("Unknown PropertyStatus: $this")
    }
}

//
fun PointOfInterest.toSelectable(): SelectablePointOfInterest {
    return SelectablePointOfInterest(name = this.name, isSelected = false)
}

/*fun SelectablePointOfInterest.toPointOfInterest(): PointOfInterest {
    return SelectablePointOfInterest(name = this.name, isSelected = false)
}*/

fun List<PointOfInterest>.toSelectableList(): List<SelectablePointOfInterest> {
    return this.map { it.toSelectable() }
}

//validation functions
fun String.validateLength(): String? {
    return if (this.length < 5) "Le titre est trop court." else ""
 }

fun String.validateNonEmpty(): String? {
    return if (this.isBlank()) "Ne peut pas être vide." else ""
}

fun String.validatePositiveNumber(): String? {
    val number = this.toDoubleOrNull()
    return when {
        number == null -> "Doit être un nombre valide."
        number <= 0 -> "Doit être supérieur à 0."
        else -> ""
    }
}

/**
 * Formate un [Long] (millis depuis l’Epoch) en date locale lisible.
 *
 * @receiver le timestamp à formater (ou null pour « aucune date »)
 * @param pattern le pattern JavaTime, par défaut "dd MMMM yyyy" (ex. "07 mars 2025")
 * @param locale la locale à utiliser (défaut : Locale.getDefault())
 * @return la chaîne formatée, ou chaîne vide si receiver est null
 */
fun Long?.formatMillisToLocal(
    pattern: String = "dd MMMM yyyy",
    locale: Locale = Locale.getDefault()
): String = this
    ?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
    ?.format(DateTimeFormatter.ofPattern(pattern, locale))
    .orEmpty()




