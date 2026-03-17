package com.openclassrooms.realestatemanagerv2.viewmodels

import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.usecases.AddPropertyUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetLocationUseCase
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyFormUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AddPropertyViewModelTest {

    @get:Rule
    val testDispatcher = object : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }
        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }

    private lateinit var addPropertyUseCase: AddPropertyUseCase
    private lateinit var getAllAgentsUseCase: GetAllAgentsUseCase
    private lateinit var getLocationUseCase: GetLocationUseCase

    private val fakeAgents = listOf(
        Agent("agent-1", "John Doe", "555-0100", "john@example.com"),
        Agent("agent-2", "Jane Doe", "555-0101", "jane@example.com")
    )

    private fun createViewModel(): AddPropertyViewModel {
        return AddPropertyViewModel(addPropertyUseCase, getAllAgentsUseCase, getLocationUseCase)
    }

    private fun setupMocks() {
        addPropertyUseCase = mock()
        getAllAgentsUseCase = mock()
        getLocationUseCase = mock()
    }

    private fun fillValidForm(viewModel: AddPropertyViewModel) {
        viewModel.updateType("House")
        viewModel.updatePrice("300000")
        viewModel.updateArea("120")
        viewModel.updateNumberOfRooms("5")
        viewModel.updateDescription("A beautiful house with garden")
        viewModel.updateAddress("123 Main Street, New York")
        viewModel.updateAgent(fakeAgents[0])
        viewModel.updatePhotoUri("content://photo1.jpg")
        viewModel.updatePhotoDescription("Front view")
        viewModel.addPhoto()
        viewModel.updatePointOfInterestSelection(PointOfInterest.PARK, true)
        viewModel.updateEntryDate(1700000000000L)
    }

    @Test
    fun init_loadsAgents_intoEditingState() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Editing)
        assertEquals(fakeAgents, (state as PropertyFormUiState.Editing).agentList)
    }

    @Test
    fun init_agentLoadFails_emitsError() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenThrow(RuntimeException("Network error"))

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Error)
        assertEquals("Network error", (state as PropertyFormUiState.Error).message)
    }

    @Test
    fun updateType_empty_setsError() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateType("")

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertNotNull(state.type.error)
        assertTrue(state.type.error!!.contains("Can't be empty"))
    }

    @Test
    fun updateType_valid_clearsError() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateType("House")

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertEquals("House", state.type.value)
        assertTrue(state.type.error.isNullOrBlank())
    }

    @Test
    fun updatePrice_negative_setsError() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updatePrice("-100")

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertNotNull(state.price.error)
        assertFalse(state.price.error!!.isBlank())
    }

    @Test
    fun updatePrice_valid_clearsError() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updatePrice("300000")

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertEquals("300000", state.price.value)
        assertTrue(state.price.error.isNullOrBlank())
    }

    @Test
    fun updateDescription_tooShort_setsError() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateDescription("abc")

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertNotNull(state.description.error)
        assertTrue(state.description.error!!.contains("too short", ignoreCase = true))
    }

    @Test
    fun updateAddress_empty_setsError() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateAddress("")

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertNotNull(state.address.error)
        assertTrue(state.address.error!!.contains("Can't be empty"))
    }

    @Test
    fun updateNumberOfRooms_nonNumeric_setsError() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateNumberOfRooms("abc")

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertNotNull(state.numberOfRooms.error)
        assertFalse(state.numberOfRooms.error!!.isBlank())
    }

    @Test
    fun isFormValid_allFieldsValid_returnsTrue() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        fillValidForm(viewModel)

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertTrue(state.isFormValid)
    }

    @Test
    fun isFormValid_missingAgent_returnsFalse() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateType("House")
        viewModel.updatePrice("300000")
        viewModel.updateArea("120")
        viewModel.updateNumberOfRooms("5")
        viewModel.updateDescription("A beautiful house with garden")
        viewModel.updateAddress("123 Main Street")
        viewModel.updatePhotoUri("content://photo1.jpg")
        viewModel.updatePhotoDescription("Front")
        viewModel.addPhoto()
        viewModel.updatePointOfInterestSelection(PointOfInterest.PARK, true)
        viewModel.updateEntryDate(1700000000000L)

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertFalse(state.isFormValid)
    }

    @Test
    fun isFormValid_noMedia_returnsFalse() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateType("House")
        viewModel.updatePrice("300000")
        viewModel.updateArea("120")
        viewModel.updateNumberOfRooms("5")
        viewModel.updateDescription("A beautiful house with garden")
        viewModel.updateAddress("123 Main Street")
        viewModel.updateAgent(fakeAgents[0])
        viewModel.updatePointOfInterestSelection(PointOfInterest.PARK, true)
        viewModel.updateEntryDate(1700000000000L)

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertFalse(state.isFormValid)
    }

    @Test
    fun isFormValid_noPoi_returnsFalse() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateType("House")
        viewModel.updatePrice("300000")
        viewModel.updateArea("120")
        viewModel.updateNumberOfRooms("5")
        viewModel.updateDescription("A beautiful house with garden")
        viewModel.updateAddress("123 Main Street")
        viewModel.updateAgent(fakeAgents[0])
        viewModel.updatePhotoUri("content://photo1.jpg")
        viewModel.updatePhotoDescription("Front")
        viewModel.addPhoto()
        viewModel.updateEntryDate(1700000000000L)

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertFalse(state.isFormValid)
    }

    @Test
    fun isFormValid_noEntryDate_returnsFalse() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updateType("House")
        viewModel.updatePrice("300000")
        viewModel.updateArea("120")
        viewModel.updateNumberOfRooms("5")
        viewModel.updateDescription("A beautiful house with garden")
        viewModel.updateAddress("123 Main Street")
        viewModel.updateAgent(fakeAgents[0])
        viewModel.updatePhotoUri("content://photo1.jpg")
        viewModel.updatePhotoDescription("Front")
        viewModel.addPhoto()
        viewModel.updatePointOfInterestSelection(PointOfInterest.PARK, true)

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertFalse(state.isFormValid)
    }

    @Test
    fun addPhoto_addsToMediaList() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updatePhotoUri("content://photo1.jpg")
        viewModel.updatePhotoDescription("Front view")
        viewModel.addPhoto()

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertEquals(1, state.mediaLists.size)
        val photo = state.mediaLists[0] as Photo
        assertEquals("content://photo1.jpg", photo.mediaUrl)
        assertEquals("Front view", photo.description)
        assertEquals("", state.photoUri)
        assertEquals("", state.photoDescription)
    }

    @Test
    fun deleteMedia_removesFromList() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        val viewModel = createViewModel()

        viewModel.updatePhotoUri("content://photo1.jpg")
        viewModel.updatePhotoDescription("Front view")
        viewModel.addPhoto()

        val addedPhoto = (viewModel.uiState.value as PropertyFormUiState.Editing).mediaLists[0]
        viewModel.deleteMedia(addedPhoto)

        val state = viewModel.uiState.value as PropertyFormUiState.Editing
        assertTrue(state.mediaLists.isEmpty())
    }

    @Test
    fun createProperty_validForm_emitsSuccessState() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getLocationUseCase(any())).thenReturn(LatLng(40.0, -74.0))
        val viewModel = createViewModel()

        fillValidForm(viewModel)
        viewModel.createProperty()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Success)
        verify(addPropertyUseCase).invoke(any())
    }

    @Test
    fun createProperty_geocodingFails_createsWithNullCoords() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getLocationUseCase(any())).thenReturn(null)
        val viewModel = createViewModel()

        fillValidForm(viewModel)
        viewModel.createProperty()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Success)
        verify(addPropertyUseCase).invoke(any())
    }

    @Test
    fun returnToEditingState_afterError_restoresPreviousState() = runTest {
        setupMocks()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getLocationUseCase(any())).thenReturn(LatLng(40.0, -74.0))
        whenever(addPropertyUseCase(any())).thenThrow(RuntimeException("DB error"))
        val viewModel = createViewModel()

        fillValidForm(viewModel)
        viewModel.createProperty()

        assertTrue(viewModel.uiState.value is PropertyFormUiState.Error)

        viewModel.returnToEditingState()

        val state = viewModel.uiState.value
        assertTrue(state is PropertyFormUiState.Editing)
        assertEquals("House", (state as PropertyFormUiState.Editing).type.value)
    }
}
