package com.openclassrooms.realestatemanagerv2.viewmodels

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyTypesUseCase
import com.openclassrooms.realestatemanagerv2.ui.states.SearchPropertiesUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SearchPropertiesViewModelTest {

    @get:Rule
    val testDispatcher = object : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }
        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }

    private lateinit var getAllAgentsUseCase: GetAllAgentsUseCase
    private lateinit var getPropertyTypesUseCase: GetPropertyTypesUseCase

    private val fakeAgents = listOf(
        Agent("agent-1", "John Doe", "555-0100", "john@example.com"),
        Agent("agent-2", "Jane Doe", "555-0101", "jane@example.com")
    )

    private val fakeTypes = listOf("House", "Apartment", "Manor")

    private fun createViewModel(): SearchPropertiesViewModel {
        return SearchPropertiesViewModel(getAllAgentsUseCase, getPropertyTypesUseCase)
    }

    @Test
    fun init_loadsAgentsAndTypes() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state is SearchPropertiesUiState.Editing)
        state as SearchPropertiesUiState.Editing
        assertEquals(fakeAgents, state.agentList)
        assertEquals(fakeTypes, state.allTypes)
    }

    @Test
    fun init_agentLoadFails_emitsError() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenThrow(RuntimeException("Network error"))
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state is SearchPropertiesUiState.Error)
        assertEquals("Network error", (state as SearchPropertiesUiState.Error).message)
    }

    @Test
    fun updateMinPrice_validNumber_clearsError() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        viewModel.updateMinPrice("100")

        val state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertEquals("100", state.minPrice.value)
        assertTrue(state.minPrice.error.isNullOrBlank())
    }

    @Test
    fun updateMinPrice_invalidNumber_setsError() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        viewModel.updateMinPrice("abc")

        val state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertNotNull(state.minPrice.error)
        assertFalse(state.minPrice.error!!.isBlank())
    }

    @Test
    fun updateMaxArea_negativeNumber_setsError() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        viewModel.updateMaxArea("-5")

        val state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertNotNull(state.maxArea.error)
        assertFalse(state.maxArea.error!!.isBlank())
    }

    @Test
    fun isFormValid_noFieldsFilled_returnsFalse() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        val state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertFalse(state.isFormValid)
    }

    @Test
    fun isFormValid_oneFieldFilled_returnsTrue() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        viewModel.updateMinPrice("100")

        val state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertTrue(state.isFormValid)
    }

    @Test
    fun isFormValid_onlyTypeSelected_returnsTrue() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        viewModel.updateTypeSelection("House", true)

        val state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertTrue(state.isFormValid)
    }

    @Test
    fun isFormValid_fieldWithError_returnsFalse() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        viewModel.updateMinPrice("abc")

        val state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertFalse(state.isFormValid)
    }

    @Test
    fun getCurrentCriteria_editingState_returnsCriteria() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        viewModel.updateMinPrice("100000")
        viewModel.updateMaxPrice("500000")
        viewModel.updateTypeSelection("House", true)

        val criteria = viewModel.getCurrentCriteria()
        assertNotNull(criteria)
        assertEquals(100000.0, criteria!!.minPrice)
        assertEquals(500000.0, criteria.maxPrice)
        assertEquals(listOf("House"), criteria.propertyType)
    }

    @Test
    fun getCurrentCriteria_errorState_returnsNull() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenThrow(RuntimeException("Error"))
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        val criteria = viewModel.getCurrentCriteria()
        assertNull(criteria)
    }

    @Test
    fun updateTypeSelection_addAndRemove_updatesSet() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        viewModel.updateTypeSelection("House", true)
        viewModel.updateTypeSelection("Apartment", true)
        var state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertEquals(setOf("House", "Apartment"), state.typeSet)

        viewModel.updateTypeSelection("House", false)
        state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertEquals(setOf("Apartment"), state.typeSet)
    }

    @Test
    fun updatePointOfInterestSelection_toggle_updatesSet() = runTest {
        getAllAgentsUseCase = mock()
        getPropertyTypesUseCase = mock()
        whenever(getAllAgentsUseCase()).thenReturn(fakeAgents)
        whenever(getPropertyTypesUseCase()).thenReturn(fakeTypes)
        val viewModel = createViewModel()

        viewModel.updatePointOfInterestSelection(PointOfInterest.PARK, true)
        var state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertTrue(state.nearbyPointSet.contains(PointOfInterest.PARK))

        viewModel.updatePointOfInterestSelection(PointOfInterest.PARK, false)
        state = viewModel.uiState.value as SearchPropertiesUiState.Editing
        assertFalse(state.nearbyPointSet.contains(PointOfInterest.PARK))
    }
}
