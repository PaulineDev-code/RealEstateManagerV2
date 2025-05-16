package com.openclassrooms.realestatemanagerv2.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class PropertySearchCriteria (
    val propertyType: List<String>? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minArea: Double? = null,
    val maxArea: Double? = null,
    val minNumberOfRooms: Int? = null,
    val minPhotos: Int? = null,
    val minVideos: Int? = null,
    val nearbyPointsOfInterest: List<PointOfInterest>? = null,
    val minEntryDate: Long? = null,
    val minSaleDate: Long? = null,
    val agentId: String? = null
) :Parcelable {
    companion object {
        val Empty: PropertySearchCriteria = PropertySearchCriteria()
    }
}