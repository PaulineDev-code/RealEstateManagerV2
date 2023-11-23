package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "properties", foreignKeys =
[ ForeignKey(entity = AgentEntity::class, parentColumns = ["id"], childColumns = ["agentId"])])
data class PropertyLocalEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val type: String,
    val price: Double,
    val area: Double,
    val numberOfRooms: Int,
    val description: String,
    val videoUrl: String?,
    val address: String,
    val status: String,
    val entryDate: String,
    val saleDate: String?,
    @ColumnInfo(index = true)
    val agentId: Long
)