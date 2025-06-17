package com.openclassrooms.realestatemanagerv2.repositories

import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AgentRepository @Inject constructor(private val agentDao: AgentDAO) {

    suspend fun insertAgent(agent: AgentEntity): Unit = agentDao.insertAgent(agent)

    suspend fun updateAgent(agent: AgentEntity): Unit = agentDao.updateAgent(agent)

    suspend fun getAllAgents(): List<AgentEntity> = withContext(Dispatchers.IO){
        agentDao.getAllAgents()
    }
}