package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "points_of_interest",
    foreignKeys = [
        ForeignKey(entity = PropertyLocalEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyLocalId"],
            onDelete = ForeignKey.CASCADE
        )])


data class PointOfInterestEntity (
    @PrimaryKey val id: String,
    val pointOfInterest: String,
    @ColumnInfo(index = true)
    val propertyLocalId: String
)