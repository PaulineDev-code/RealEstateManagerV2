package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class GetLocationUseCaseTest {

    private lateinit var locationRepository: LocationRepository
    private lateinit var useCase: GetLocationUseCaseImpl

    @Before
    fun setUp() {
        locationRepository = mock()
        useCase = GetLocationUseCaseImpl(locationRepository)
    }

    @Test
    fun invoke_returnsLatLngFromRepository() = runTest {
        val address = "1600 Pennsylvania Ave, Washington DC"
        val expected = LatLng(38.8977, -77.0365)
        whenever(locationRepository.geocodeNowOrNull(address)).thenReturn(expected)

        val result = useCase(address)

        assertEquals(expected, result)
    }

    @Test
    fun invoke_returnsNullWhenGeocodeFails() = runTest {
        val address = "Invalid address xyz"
        whenever(locationRepository.geocodeNowOrNull(address)).thenReturn(null)

        val result = useCase(address)

        assertNull(result)
    }
}
