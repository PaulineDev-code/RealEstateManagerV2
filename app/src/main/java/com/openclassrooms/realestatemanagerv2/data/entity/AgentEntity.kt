package com.openclassrooms.realestatemanagerv2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "agents")
data class AgentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String
)