package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.repositories.AgentRepository
import javax.inject.Inject

interface GetAllAgentsUseCase {
    suspend operator fun invoke(): List<Agent>
}

class GetAllAgentsUseCaseImpl @Inject constructor(
    private val agentRepository: AgentRepository
) : GetAllAgentsUseCase {
    override suspend operator fun invoke(): List<Agent> = agentRepository.getAllAgents()
}
