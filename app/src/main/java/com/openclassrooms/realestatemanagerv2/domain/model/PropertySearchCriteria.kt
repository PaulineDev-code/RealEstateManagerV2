package com.openclassrooms.realestatemanagerv2.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data Transfer Object (DTO) used to encapsulate multiple search filters.
 *
 * This class aggregates all optional search parameters from the UI to be
 * processed by the Domain and Data layers. It implements [Parcelable]
 * to allow safe transportation between screens via Navigation or SavedStateHandle.
 *
 * @property propertyType Optional list of property categories (e.g., Manor, Flat).
 * @property minPrice Minimum price threshold for the search.
 * @property maxPrice Maximum price threshold for the search.
 * @property minArea Minimum surface area in square meters.
 * @property maxArea Maximum surface area in square meters.
 * @property minNumberOfRooms Minimum number of rooms required.
 * @property maxNumberOfRooms Maximum number of rooms allowed.
 * @property minPhotos Minimum number of photos the property must have.
 * @property minVideos Minimum number of videos the property must have.
 * @property nearbyPointsOfInterest List of local amenities that must be nearby.
 * @property minEntryDate The earliest listing date to consider.
 * @property minSaleDate The earliest sale date to consider (for sold properties).
 * @property agentId Unique identifier of the real estate agent responsible for the listing.
 */
@Parcelize
data class PropertySearchCriteria (
    val propertyType: List<String>? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minArea: Double? = null,
    val maxArea: Double? = null,
    val minNumberOfRooms: Int? = null,
    val maxNumberOfRooms: Int? = null,
    val minPhotos: Int? = null,
    val minVideos: Int? = null,
    val nearbyPointsOfInterest: List<PointOfInterest>? = null,
    val minEntryDate: Long? = null,
    val minSaleDate: Long? = null,
    val agentId: String? = null
) :Parcelable {
    companion object {
        /**
         * Represents a default state with no filters applied.
         */
        val Empty: PropertySearchCriteria = PropertySearchCriteria()
    }
}