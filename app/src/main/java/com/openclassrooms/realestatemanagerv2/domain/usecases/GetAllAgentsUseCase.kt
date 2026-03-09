package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.repositories.AgentRepository
import javax.inject.Inject

/**
 * Use case responsible for retrieving the complete list of real estate agents.
 *
 * This is primarily used to populate selection components (Spinners/Dropdowns)
 * in property creation, edition, and search filter screens.
 */
interface GetAllAgentsUseCase {

    /**
     * Executes the retrieval process for all registered agents.
     *
     * @return A list of [Agent] domain models.
     */
    suspend operator fun invoke(): List<Agent>
}

/**
 * Standard implementation of [GetAllAgentsUseCase].
 *
 * It delegates the data fetching to the [AgentRepository], which handles
 * communication with the underlying data source.
 *
 * @property agentRepository The repository used to access agent data.
 */
class GetAllAgentsUseCaseImpl @Inject constructor(
    private val agentRepository: AgentRepository
) : GetAllAgentsUseCase {
    override suspend operator fun invoke(): List<Agent> = agentRepository.getAllAgents()
}
