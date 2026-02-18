package com.openclassrooms.realestatemanagerv2.repositories

import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.repositories.AgentRepository
import com.openclassrooms.realestatemanagerv2.utils.toAgentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AgentRepositoryImpl @Inject constructor(
    private val agentDao: AgentDAO
) : AgentRepository {

    override suspend fun insertAgent(agent: Agent): Unit {
        val agentEntity = agent.toAgentEntity()
        agentDao.insertAgent(agentEntity)
    }

    override suspend fun updateAgent(agent: Agent): Unit {
        val agentEntity = agent.toAgentEntity()
        agentDao.updateAgent(agentEntity)
    }

    override suspend fun getAllAgents(): List<Agent> = withContext(Dispatchers.IO){
        val agentEntities = agentDao.getAllAgents()
        agentEntities.map { agentEntity ->
            Agent.fromAgentEntity(agentEntity)
        }
    }
}