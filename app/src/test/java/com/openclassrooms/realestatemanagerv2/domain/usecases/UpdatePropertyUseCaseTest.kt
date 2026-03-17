package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UpdatePropertyUseCaseTest {

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var useCase: UpdatePropertyUseCaseImpl

    private val testProperty = Property(
        id = "prop-1",
        type = "Apartment",
        price = 250000.0,
        area = 80.0,
        numberOfRooms = 3,
        description = "Modern apartment",
        media = emptyList(),
        address = "456 Oak Ave",
        latitude = 41.0,
        longitude = -75.0,
        nearbyPointsOfInterest = emptyList(),
        status = PropertyStatus.Available,
        entryDate = 1700000000000L,
        saleDate = null,
        agent = Agent("agent-1", "Jane Doe", "555-0101", "jane@example.com")
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        propertyRepository = mock()
        useCase = UpdatePropertyUseCaseImpl(propertyRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun invoke_delegatesToRepository() = runTest {
        useCase(testProperty)

        verify(propertyRepository).updateProperty(testProperty)
    }
}
