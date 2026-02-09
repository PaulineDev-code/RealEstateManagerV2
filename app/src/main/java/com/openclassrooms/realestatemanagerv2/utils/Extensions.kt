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
import java.lang.Double.parseDouble
import java.text.NumberFormat
import java.util.UUID
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


//For better architecture, Entities should not have access to Model, so Model has companion objects
//to convert entities to model and extension functions to convert model to entities
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

fun Property.mapToPointOfInterestCrossRefs(): List<PointOfInterestCrossRef> {
    return nearbyPointsOfInterest.map { poi ->
        PointOfInterestCrossRef(
            propertyId = id,
            pointOfInterestId = poi.serialName
        )
    }
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

fun PointOfInterest.toSelectable(): SelectablePointOfInterest {
    return SelectablePointOfInterest(name = this.name, isSelected = false)
}

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
fun Double.convertToLocalCurrency(): String {
    //To use in screens convert from dollar to local currency
    val locale = Locale.getDefault()

    val convertedAmount = when (locale.country) {
        "FR" -> this * 0.91  // EUR (France)
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

    return convertedAmount.toString()

}

/**
* Converts a price from local currency back to USD.
*
* Note: This returns a Double (not a formatted String) because it's typically
* used to store values in the database, which expects raw USD amounts.
*/
fun Double.convertFromLocalCurrency(): String {
    //To use in AddScreen & SearchScreen convert from local currency to dollar
    val locale = Locale.getDefault()
    val toUSD = when (locale.country) {
        "FR" -> this / 0.91      // EUR → USD
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
    return toUSD.toString()
}



