package com.openclassrooms.realestatemanagerv2.repositories

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import com.openclassrooms.realestatemanagerv2.data.dao.AgentDAO
import com.openclassrooms.realestatemanagerv2.data.dao.MediaDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PointOfInterestCrossRefDAO
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.data.database.MyDatabase
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Callable
import java.util.concurrent.Executor

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PropertyRepositoryImplTest {

    private lateinit var database: MyDatabase
    private lateinit var propertyDao: PropertyLocalDAO
    private lateinit var agentDao: AgentDAO
    private lateinit var mediaDao: MediaDAO
    private lateinit var pointsOfInterestDao: PointOfInterestCrossRefDAO
    private lateinit var repository: PropertyRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(context, MyDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        propertyDao = database.propertyDAO()
        agentDao = database.agentDAO()
        mediaDao = database.mediaDAO()
        pointsOfInterestDao = database.pointOfInterestCrossRefDAO()

        repository = PropertyRepositoryImpl(
            database,
            propertyDao,
            agentDao,
            mediaDao,
            pointsOfInterestDao
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createSimpleProperty(id: String, type: String, agent: Agent): Property {
        return Property(
            id = id, type = type, price = 100000.0, area = 50.0,
            numberOfRooms = 3, description = "Desc", media = emptyList(),
            address = "Address", latitude = null, longitude = null,
            nearbyPointsOfInterest = emptyList(), status = PropertyStatus.Available,
            entryDate = 123456789L, saleDate = null, agent = agent
        )
    }

    @Test
    fun `getAllProperties returns properties from database`() = runTest {
        // Given
        val agent = Agent("1", "Schmidt", "118", "will@agency.com")
        val property = Property(
            id = "1", type = "Apartment", price = 150000.0, area = 50.0,
            numberOfRooms = 3, description = "Desc", media = emptyList(),
            address = "Address", latitude = null, longitude = null,
            nearbyPointsOfInterest = emptyList(), status = PropertyStatus.Available,
            entryDate = 123456789L, saleDate = null, agent = agent
        )
        repository.insertProperty(property)

        // When
        val result = repository.getAllProperties()

        // Then
        assertEquals(1, result.size)
        assertEquals("Apartment", result[0].type)
    }

    @Test
    fun `getPropertyTypes returns distinct types from database`() = runTest {
        // Given
        val agent = Agent("1", "Schmidt", "118", "will@agency.com")
        val prop1 = createSimpleProperty("1", "House", agent)
        val prop2 = createSimpleProperty("2", "Apartment", agent)

        repository.insertProperty(prop1)
        repository.insertProperty(prop2)

        // When
        val result = repository.getPropertyTypes()

        // Then
        assertEquals(2, result.size)
        assert(result.contains("House"))
        assert(result.contains("Apartment"))
    }

    @Test
    fun `searchByCriteria returns filtered properties`() = runTest {
        // Given
        val agent = Agent("1", "Schmidt", "118", "will@agency.com")
        repository.insertProperty(createSimpleProperty("1", "Manor", agent)) // Match
        repository.insertProperty(createSimpleProperty("2", "Studio", agent)) // No match

        val criteria = PropertySearchCriteria(
            propertyType = listOf("Manor"),
            minPrice = 50000.0
        )

        // When
        val result = repository.searchByCriteria(criteria)

        // Then
        assertEquals(1, result.size)
        assertEquals("Manor", result[0].type)
    }

    @Test
    fun `insertProperty maps domain to entity and calls dao`() = runTest {
        val mockAgent = Agent("agent1", "Schmidt", "118", "will@agency.com")
        val property = Property(
            id = "123",
            type = "Manor",
            price = 1000000.0,
            area = 250.0,
            numberOfRooms = 10,
            description = "Beau manoir",
            media = emptyList(),
            address = "Paris",
            latitude = null,
            longitude = null,
            nearbyPointsOfInterest = emptyList(),
            status = PropertyStatus.Available,
            entryDate = 123456789L,
            saleDate = null,
            agent = mockAgent
        )

        // WHEN
        repository.insertProperty(property)

        // THEN
        val result = repository.getPropertyById("123")
        assertEquals("123", result.id)
        assertEquals("Manor", result.type)
        assertEquals("Schmidt", result.agent.name)
    }
}