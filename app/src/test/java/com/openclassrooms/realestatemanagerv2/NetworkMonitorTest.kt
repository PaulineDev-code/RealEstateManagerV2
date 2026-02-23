package com.openclassrooms.realestatemanagerv2

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import app.cash.turbine.test
import com.openclassrooms.realestatemanagerv2.data.network.NetworkMonitorImpl
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NetworkMonitorTest {

    private lateinit var networkMonitor: NetworkMonitorImpl

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    @Captor
    private lateinit var networkCallbackCaptor: ArgumentCaptor<ConnectivityManager.NetworkCallback>

    @Mock
    private lateinit var network: Network
    /*@Mock
    private lateinit var capabilities: NetworkCapabilities*/

    private lateinit var closeable: AutoCloseable

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)

        val mockContext = mock(Context::class.java)

        whenever(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(connectivityManager)

        networkMonitor = NetworkMonitorImpl(mockContext)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun whenNetworkIsInitiallyValidated_flowEmitsAvailable(): TestResult = runTest {
        // ARRANGE
        val capabilities = ShadowNetworkCapabilities.newInstance()
        val shadowCapabilities = Shadows.shadowOf(capabilities)
        shadowCapabilities.addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        whenever(connectivityManager.activeNetwork).thenReturn(network)
        whenever(connectivityManager.getNetworkCapabilities(network)).thenReturn(capabilities)

        // ACT & ASSERT
        networkMonitor.networkStatus.test {
            assert(awaitItem() == NetworkStatus.Available)
            expectNoEvents()
        }
    }

    @Test
    fun whenNetworkIsInitiallyNotValidated_flowEmitsUnavailable(): TestResult = runTest {
        // ARRANGE
        val capabilities = ShadowNetworkCapabilities.newInstance()

        whenever(connectivityManager.activeNetwork).thenReturn(network)
        whenever(connectivityManager.getNetworkCapabilities(network)).thenReturn(capabilities)

        // ACT & ASSERT
        networkMonitor.networkStatus.test {
            assert(awaitItem() == NetworkStatus.Unavailable)
            expectNoEvents()
        }
    }

    @Test
    fun whenNetworkIsLost_flowEmitsUnavailable(): TestResult = runTest {
        // ARRANGE
        val capabilities = ShadowNetworkCapabilities.newInstance()
        val shadowCapabilities = Shadows.shadowOf(capabilities)
        shadowCapabilities.addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        whenever(connectivityManager.activeNetwork).thenReturn(network)
        whenever(connectivityManager.getNetworkCapabilities(network)).thenReturn(capabilities)

        // ACT & ASSERT
        networkMonitor.networkStatus.test {
            assert(awaitItem() == NetworkStatus.Available)

            verify(connectivityManager).registerNetworkCallback(any(), networkCallbackCaptor.capture())
            val callback = networkCallbackCaptor.value

            callback.onLost(network)

            assert(awaitItem() == NetworkStatus.Unavailable)
        }
    }

    @Test
    fun whenCapabilitiesChangeToValidated_flowEmitsAvailable(): TestResult = runTest {
        // ARRANGE
        val initialCapabilities = ShadowNetworkCapabilities.newInstance()

        whenever(connectivityManager.activeNetwork).thenReturn(network)
        whenever(connectivityManager.getNetworkCapabilities(network)).thenReturn(initialCapabilities)

        // ACT & ASSERT
        networkMonitor.networkStatus.test {
            assert(awaitItem() == NetworkStatus.Unavailable)

            verify(connectivityManager).registerNetworkCallback(any(), networkCallbackCaptor.capture())
            val callback = networkCallbackCaptor.value

            val validatedCaps = ShadowNetworkCapabilities.newInstance()
            val shadowValidatedCaps = Shadows.shadowOf(validatedCaps)
            shadowValidatedCaps.addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

            callback.onCapabilitiesChanged(network, validatedCaps)

            assert(awaitItem() == NetworkStatus.Available)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenCollectionStops_unregisterNetworkCallbackIsCalled(): TestResult = runTest {
        whenever(connectivityManager.activeNetwork).thenReturn(null)

        val job = launch {
            networkMonitor.networkStatus.collect { }
        }

        advanceUntilIdle()
        job.cancel()
        job.join()

        verify(connectivityManager).unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>())
    }
}
