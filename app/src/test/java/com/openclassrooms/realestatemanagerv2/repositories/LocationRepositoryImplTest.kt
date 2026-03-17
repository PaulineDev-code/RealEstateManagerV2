package com.openclassrooms.realestatemanagerv2.repositories

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.data.dao.PropertyLocalDAO
import com.openclassrooms.realestatemanagerv2.data.network.NetworkMonitor
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LocationRepositoryImplTest {

    private lateinit var propertyDao: PropertyLocalDAO
    private lateinit var repository: LocationRepositoryImpl
    private lateinit var networkMonitor: NetworkMonitor // Ton interface NetworkMonitor
    private lateinit var context: Context

    @Before
    fun setUp() {
        propertyDao = mock()
        networkMonitor = mock()
        context = mock()
        repository = LocationRepositoryImpl(context, propertyDao, networkMonitor)
    }

    @Test
    fun getAddressesToGeocode_delegatesToDao() = runTest {
        val expectedAddresses = listOf("123 Main St", "456 Oak Ave")
        whenever(propertyDao.getAddressesWithoutLatLng()).thenReturn(expectedAddresses)

        val result = repository.getAddressesToGeocode()

        assertEquals(expectedAddresses, result)
        verify(propertyDao).getAddressesWithoutLatLng()
    }

    @Test
    fun updateLocationByAddress_callsDaoUpdate() = runTest {
        val address = "123 Main St"
        val lat = 48.8566
        val lng = 2.3522

        repository.updateLocationByAddress(address, lat, lng)

        verify(propertyDao).updateLocationByAddress(address, lat, lng)
    }

    @Test
    fun observeNetworkStatus_emitsCorrectStatus() = runTest {

        val expectedStatus = NetworkStatus.Available
        whenever(networkMonitor.networkStatus).thenReturn(flowOf(expectedStatus))

        val result = repository.observeNetworkStatus().first()

        assertEquals(expectedStatus, result)
    }
}