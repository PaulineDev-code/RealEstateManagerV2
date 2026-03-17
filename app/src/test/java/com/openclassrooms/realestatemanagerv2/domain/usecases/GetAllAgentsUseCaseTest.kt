package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.repositories.AgentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals

class GetAllAgentsUseCaseTest {

    private lateinit var agentRepository: AgentRepository
    private lateinit var useCase: GetAllAgentsUseCaseImpl

    private val fakeAgents = listOf(
        Agent("agent-1", "John Doe", "555-0100", "john@example.com"),
        Agent("agent-2", "Jane Doe", "555-0101", "jane@example.com")
    )

    @Before
    fun setUp() {
        agentRepository = mock()
        useCase = GetAllAgentsUseCaseImpl(agentRepository)
    }

    @Test
    fun invoke_returnsAgentsFromRepository() = runTest {
        whenever(agentRepository.getAllAgents()).thenReturn(fakeAgents)

        val result = useCase()

        assertEquals(fakeAgents, result)
    }
}
