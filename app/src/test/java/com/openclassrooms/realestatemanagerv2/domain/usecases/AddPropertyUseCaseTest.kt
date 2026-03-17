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

class AddPropertyUseCaseTest {

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var useCase: AddPropertyUseCaseImpl

    private val testProperty = Property(
        id = "prop-1",
        type = "House",
        price = 300000.0,
        area = 120.0,
        numberOfRooms = 5,
        description = "A beautiful house",
        media = emptyList(),
        address = "123 Main St",
        latitude = 40.0,
        longitude = -74.0,
        nearbyPointsOfInterest = emptyList(),
        status = PropertyStatus.Available,
        entryDate = 1700000000000L,
        saleDate = null,
        agent = Agent("agent-1", "John Doe", "555-0100", "john@example.com")
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        propertyRepository = mock()
        useCase = AddPropertyUseCaseImpl(propertyRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun invoke_delegatesToRepository() = runTest {
        useCase(testProperty)

        verify(propertyRepository).insertProperty(testProperty)
    }
}
