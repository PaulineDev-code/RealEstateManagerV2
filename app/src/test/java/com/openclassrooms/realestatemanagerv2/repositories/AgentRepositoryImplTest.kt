package com.openclassrooms.realestatemanagerv2.repositories

import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import kotlin.collections.emptyList

class AgentRepositoryImplTest {

    private lateinit var agentDao: AgentDAO
    private lateinit var repository: AgentRepositoryImpl

    private val fakeAgent = Agent(
        id = "agent-1",
        name = "John Doe",
        phoneNumber = "555-0100",
        email = "john@example.com"
    )

    private val fakeAgentEntity = AgentEntity(
        id = "agent-1",
        name = "John Doe",
        phoneNumber = "555-0100",
        email = "john@example.com"
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        agentDao = mock()
        repository = AgentRepositoryImpl(agentDao)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun insertAgent_delegatesToDaoWithCorrectEntity() = runTest {
        repository.insertAgent(fakeAgent)

        val captor = argumentCaptor<AgentEntity>()
        verify(agentDao).insertAgent(captor.capture())
        val captured = captor.firstValue
        assertEquals(fakeAgent.id, captured.id)
        assertEquals(fakeAgent.name, captured.name)
        assertEquals(fakeAgent.phoneNumber, captured.phoneNumber)
        assertEquals(fakeAgent.email, captured.email)
    }

    @Test
    fun updateAgent_delegatesToDaoWithCorrectEntity() = runTest {
        repository.updateAgent(fakeAgent)

        val captor = argumentCaptor<AgentEntity>()
        verify(agentDao).updateAgent(captor.capture())
        val captured = captor.firstValue
        assertEquals(fakeAgent.id, captured.id)
        assertEquals(fakeAgent.name, captured.name)
    }

    @Test
    fun getAllAgents_returnsMappedDomainModels() = runTest {
        val entities = listOf(
            fakeAgentEntity,
            AgentEntity("agent-2", "Jane Doe", "555-0101", "jane@example.com")
        )
        whenever(agentDao.getAllAgents()).thenReturn(entities)

        val result = repository.getAllAgents()

        assertEquals(2, result.size)
        assertEquals("agent-1", result[0].id)
        assertEquals("John Doe", result[0].name)
        assertEquals("agent-2", result[1].id)
        assertEquals("Jane Doe", result[1].name)
    }

    @Test
    fun getAllAgents_emptyList_returnsEmpty() = runTest {
        whenever(agentDao.getAllAgents()).thenReturn(emptyList())

        val result = repository.getAllAgents()

        assertEquals(emptyList<Agent>(), result)
    }
}
