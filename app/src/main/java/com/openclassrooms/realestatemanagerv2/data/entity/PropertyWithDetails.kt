package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PropertyWithDetails(
    @Embedded val property: PropertyLocalEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PointOfInterestCrossRef::class,
            parentColumn = "propertyId",
            entityColumn = "pointOfInterestId"
        )
    )
    val pointsOfInterest: List<PointOfInterestEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "propertyLocalId"
    )
    val media: List<MediaEntity>,

    @Relation(
        parentColumn = "agentId",
        entityColumn = "id"
    )
    val agent: AgentEntity
)