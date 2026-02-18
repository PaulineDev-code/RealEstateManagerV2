package com.openclassrooms.realestatemanagerv2.domain.repositories

import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.domain.model.Agent

interface AgentRepository {
    suspend fun insertAgent(agent: Agent): Unit
    suspend fun updateAgent(agent: Agent): Unit
    suspend fun getAllAgents(): List<Agent>
}