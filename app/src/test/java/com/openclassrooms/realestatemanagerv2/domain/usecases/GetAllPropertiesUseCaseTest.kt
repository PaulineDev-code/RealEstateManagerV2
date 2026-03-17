package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import kotlin.collections.emptyList

class GetAllPropertiesUseCaseTest {

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var useCase: GetAllPropertiesUseCaseImpl

    private val fakeAgent = Agent("agent-1", "John Doe", "555-0100", "john@example.com")

    private val fakeProperties = listOf(
        Property(
            id = "prop-1", type = "House", price = 300000.0, area = 120.0,
            numberOfRooms = 5, description = "Nice house", media = emptyList(),
            address = "123 Main St", latitude = 40.0, longitude = -74.0,
            nearbyPointsOfInterest = emptyList(), status = PropertyStatus.Available,
            entryDate = 1700000000000L, saleDate = null, agent = fakeAgent
        ),
        Property(
            id = "prop-2", type = "Apartment", price = 200000.0, area = 80.0,
            numberOfRooms = 3, description = "Modern flat", media = emptyList(),
            address = "456 Oak Ave", latitude = 41.0, longitude = -75.0,
            nearbyPointsOfInterest = emptyList(), status = PropertyStatus.Sold,
            entryDate = 1700000000000L, saleDate = 1700100000000L, agent = fakeAgent
        )
    )

    @Before
    fun setUp() {
        propertyRepository = mock()
        useCase = GetAllPropertiesUseCaseImpl(propertyRepository)
    }

    @Test
    fun invoke_returnsPropertiesFromRepository() = runTest {
        whenever(propertyRepository.getAllProperties()).thenReturn(fakeProperties)

        val result = useCase()

        assertEquals(fakeProperties, result)
    }

    @Test
    fun invoke_emptyList_returnsEmpty() = runTest {
        whenever(propertyRepository.getAllProperties()).thenReturn(emptyList())

        val result = useCase()

        assertEquals(emptyList<Property>(), result)
    }
}
