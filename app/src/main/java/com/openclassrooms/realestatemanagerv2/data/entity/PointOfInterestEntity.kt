package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points_of_interest")

data class PointOfInterestEntity (
    @PrimaryKey /*(autoGenerate = true)*/
    @ColumnInfo
    val id: String = "",
    @ColumnInfo
    val displayNameResId: Int,
)