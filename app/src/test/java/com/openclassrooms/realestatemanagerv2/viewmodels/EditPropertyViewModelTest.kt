package com.openclassrooms.realestatemanagerv2.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetLocationUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyByIdUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdatePropertyUseCase
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyFormUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class EditPropertyViewModelTest {

    @get:Rule
    val testDispatcher = object : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }
        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }

    private lateinit var updatePropertyUseCase: UpdatePropertyUseCase
    private lateinit var getAllAgentsUseCase: GetAllAgentsUseCase
    private lateinit var getPropertyByIdUseCase: GetPropertyByIdUseCase
    private lateinit var getLocationUseCase: GetLocationUseCase

    private val fakeAgent = Agent("agent-1", "John Doe", "555-0100", "john@example.com")
    private val fakeAgents = listOf(fakeAgent, Agent("agent-2", "Jane Doe", "555-0101", "jane@example.com"))

    private val fakeProperty = Property(
        id = "prop-1", type = "House", price = 300000.0, area = 120.0,
        numberOfRooms = 5, description = "A beautiful house with garden",
        media = listOf(Photo("photo1.jpg", "Front view")),
        address = "123 Main Street", latitude = 40.0, longitude = -74.0,
        nearbyPointsOfInterest = listOf(PointOfInterest.PARK),
        status = PropertyStatus.Available,
        entryDate = 1700000000000L, saleDate = null, agent = fakeAgent
    )

    private fun setupMocks() {
        updatePropertyUseCase = mock()
        getAllAgentsUseCase = mock()
        getPropertyByIdUseCase = mock()
        getLocationUseCase = mock()
    }

    private fun createViewModel(propertyId: String = "prop-1"): EditPropertyViewModel {
        val savedState = SavedStateHandle(mapOf("propertyId" to propertyId))
        return EditPropertyViewModel(
            updatePropertyUseCase, getAllAgentsUseCase,
            getPropertyByIdUseCase, getLocationUseCase, savedState
        )
    }

    @Test
    fun init_withPropertyId_prefillsForm() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Editing)
        state as PropertyFormUiState.Editing
        assertEquals("prop-1", state.id)
        assertEquals("House", state.type.value)
        assertEquals("A beautiful house with garden", state.description.value)
        assertEquals("123 Main Street", state.address.value)
        assertEquals("5", state.numberOfRooms.value)
        assertEquals(fakeAgent, state.agent)
        assertEquals(1700000000000L, state.entryDate)
        assertTrue(state.nearbyPointSet.contains(PointOfInterest.PARK))
        assertEquals(1, state.mediaLists.size)
    }

    @Test
    fun init_withPropertyId_loadsAgents() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)

        val viewModel = createViewModel()

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertEquals(fakeAgents, state.agentList)
    }

    @Test
    fun init_fetchError_emitsError() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenThrow(RuntimeException("Not found"))

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Error)
        assertEquals("Not found", (state as PropertyFormUiState.Error).message)
    }

    @Test
    fun updateProperty_addressUnchanged_skipsGeocoding() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateProperty()

        verify(getLocationUseCase, never()).invoke(any())
    }

    @Test
    fun updateProperty_addressChanged_callsGeocoding() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getLocationUseCase(any())).thenReturn(LatLng(42.0, -76.0))
        val viewModel = createViewModel()

        viewModel.updateAddress("999 New Address")
        viewModel.updateProperty()

        verify(getLocationUseCase).invoke("999 New Address")
    }

    @Test
    fun updateProperty_success_emitsSuccess() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateProperty()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Success)
        assertEquals("prop-1", (state as PropertyFormUiState.Success).propertyId)
    }

    @Test
    fun updateProperty_updateFails_emitsError() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(updatePropertyUseCase(any())).thenThrow(RuntimeException("DB error"))
        val viewModel = createViewModel()

        viewModel.updateProperty()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Error)
        assertEquals("DB error", (state as PropertyFormUiState.Error).message)
    }

    @Test
    fun updatePrice_valid_updatesState() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updatePrice("400000")

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertEquals("400000", state.price.value)
        assertTrue(state.price.error.isNullOrBlank())
    }

    @Test
    fun addPhoto_addsToMediaList() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        val initialCount = (viewModel.uiState.value as PropertyFormUiState.Editing).mediaLists.size
        viewModel.updatePhotoUri("content://photo2.jpg")
        viewModel.updatePhotoDescription("Back view")
        viewModel.addPhoto()

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertEquals(initialCount + 1, state.mediaLists.size)
    }

    @Test
    fun deleteMedia_removesFromList() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        val mediaToDelete = (viewModel.uiState.value as PropertyFormUiState.Editing).mediaLists[0]
        viewModel.deletePhoto(mediaToDelete)

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertTrue(state.mediaLists.isEmpty())
    }

    @Test
    fun returnToEditingState_afterError_restores() = runTest {
        setupMocks()
        whenever(getPropertyByIdUseCase("prop-1")).thenReturn(fakeProperty)
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(updatePropertyUseCase(any())).thenThrow(RuntimeException("DB error"))
        val viewModel = createViewModel()

        viewModel.updateProperty()
        assertTrue(viewModel.uiState.value is PropertyFormUiState.Error)

        viewModel.returnToEditingState()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Editing)
        assertEquals("House", (state as PropertyFormUiState.Editing).type.value)
    }
}
