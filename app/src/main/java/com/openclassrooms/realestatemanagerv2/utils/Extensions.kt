package com.openclassrooms.realestatemanagerv2.utils

import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.MediaEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PointOfInterestCrossRef
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.UUID


/**
 * Utility extensions for mapping, validation, and formatting.
 *
 * ARCHITECTURE NOTE:
 * To maintain a strict separation of concerns, Data Entities do not have access
 * to Domain Models. Mapping from Entities to Models is handled via companion
 * object factories in the Model classes, while mapping from Models to Entities
 * is handled here via extension functions.
 */

/**
 * Mappers: Domain Models to Data Entities
 */

/**
 * Converts a list of [Property] domain models to [PropertyLocalEntity] for database storage.
 */
fun List<Property>.mapToPropertyLocalEntities(): List<PropertyLocalEntity> {
    return map { it.toPropertyLocalEntity() }
}

/**
 * Converts a [Property] domain model to its database entity representation.
 */
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

/**
 * Maps property media to a list of [MediaEntity] using the property ID as a foreign key.
 */
fun Property.mapToMediaEntities(): List<MediaEntity> {
    return media.map { it.toMediaEntity(id) }
}

/**
 * Internal mapper for individual Media items.
 */
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

/**
 * Maps nearby amenities to a list of CrossReference entities for the many-to-many relationship.
 */
fun Property.mapToPointOfInterestCrossRefs(): List<PointOfInterestCrossRef> {
    return nearbyPointsOfInterest.map { poi ->
        PointOfInterestCrossRef(
            propertyId = id,
            pointOfInterestId = poi.serialName
        )
    }
}

/**
 * Enum & Status Converters
 */

/**
 * Converts an [Agent] domain model to an [AgentEntity].
 */
fun Agent.toAgentEntity(): AgentEntity {
    return AgentEntity(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        email = email
    )
}

/**
 * Converts [PropertyStatus] to a string representation for database storage.
 */
fun PropertyStatus.toReadableString(): String {
    return when (this) {
        is PropertyStatus.Available -> "Available"
        is PropertyStatus.Sold -> "Sold"
        else -> error("Unknown PropertyStatus: $this")
    }
}

/**
 * Converts a string from the database back into a [PropertyStatus] domain model.
 */
fun String.toPropertyStatus(): PropertyStatus {
    return when (this) {
        "Available" -> PropertyStatus.Available
        "Sold" -> PropertyStatus.Sold
        else -> throw IllegalArgumentException("Unknown PropertyStatus: $this")
    }
}

fun PointOfInterest.toSelectable(): SelectablePointOfInterest {
    return SelectablePointOfInterest(name = this.name, isSelected = false)
}

fun List<PointOfInterest>.toSelectableList(): List<SelectablePointOfInterest> {
    return this.map { it.toSelectable() }
}

/**
 * Form & Validation Functions
 */

/**
 * Validates that a string has a minimum length.
 * @return An error message if invalid, or an empty string if valid.
 */
fun String.validateLength(): String? {
    return if (this.length < 5) "Is too short." else ""
 }

/**
 * Validates that a string is not blank.
 */
fun String.validateNonEmpty(): String? {
    return if (this.isBlank()) "Can't be empty." else ""
}


/**
 * Validates that a string represents a positive numerical value.
 */
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
 * Formatting & Currency Functions
 */

/**
 * Formats a [Long] timestamp (millis) into a localized date string.
 *
 * @param dateStyle The visual style of the date (SHORT, MEDIUM, LONG, FULL).
 * @param locale The user's locale (defaults to system locale).
 */
fun Long?.formatMillisToLocal(
    dateStyle: FormatStyle = FormatStyle.MEDIUM,
    locale: Locale = Locale.getDefault()
): String = this
    ?.let {
        val localDate = Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val formatter = DateTimeFormatter
            .ofLocalizedDate(dateStyle)
            .withLocale(locale)

        localDate.format(formatter)
    }
    .orEmpty()

/**
 * Formats a numeric string into the local currency format (e.g., adds $ or €).
 */
fun String.formatToLocalCurrency(): String {
    val locale = Locale.getDefault()

    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(this.toDoubleOrNull() ?:  return this)
}

/**
* Formats a String representing a USD price into the user's locale currency format.
* Note: This converts from USD to local currency based on hardcoded exchange rates.
*
* WARNING: Exchange rates are static and may become outdated.
* For production, consider using a real-time exchange rate API.
*
* @receiver String representing a price in USD (e.g., "300000")
* @return Formatted string in local currency (e.g., "273 000 €" for France)
*/
fun Double.convertToLocalCurrency(): Double {
    val locale = Locale.getDefault()

    val convertedAmount = when (locale.country) {
        "FR", "ES", "DE", "IT" -> this * 0.91  // EUR (France)
        "GB" -> this * 0.78  // GBP (United Kingdom)
        "JP" -> this * 145.30  // JPY (Japan)
        "CH" -> this * 0.90  // CHF (Switzerland)
        "CA" -> this * 1.35  // CAD (Canada)
        "AU" -> this * 1.52  // AUD (Australia)
        "CN" -> this * 7.19  // CNY (China)
        "SE" -> this * 11.0  // SEK (Sweden)
        "RU" -> this * 92.3  // RUB (Russia)
        "IN" -> this * 83.1  // INR (India)
        "BR" -> this * 4.95  // BRL (Brazil)
        "MX" -> this * 17.3  // MXN (Mexico)
        else -> this  // Default: keep USD
    }
    return convertedAmount
}

/**
 * Converts a price from the user's local currency back to the base USD for storage.
 */
fun Double.convertFromLocalCurrency(): Double {
    val locale = Locale.getDefault()
    val toUSD = when (locale.country) {
        "FR", "ES", "DE", "IT" -> this / 0.91      // EUR → USD
        "GB" -> this / 0.78      // GBP → USD
        "JP" -> this / 145.30    // JPY → USD
        "CH" -> this / 0.90      // CHF → USD
        "CA" -> this / 1.35      // CAD → USD
        "AU" -> this / 1.52      // AUD → USD
        "CN" -> this / 7.19      // CNY → USD
        "SE" -> this / 11.0      // SEK → USD
        "RU" -> this / 92.3      // RUB → USD
        "IN" -> this / 83.1      // INR → USD
        "BR" -> this / 4.95      // BRL → USD
        "MX" -> this / 17.3      // MXN → USD
        else -> this             // if already USD or unknown
    }
    return toUSD
}



