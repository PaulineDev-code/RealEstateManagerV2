package com.openclassrooms.realestatemanagerv2.viewmodels

import app.cash.turbine.test
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.ObserveNetworkStatusUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.SearchPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdateMissingLocationUseCase
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyUiState
import com.openclassrooms.realestatemanagerv2.utils.DatabaseStatusTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PropertySharedViewModelTest {

    @get:Rule
    val testDispatcher = object : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }
        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }

    private lateinit var getAllPropertiesUseCase: GetAllPropertiesUseCase
    private lateinit var searchPropertiesUseCase: SearchPropertiesUseCase
    private lateinit var updateMissingLocationUseCase: UpdateMissingLocationUseCase
    private lateinit var observeNetworkStatusUseCase: ObserveNetworkStatusUseCase
    private lateinit var databaseStatusTracker: DatabaseStatusTracker

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
            nearbyPointsOfInterest = emptyList(), status = PropertyStatus.Available,
            entryDate = 1700000000000L, saleDate = null, agent = fakeAgent
        )
    )

    private fun setupMocks(
        networkFlow: MutableStateFlow<NetworkStatus> = MutableStateFlow(NetworkStatus.Unknown),
        dbReady: Boolean = true
    ) {
        getAllPropertiesUseCase = mock()
        searchPropertiesUseCase = mock()
        updateMissingLocationUseCase = mock()
        observeNetworkStatusUseCase = mock()
        databaseStatusTracker = DatabaseStatusTracker()
        whenever(observeNetworkStatusUseCase()).thenReturn(networkFlow)
        if (dbReady) {
            databaseStatusTracker.notifyPrepopulated()
        }
    }

    private fun createViewModel(): PropertySharedViewModel {
        return PropertySharedViewModel(
            getAllPropertiesUseCase, searchPropertiesUseCase,
            updateMissingLocationUseCase, observeNetworkStatusUseCase,
            databaseStatusTracker
        )
    }

    @Test
    fun init_dbReady_loadsPropertiesAndTriggersSync() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)

        createViewModel()
        advanceUntilIdle()

        verify(getAllPropertiesUseCase).invoke()
        verify(updateMissingLocationUseCase).invoke()
    }

    @Test
    fun init_dbNotReady_waitsForReady() = runTest {
        setupMocks(dbReady = false)

        createViewModel()
        advanceUntilIdle()

        verify(getAllPropertiesUseCase, never()).invoke()
        verify(updateMissingLocationUseCase, never()).invoke()
    }

    @Test
    fun init_loadSuccess_emitsSuccessState() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PropertyUiState.Success)
            assertEquals(fakeProperties, (state as PropertyUiState.Success).properties)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_loadError_emitsErrorState() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenThrow(RuntimeException("DB error"))
        whenever(updateMissingLocationUseCase()).thenReturn(0)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PropertyUiState.Error)
            assertEquals("DB error", (state as PropertyUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiState_combinesWithNetworkStatus_available() = runTest {
        val networkFlow = MutableStateFlow(NetworkStatus.Available)
        setupMocks(networkFlow = networkFlow)
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PropertyUiState.Success)
            assertEquals(NetworkStatus.Available, (state as PropertyUiState.Success).networkStatus)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiState_combinesWithNetworkStatus_unavailable() = runTest {
        val networkFlow = MutableStateFlow(NetworkStatus.Unavailable)
        setupMocks(networkFlow = networkFlow)
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PropertyUiState.Success)
            assertEquals(NetworkStatus.Unavailable, (state as PropertyUiState.Success).networkStatus)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateSelectedProperty_updatesSelectedId() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateSelectedProperty("prop-2")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PropertyUiState.Success)
            assertEquals("prop-2", (state as PropertyUiState.Success).selectedPropertyId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateAddedProperty_updatesAddedId() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateAddedProperty("new-prop")

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PropertyUiState.Success)
            assertEquals("new-prop", (state as PropertyUiState.Success).addedPropertyId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchProperties_emitsFilteredSuccess() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)
        val criteria = PropertySearchCriteria(minPrice = 250000.0)
        val filtered = listOf(fakeProperties[0])
        whenever(searchPropertiesUseCase(criteria)).thenReturn(filtered)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.searchProperties(criteria)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PropertyUiState.Success)
            state as PropertyUiState.Success
            assertTrue(state.isFiltered)
            assertEquals(filtered, state.properties)
            assertEquals(1, state.detailPaneCloseVersion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun resetProperties_clearsFilterAndReloads() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)
        val criteria = PropertySearchCriteria(minPrice = 250000.0)
        whenever(searchPropertiesUseCase(criteria)).thenReturn(listOf(fakeProperties[0]))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.searchProperties(criteria)
        advanceUntilIdle()
        viewModel.resetProperties()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PropertyUiState.Success)
            state as PropertyUiState.Success
            assertFalse(state.isFiltered)
            assertEquals(fakeProperties, state.properties)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshProperties_whenNotFiltered_reloads() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.refreshProperties()
        advanceUntilIdle()

        verify(getAllPropertiesUseCase, times(2)).invoke()
    }

    @Test
    fun refreshProperties_whenFiltered_doesNotReload() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)
        val criteria = PropertySearchCriteria(minPrice = 250000.0)
        whenever(searchPropertiesUseCase(criteria)).thenReturn(listOf(fakeProperties[0]))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.searchProperties(criteria)
        advanceUntilIdle()

        viewModel.refreshProperties()
        advanceUntilIdle()

        verify(getAllPropertiesUseCase, times(1)).invoke()
    }

    @Test
    fun updateAndRefreshIfNeeded_countPositive_refreshes() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(2)

        createViewModel()
        advanceUntilIdle()

        // init calls loadProperties once + updateAndRefreshIfNeeded (count>0 triggers refreshProperties)
        verify(getAllPropertiesUseCase, times(2)).invoke()
    }

    @Test
    fun updateAndRefreshIfNeeded_countZero_noRefresh() = runTest {
        setupMocks()
        whenever(getAllPropertiesUseCase()).thenReturn(fakeProperties)
        whenever(updateMissingLocationUseCase()).thenReturn(0)

        createViewModel()
        advanceUntilIdle()

        // init calls loadProperties once, updateAndRefreshIfNeeded returns 0 so no refresh
        verify(getAllPropertiesUseCase, times(1)).invoke()
    }
}
