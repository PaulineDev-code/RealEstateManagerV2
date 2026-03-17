package com.openclassrooms.realestatemanagerv2.viewmodels

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyByIdUseCase
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyDetailsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PropertyDetailsViewModelTest {

    @get:Rule
    val testDispatcher = object : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }
        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }

    private lateinit var getPropertyByIdUseCase: GetPropertyByIdUseCase
    private lateinit var viewModel: PropertyDetailsViewModel

    private val fakeAgent = Agent("agent-1", "John Doe", "555-0100", "john@example.com")

    private val fakeProperty = Property(
        id = "prop-1", type = "House", price = 300000.0, area = 120.0,
        numberOfRooms = 5, description = "A beautiful house", media = emptyList(),
        address = "123 Main St", latitude = 40.0, longitude = -74.0,
        nearbyPointsOfInterest = emptyList(), status = PropertyStatus.Available,
        entryDate = 1700000000000L, saleDate = null, agent = fakeAgent
    )

    @Before
    fun setUp() {
        getPropertyByIdUseCase = mock()
    }

    private fun createViewModel(): PropertyDetailsViewModel {
        return PropertyDetailsViewModel(getPropertyByIdUseCase)
    }

    @Test
    fun initialState_isSuccessWithNullProperty() {
        viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyDetailsUiState.Success)
        assertNull((state as PropertyDetailsUiState.Success).property)
    }

    @Test
    fun getPropertyById_validId_emitsSuccessWithProperty() = runTest {
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        viewModel = createViewModel()

        viewModel.getPropertyById("prop-1")

        val state = viewModel.uiState.value
        assertTrue(state is PropertyDetailsUiState.Success)
        state as PropertyDetailsUiState.Success
        assertEquals(fakeProperty, state.property)
        assertEquals(0, state.selectedPhotoIndex)
        assertEquals(false, state.isPhotoViewerShown)
    }

    @Test
    fun getPropertyById_error_emitsError() = runTest {
        whenever(getPropertyByIdUseCase("bad-id")).thenThrow(RuntimeException("Not found"))
        viewModel = createViewModel()

        viewModel.getPropertyById("bad-id")

        val state = viewModel.uiState.value
        assertTrue(state is PropertyDetailsUiState.Error)
        assertEquals("Not found", (state as PropertyDetailsUiState.Error).message)
    }

    @Test
    fun getPropertyById_sameIdTwice_fetchesOnlyOnce() = runTest {
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        viewModel = createViewModel()

        viewModel.getPropertyById("prop-1")
        viewModel.getPropertyById("prop-1")

        verify(getPropertyByIdUseCase, times(1)).invoke("prop-1")
    }

    @Test
    fun getPropertyById_differentId_fetchesAgain() = runTest {
        val fakeProperty2 = fakeProperty.copy(id = "prop-2")
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getPropertyByIdUseCase("prop-2")).thenReturn(fakeProperty2)
        viewModel = createViewModel()

        viewModel.getPropertyById("prop-1")
        viewModel.getPropertyById("prop-2")

        verify(getPropertyByIdUseCase).invoke("prop-1")
        verify(getPropertyByIdUseCase).invoke("prop-2")
    }

    @Test
    fun updateSelectedPhotoIndex_updatesState() = runTest {
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        viewModel = createViewModel()
        viewModel.getPropertyById("prop-1")

        viewModel.updateSelectedPhotoIndex(3)

        val state = viewModel.uiState.value
        assertTrue(state is PropertyDetailsUiState.Success)
        assertEquals(3, (state as PropertyDetailsUiState.Success).selectedPhotoIndex)
    }

    @Test
    fun updatePhotoViewerShown_updatesState() = runTest {
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        viewModel = createViewModel()
        viewModel.getPropertyById("prop-1")

        viewModel.updatePhotoViewerShown(true)

        val state = viewModel.uiState.value
        assertTrue(state is PropertyDetailsUiState.Success)
        assertTrue((state as PropertyDetailsUiState.Success).isPhotoViewerShown)
    }

    @Test
    fun updatePhotoViewerShown_whenNotSuccess_noOp() = runTest {
        whenever(getPropertyByIdUseCase("bad-id")).thenThrow(RuntimeException("Not found"))
        viewModel = createViewModel()
        viewModel.getPropertyById("bad-id")

        viewModel.updatePhotoViewerShown(true)

        val state = viewModel.uiState.value
        assertTrue(state is PropertyDetailsUiState.Error)
    }
}
