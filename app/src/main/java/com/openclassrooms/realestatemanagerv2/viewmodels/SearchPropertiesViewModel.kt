package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyTypesUseCase
import com.openclassrooms.realestatemanagerv2.ui.states.SearchPropertiesUiState
import com.openclassrooms.realestatemanagerv2.utils.validatePositiveNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the state and logic of the property search form.
 *
 * This ViewModel handles:
 * 1. Initializing reference data (Agents and Property Types) for form selection.
 * 2. Real-time validation of search constraints (price, area, room count, etc.).
 * 3. Dynamic state management of multi-selection filters like property types and POIs.
 * 4. Transformation of the UI state into a [PropertySearchCriteria] domain model for execution.
 *
 * @property getAllAgentsUseCase Use case to fetch the list of agents for filtering.
 * @property getPropertyTypesUseCase Use case to fetch available property types.
 */
@HiltViewModel
class SearchPropertiesViewModel @Inject constructor(
    private val getAllAgentsUseCase: GetAllAgentsUseCase,
    private val getPropertyTypesUseCase: GetPropertyTypesUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<SearchPropertiesUiState>(SearchPropertiesUiState.Editing())

    /**
     * UI State flow representing the current search form status (Editing or Error).
     */
    val uiState: StateFlow<SearchPropertiesUiState> = _uiState

    /**
     * List of all possible Points of Interest to be displayed in the search form.
     */
    val allPointOfInterestList: List<PointOfInterest> = PointOfInterest.entries

    init {
        //retrieve all agents
        viewModelScope.launch {
            try {
                val agents = getAllAgentsUseCase()
                Log.d("SearchViewModel", "Collected agents: $agents")
                updateState {
                    copy(agentList = agents)
                }
            } catch (exception: Exception) {
                Log.e("SearchViewModel", "Error collecting agents", exception)
                handleError(exception)
            }
        }
        //retrieve each property types
        viewModelScope.launch {
            try {
                val types = getPropertyTypesUseCase()
                updateState {
                    copy(allTypes = types)
                }
            } catch (exception: Exception) {
                Log.e("SearchViewModel", "Error collecting types", exception)
                handleError(exception)
            }
        }
    }

    fun updateMinPrice(newMinPrice: String) {
        val error = newMinPrice.validatePositiveNumber()
        updateState {
            copy(
                minPrice = minPrice.copy(value = newMinPrice, error = error)
            )
        }
    }

    fun updateMaxPrice(newMaxPrice: String) {
        val error = newMaxPrice.validatePositiveNumber()
        updateState {
            copy(
                maxPrice = maxPrice.copy(value = newMaxPrice, error = error)
            )
        }
    }

    fun updateMinArea(newMinArea: String) {
        val error = newMinArea.validatePositiveNumber()
        updateState {
            copy(
                minArea = minArea.copy(value = newMinArea, error = error)
            )
        }
    }

    fun updateMaxArea(newMaxArea: String) {
        val error = newMaxArea.validatePositiveNumber()
        updateState {
            copy(
                maxArea = maxArea.copy(value = newMaxArea, error = error)
            )
        }
    }

    fun updateMinNumberOfRooms(newMinNumberOfRooms: String) {
        val error = newMinNumberOfRooms.validatePositiveNumber()
        updateState {
            copy(
                minNumberOfRooms = minNumberOfRooms.copy(value = newMinNumberOfRooms, error = error)
            )
        }
    }

    fun updateMaxNumberOfRooms(newMaxNumberOfRooms: String) {
        val error = newMaxNumberOfRooms.validatePositiveNumber()
        updateState {
            copy(
                maxNumberOfRooms = maxNumberOfRooms.copy(value = newMaxNumberOfRooms, error = error)
            )
        }
    }

    fun updateMinPhotos(newMinPhotos: String) {
        val error = newMinPhotos.validatePositiveNumber()
        updateState {
            copy(
                minPhotos = minPhotos.copy(value = newMinPhotos, error = error)
            )
        }
    }

    fun updateMinVideos(newMinVideos: String) {
        val error = newMinVideos.validatePositiveNumber()
        updateState {
            copy(
                minVideos = minVideos.copy(value = newMinVideos, error = error)
            )
        }
    }

    fun updateEntryDate(newEntryDate: Long?) {
        updateState {
            copy(
                entryDate = newEntryDate
            )
        }
    }

    fun updateEntryDateDialogShown(newIsDialogShown: Boolean) {
        updateState {
            copy(
                isEntryDatePickerShown = newIsDialogShown
            )
        }
    }

    fun updateSaleDate(newSaleDate: Long?) {
        updateState {
            copy(
                saleDate = newSaleDate
            )
        }
    }

    fun updateSaleDateDialogShown(newIsDialogShown: Boolean) {
        updateState {
            copy(
                isSaleDatePickerShown = newIsDialogShown
            )
        }
    }

    fun updateTypeSelection(type: String, isSelected: Boolean) {
        updateState {
            val updatedTypes = if (isSelected) {
                typeSet + type
            } else {
                typeSet - type
            }
            copy(typeSet = updatedTypes)
        }
    }

    fun updatePointOfInterestSelection(poi: PointOfInterest, isSelected: Boolean) {
        updateState {
            val updatedNearbyPoints = if (isSelected) {
                nearbyPointSet + poi
            } else {
                nearbyPointSet - poi
            }
            copy(nearbyPointSet = updatedNearbyPoints)
        }
    }

    fun updateAgent(newAgent: Agent?) {
        updateState {
            copy(
                agent = newAgent
            )
        }
    }

    /**
     * Internal helper to update the [SearchPropertiesUiState.Editing] state atomically.
     * Automatically triggers [isFormValid] to refresh the search button's enabled status.
     *
     * @param update Lambda to modify the current editing state.
     */
    private fun updateState(update: SearchPropertiesUiState.Editing.() -> SearchPropertiesUiState.Editing) {
        _uiState.update { currentState ->
            if (currentState is SearchPropertiesUiState.Editing) {
                val newState = currentState.update()
                newState.copy(isFormValid = isFormValid(newState))
            } else {
                currentState
            }
        }
    }

    private fun handleError(exception: Exception) {
        _uiState.value = SearchPropertiesUiState.Error(
            exception.message ?: "An unexpected error occurred"
        )
    }

    /**
     * Validates if at least one search criterion has been entered and no field contains errors.
     *
     * @param state The current form state to validate.
     * @return True if the search can be executed.
     */
    private fun isFormValid(state: SearchPropertiesUiState.Editing): Boolean {
        val isNoError = listOf(
            state.minPrice.error,
            state.maxPrice.error,
            state.minPhotos.error,
            state.minVideos.error,
            state.minArea.error,
            state.maxArea.error,
            state.minNumberOfRooms.error,
            state.maxNumberOfRooms.error
        ).all { it.isNullOrBlank() }

        val isAnyFieldFilled = listOf(
            state.minPrice.value,
            state.maxPrice.value,
            state.minArea.value,
            state.maxArea.value,
            state.minNumberOfRooms.value,
            state.maxNumberOfRooms.value,
            state.minPhotos.value,
            state.minVideos.value,
            state.entryDate,
            state.saleDate,
            state.agent
        ).any { it != null && it.toString().isNotBlank() }

        val isAnySetFilled = state.typeSet.isNotEmpty() || state.nearbyPointSet.isNotEmpty()

        return isNoError && (isAnyFieldFilled || isAnySetFilled)
    }

    /**
     * Transforms the current form data into a [PropertySearchCriteria] object.
     *
     * @return A valid [PropertySearchCriteria] if the state is [SearchPropertiesUiState.Editing],
     * or null otherwise.
     */
    fun getCurrentCriteria(): PropertySearchCriteria? {
        val state = _uiState.value as? SearchPropertiesUiState.Editing ?: return null
        return PropertySearchCriteria(
            propertyType = state.typeSet.toList(),
            minPrice = state.minPrice.value.toDoubleOrNull(),
            maxPrice = state.maxPrice.value.toDoubleOrNull(),
            minArea = state.minArea.value.toDoubleOrNull(),
            maxArea = state.maxArea.value.toDoubleOrNull(),
            minNumberOfRooms = state.minNumberOfRooms.value.toIntOrNull(),
            maxNumberOfRooms = state.maxNumberOfRooms.value.toIntOrNull(),
            minPhotos = state.minPhotos.value.toIntOrNull(),
            minVideos = state.minVideos.value.toIntOrNull(),
            nearbyPointsOfInterest = state.nearbyPointSet.toList(),
            minEntryDate = state.entryDate,
            minSaleDate = state.saleDate,
            agentId = state.agent?.id
        )
    }
}