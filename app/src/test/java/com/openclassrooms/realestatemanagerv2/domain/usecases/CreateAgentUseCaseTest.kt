package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.repositories.AgentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CreateAgentUseCaseTest {

    private lateinit var agentRepository: AgentRepository
    private lateinit var useCase: CreateAgentUseCaseImpl

    private val testAgent = Agent(
        id = "agent-1",
        name = "John Doe",
        phoneNumber = "555-0100",
        email = "john@example.com"
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        agentRepository = mock()
        useCase = CreateAgentUseCaseImpl(agentRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun invoke_delegatesToRepository(): TestResult = runTest {
        useCase(testAgent)

        verify(agentRepository).insertAgent(agent = testAgent)
    }
}
