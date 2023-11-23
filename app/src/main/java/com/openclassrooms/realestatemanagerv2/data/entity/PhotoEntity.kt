package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "photos",
        foreignKeys = [
        ForeignKey(entity = PropertyLocalEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyLocalId"],
            onDelete = CASCADE)])


data class PhotoEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val description: String,
    val photoUrl: String,
    @ColumnInfo(index = true)
    val propertyLocalId: Long
)