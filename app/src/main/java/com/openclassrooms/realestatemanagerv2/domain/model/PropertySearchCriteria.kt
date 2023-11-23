package com.openclassrooms.realestatemanagerv2.domain.model

import java.util.Date

data class PropertySearchCriteria (
    val propertyType: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minArea: Double? = null,
    val maxArea: Double? = null,
    val minNumberOfRooms: Int? = null,
    val minPhotos: Int? = null,
    val videoUrl: String? = null,
    val nearbyPointsOfInterest: List<String>? = null,
    val status: PropertyStatus? = null,
    val minEntryDate: Date? = null,
    val maxEntryDate: Date? = null,
    val minSaleDate: Date? = null,
    val maxSaleDate: Date? = null,
    val agentId: String? = null
)