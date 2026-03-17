package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.openclassrooms.realestatemanagerv2.domain.repositories.PropertyRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals

class GetPropertyTypesUseCaseTest {

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var useCase: GetPropertyTypesUseCaseImpl

    @Before
    fun setUp() {
        propertyRepository = mock()
        useCase = GetPropertyTypesUseCaseImpl(propertyRepository)
    }

    @Test
    fun invoke_returnsTypesFromRepository() = runTest {
        val types = listOf("House", "Apartment", "Manor", "Penthouse")
        whenever(propertyRepository.getPropertyTypes()).thenReturn(types)

        val result = useCase()

        assertEquals(types, result)
    }
}
