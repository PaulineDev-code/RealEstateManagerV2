package com.openclassrooms.realestatemanagerv2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PropertyWithDetails

@Dao
interface AgentDAO{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: AgentEntity)

    @Update
    suspend fun updateAgent(agent: AgentEntity)

    @Query("SELECT * FROM agents")
    suspend fun getAllAgents(): List<AgentEntity>

    @Query("SELECT * FROM agents WHERE id = :agentId")
    suspend fun getAgentById(agentId: Long): AgentEntity?

}