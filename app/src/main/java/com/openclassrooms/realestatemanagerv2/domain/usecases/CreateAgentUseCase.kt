package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.repositories.AgentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case responsible for adding a new real estate agent to the system.
 *
 * This component handles the business logic for agent registration,
 * acting as an intermediary between the UI layer and the [AgentRepository].
 */
interface CreateAgentUseCase {

    /**
     * Executes the agent creation process.
     *
     * @param agent The [Agent] domain model representing the new agent to be registered.
     */
    suspend operator fun invoke(agent: Agent): Unit
}

/**
 * Standard implementation of [CreateAgentUseCase].
 *
 * It ensures the database insertion is performed on the [Dispatchers.IO] context
 * to maintain UI responsiveness.
 *
 * @property agentRepository The repository used to persist the agent data.
 */
class CreateAgentUseCaseImpl @Inject constructor(
    private val agentRepository: AgentRepository
) : CreateAgentUseCase {

    override suspend operator fun invoke(agent: Agent): Unit = withContext(Dispatchers.IO) {
        agentRepository.insertAgent(agent = agent)
    }
}