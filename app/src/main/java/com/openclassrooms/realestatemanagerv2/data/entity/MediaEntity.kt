package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "medias",
        foreignKeys = [
        ForeignKey(entity = PropertyLocalEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyLocalId"],
            onDelete = CASCADE)])


data class MediaEntity (
    @PrimaryKey
    @ColumnInfo
    val id: String,
    val type: String,
    @ColumnInfo
    val description: String,
    @ColumnInfo
    val mediaUrl: String,
    @ColumnInfo(index = true)
    val propertyLocalId: String
)