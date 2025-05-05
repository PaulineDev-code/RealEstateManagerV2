package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    tableName = "point_of_interest_cross_ref",
    primaryKeys = ["propertyId", "pointOfInterestId"],
    foreignKeys = [
        ForeignKey(
            entity = PropertyLocalEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PointOfInterestEntity::class,
            parentColumns = ["id"],
            childColumns = ["pointOfInterestId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["propertyId"]),
        Index(value = ["pointOfInterestId"])
    ]
)
data class PointOfInterestCrossRef(
    val propertyId: String,
    val pointOfInterestId: String
)