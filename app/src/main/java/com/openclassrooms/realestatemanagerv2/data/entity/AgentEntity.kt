package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "agents")
data class AgentEntity(
    @PrimaryKey
    @ColumnInfo
    val id: String,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val phoneNumber: String,
    @ColumnInfo
    val email: String
)