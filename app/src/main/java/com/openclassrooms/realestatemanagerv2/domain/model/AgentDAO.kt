package com.openclassrooms.realestatemanagerv2.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity

@Dao
interface AgentDAO{
    @Insert
    suspend fun insertAgent(agent: AgentEntity)

    @Update
    suspend fun updateAgent(agent: AgentEntity)

    @Query("SELECT * FROM agents WHERE id = :agentId")
    suspend fun getAgentById(agentId: Long): AgentEntity?

}