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

class GetPropertyByIdUseCaseTest {

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var useCase: GetPropertyByIdUseCaseImpl

    private val fakeProperty = Property(
        id = "prop-1", type = "House", price = 300000.0, area = 120.0,
        numberOfRooms = 5, description = "Nice house", media = emptyList(),
        address = "123 Main St", latitude = 40.0, longitude = -74.0,
        nearbyPointsOfInterest = emptyList(), status = PropertyStatus.Available,
        entryDate = 1700000000000L, saleDate = null,
        agent = Agent("agent-1", "John Doe", "555-0100", "john@example.com")
    )

    @Before
    fun setUp() {
        propertyRepository = mock()
        useCase = GetPropertyByIdUseCaseImpl(propertyRepository)
    }

    @Test
    fun invoke_returnsPropertyFromRepository() = runTest {
        whenever(propertyRepository.getPropertyById("prop-1")).thenReturn(fakeProperty)

        val result = useCase("prop-1")

        assertEquals(fakeProperty, result)
    }
}
