package com.openclassrooms.realestatemanagerv2.domain.usecases

import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals

class UpdateMissingLocationUseCaseTest {

    private lateinit var locationRepository: LocationRepository
    private lateinit var useCase: UpdateMissingLocationUseCaseImpl

    @Before
    fun setUp() {
        locationRepository = mock()
        useCase = UpdateMissingLocationUseCaseImpl(locationRepository)
    }

    @Test
    fun invoke_noAddresses_returnsZero() = runTest {
        whenever(locationRepository.getAddressesToGeocode()).thenReturn(emptyList())

        val result = useCase()

        assertEquals(0, result)
        verify(locationRepository, never()).updateLocationByAddress(
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any()
        )
    }

    @Test
    fun invoke_twoAddressesBothGeocoded_updatesBoth() = runTest {
        val address1 = "123 Main St"
        val address2 = "456 Oak Ave"
        whenever(locationRepository.getAddressesToGeocode()).thenReturn(listOf(address1, address2))
        whenever(locationRepository.geocodeNowOrNull(address1)).thenReturn(LatLng(40.0, -74.0))
        whenever(locationRepository.geocodeNowOrNull(address2)).thenReturn(LatLng(41.0, -75.0))

        useCase()

        verify(locationRepository).updateLocationByAddress(address1, 40.0, -74.0)
        verify(locationRepository).updateLocationByAddress(address2, 41.0, -75.0)
    }

    @Test
    fun invoke_oneGeocodeFails_onlyUpdatesSuccessful() = runTest {
        val address1 = "Unknown Place"
        val address2 = "456 Oak Ave"
        whenever(locationRepository.getAddressesToGeocode()).thenReturn(listOf(address1, address2))
        whenever(locationRepository.geocodeNowOrNull(address1)).thenReturn(null)
        whenever(locationRepository.geocodeNowOrNull(address2)).thenReturn(LatLng(41.0, -75.0))

        useCase()

        verify(locationRepository, never()).updateLocationByAddress(
            org.mockito.kotlin.eq(address1),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any()
        )
        verify(locationRepository).updateLocationByAddress(address2, 41.0, -75.0)
    }

    @Test
    fun invoke_allGeocodeFail_noUpdates() = runTest {
        whenever(locationRepository.getAddressesToGeocode()).thenReturn(listOf("A", "B"))
        whenever(locationRepository.geocodeNowOrNull(org.mockito.kotlin.any())).thenReturn(null)

        useCase()

        verify(locationRepository, never()).updateLocationByAddress(
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any()
        )
    }

    @Test
    fun invoke_returnsAddressCount() = runTest {
        whenever(locationRepository.getAddressesToGeocode()).thenReturn(listOf("A", "B", "C"))
        whenever(locationRepository.geocodeNowOrNull("A")).thenReturn(null)
        whenever(locationRepository.geocodeNowOrNull("B")).thenReturn(LatLng(1.0, 2.0))
        whenever(locationRepository.geocodeNowOrNull("C")).thenReturn(LatLng(3.0, 4.0))

        val result = useCase()

        assertEquals(3, result)
    }

    @Test
    fun invoke_passesCorrectCoordinates() = runTest {
        val address = "1600 Pennsylvania Ave"
        val lat = 38.8977
        val lng = -77.0365
        whenever(locationRepository.getAddressesToGeocode()).thenReturn(listOf(address))
        whenever(locationRepository.geocodeNowOrNull(address)).thenReturn(LatLng(lat, lng))

        useCase()

        verify(locationRepository).updateLocationByAddress(address, lat, lng)
    }
}
