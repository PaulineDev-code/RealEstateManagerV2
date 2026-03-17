package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals

class SearchPropertiesUseCaseTest {

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var useCase: SearchPropertiesUseCaseImpl

    private val fakeAgent = Agent("agent-1", "John Doe", "555-0100", "john@example.com")

    private val fakeResults = listOf(
        Property(
            id = "prop-1", type = "House", price = 300000.0, area = 120.0,
            numberOfRooms = 5, description = "Nice house", media = emptyList(),
            address = "123 Main St", latitude = 40.0, longitude = -74.0,
            nearbyPointsOfInterest = emptyList(), status = PropertyStatus.Available,
            entryDate = 1700000000000L, saleDate = null, agent = fakeAgent
        )
    )

    @Before
    fun setUp() {
        propertyRepository = mock()
        useCase = SearchPropertiesUseCaseImpl(propertyRepository)
    }

    @Test
    fun invoke_passesCriteriaToRepository() = runTest {
        val criteria = PropertySearchCriteria(
            minPrice = 200000.0,
            maxPrice = 500000.0,
            propertyType = listOf("House")
        )
        whenever(propertyRepository.searchByCriteria(criteria)).thenReturn(fakeResults)

        val result = useCase(criteria)

        assertEquals(fakeResults, result)
    }
}
