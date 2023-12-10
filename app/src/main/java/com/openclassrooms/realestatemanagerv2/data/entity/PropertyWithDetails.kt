package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PropertyWithDetails(
    @Embedded val property: PropertyLocalEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "propertyLocalId"
    )
    val nearByPointsOfInterest: List<PointOfInterestEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "propertyLocalId"
    )
    val photos: List<PhotoEntity>,

    @Relation(
        parentColumn = "agentId",
        entityColumn = "id"
    )
    val agent: AgentEntity
)