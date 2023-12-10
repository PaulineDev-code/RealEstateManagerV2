package com.openclassrooms.realestatemanagerv2.domain.model

data class Property (
    val id: String,
    val type: String,
    val price: Double,
    val area: Double,
    val numberOfRooms: Int,
    val description: String,
    val photos: List<Photo>,
    val videoUrl: String?,
    val address: String,
    val nearbyPointsOfInterest: List<String>,
    val status: PropertyStatus,
    val entryDate: String,
    val saleDate: String?,
    val agent: Agent
)

data class Photo(
    val imageUrl: String,
    val description: String
)

data class Agent(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String
)

sealed class PropertyStatus {
    object Available : PropertyStatus()
    object Sold : PropertyStatus()
}
