package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.repositories.AgentRepository
import com.openclassrooms.realestatemanagerv2.utils.toAgentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface CreateAgentUseCase {
    suspend operator fun invoke(agent: Agent): Unit
}

class CreateAgentUseCaseImpl @Inject constructor(
    private val agentRepository: AgentRepository
) : CreateAgentUseCase {

    override suspend operator fun invoke(agent: Agent): Unit = withContext(Dispatchers.IO) {
        val agentEntity = agent.toAgentEntity()
        agentRepository.insertAgent(agent = agentEntity)
    }
}