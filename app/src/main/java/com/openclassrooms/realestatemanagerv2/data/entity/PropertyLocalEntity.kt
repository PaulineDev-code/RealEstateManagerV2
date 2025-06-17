package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "properties", foreignKeys =
[ ForeignKey(entity = AgentEntity::class, parentColumns = ["id"], childColumns = ["agentId"])])
data class PropertyLocalEntity (
    @PrimaryKey
    @ColumnInfo
    val id: String,
    @ColumnInfo
    val type: String,
    @ColumnInfo
    val price: Double,
    @ColumnInfo
    val area: Double,
    @ColumnInfo
    val numberOfRooms: Int,
    @ColumnInfo
    val description: String,
    @ColumnInfo
    val address: String,
    @ColumnInfo
    val latitude: Double?,
    @ColumnInfo
    val longitude: Double?,
    @ColumnInfo
    val status: String,
    @ColumnInfo
    val entryDate: Long,
    @ColumnInfo
    val saleDate: Long?,
    @ColumnInfo(index = true)
    val agentId: String
)