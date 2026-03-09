package com.openclassrooms.realestatemanagerv2.domain.repositories

import com.openclassrooms.realestatemanagerv2.domain.model.Agent

/**
 * Repository interface that defines the abstraction layer for [Agent] data operations.
 *
 * This interface acts as a contract for the Domain layer to interact with the
 * underlying data sources (e.g., Room database) without being coupled to specific
 * implementation details.
 */
interface AgentRepository {

    /**
     * Persists a new real estate agent into the data source.
     *
     * @param agent The [Agent] domain model to be inserted.
     */
    suspend fun insertAgent(agent: Agent): Unit

    /**
     * Updates an existing real estate agent's information in the data source.
     *
     * @param agent The [Agent] domain model containing the updated information.
     */
    suspend fun updateAgent(agent: Agent): Unit

    /**
     * Retrieves the complete list of real estate agents from the data source.
     *
     * This is typically used to populate selection lists (Spinners) in the UI
     * for property creation or search filters.
     *
     * @return A list of all available [Agent] objects.
     */
    suspend fun getAllAgents(): List<Agent>
}