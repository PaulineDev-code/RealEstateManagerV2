package com.openclassrooms.realestatemanagerv2.domain.usecases

import app.cash.turbine.test
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.repositories.LocationRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals

class ObserveNetworkStatusUseCaseTest {

    private lateinit var locationRepository: LocationRepository
    private lateinit var useCase: ObserveNetworkStatusUseCaseImpl

    @Before
    fun setUp() {
        locationRepository = mock()
        useCase = ObserveNetworkStatusUseCaseImpl(locationRepository)
    }

    @Test
    fun invoke_returnsFlowFromRepository() = runTest {
        whenever(locationRepository.observeNetworkStatus()).thenReturn(
            flowOf(NetworkStatus.Available, NetworkStatus.Unavailable)
        )

        useCase().test {
            assertEquals(NetworkStatus.Available, awaitItem())
            assertEquals(NetworkStatus.Unavailable, awaitItem())
            awaitComplete()
        }
    }
}
