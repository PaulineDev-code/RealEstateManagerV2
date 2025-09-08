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
import java.text.NumberFormat
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
        latitude = latitude,
        longitude = longitude,
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
        else -> error("Unknown Media type: $this")
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
        id = this.serialName,
        displayNameResId = this.displayNameResId
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
        else -> error("Unknown PropertyStatus: $this")
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
    return if (this.length < 5) "Title is too short." else ""
 }

fun String.validateNonEmpty(): String? {
    return if (this.isBlank()) "Can't be empty." else ""
}

fun String.validatePositiveNumber(): String? {
    if (this.isBlank()) return ""
    val number = this.toDoubleOrNull()
    return when {
        number == null -> "Must be a valid number."
        number <= 0 -> "Must be a positive number."
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

fun String.formatToLocalCurrency(): String {
    val locale = Locale.getDefault()

    val formatter = NumberFormat.getCurrencyInstance(locale)

    return formatter.format(this)
}

fun Double.convertToLocalCurrency(): String {
    //To use in AddScreen & SearchScreen convert from dollar to local currency
    val locale = Locale.getDefault()

    val formatter = NumberFormat.getCurrencyInstance(locale)

    when (locale) {
        Locale.FRANCE -> return formatter.format(this * 0.91)
        Locale.UK -> return formatter.format(this * 0.78)
        Locale.JAPAN -> return formatter.format(this * 145.30)
        Locale.forLanguageTag("de-CH") -> return formatter.format(this * 0.90) // Suisse (Alémanique)
        Locale.CANADA -> return formatter.format(this * 1.35)
        Locale.forLanguageTag("en-AU")  -> return formatter.format(this * 1.52)   // Australie
        Locale.CHINA -> return formatter.format(this * 7.19)
        Locale.forLanguageTag("sv-SE") -> return formatter.format(this * 11.0)  // Suède
        Locale.forLanguageTag("ru-RU") -> return formatter.format(this * 92.3)   // Russie
        Locale.forLanguageTag("en-IN") -> return formatter.format(this * 83.1)   // Inde
        Locale.forLanguageTag("pt-BR") -> return formatter.format(this * 4.95)   // Brésil
        Locale.forLanguageTag("es-MX") -> return formatter.format(this * 17.3)    // Mexique
        else -> return NumberFormat.getCurrencyInstance(Locale.US).format(this)
    }

}

fun Double.convertFromLocalCurrency(): String {
    //To use in AddScreen & SearchScreen convert from local currency to dollar
    val locale = Locale.getDefault()
    val formatter = NumberFormat.getCurrencyInstance(locale)

    when (locale) {
        Locale.FRANCE -> return formatter.format(this / 0.91)
        Locale.UK -> return formatter.format(this / 0.78)
        Locale.JAPAN -> return formatter.format(this / 145.30)
        Locale.forLanguageTag("de-CH") -> return formatter.format(this / 0.90) // Suisse (Alémanique)
        Locale.CANADA -> return formatter.format(this / 1.35)
        Locale.forLanguageTag("en-AU")  -> return formatter.format(this / 1.52)   // Australie
        Locale.CHINA -> return formatter.format(this / 7.19)
        Locale.forLanguageTag("sv-SE") -> return formatter.format(this / 11.0)  // Suède
        Locale.forLanguageTag("ru-RU") -> return formatter.format(this / 92.3)   // Russie
        Locale.forLanguageTag("en-IN") -> return formatter.format(this / 83.1)   // Inde
        Locale.forLanguageTag("pt-BR") -> return formatter.format(this / 4.95)   // Brésil
        Locale.forLanguageTag("es-MX") -> return formatter.format(this / 17.3)    // Mexique
        else -> return NumberFormat.getCurrencyInstance(Locale.US).format(this)
    }
}



