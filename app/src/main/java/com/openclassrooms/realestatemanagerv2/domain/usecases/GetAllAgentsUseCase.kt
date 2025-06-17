package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.repositories.AgentRepository
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAllAgentsUseCase @Inject constructor(private val agentRepository: AgentRepository) {
    suspend operator fun invoke(): List<Agent> =
        agentRepository.getAllAgents().map { agentEntity ->
            Agent.fromAgentEntity(agentEntity)
    }
}
