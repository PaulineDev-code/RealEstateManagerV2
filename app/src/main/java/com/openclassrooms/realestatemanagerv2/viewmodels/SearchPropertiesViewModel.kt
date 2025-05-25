package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyTypesUseCase
import com.openclassrooms.realestatemanagerv2.ui.models.FormField
import com.openclassrooms.realestatemanagerv2.utils.validateNonEmpty
import com.openclassrooms.realestatemanagerv2.utils.validatePositiveNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchPropertiesViewModel @Inject constructor(
    private val getAllAgentsUseCase: GetAllAgentsUseCase,
    private val getPropertyTypesUseCase: GetPropertyTypesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchPropertiesUiState>(SearchPropertiesUiState.Editing())
    val uiState: StateFlow<SearchPropertiesUiState> = _uiState
    val allPointOfInterestList: List<PointOfInterest> = PointOfInterest.values().toList()

    init {
        //retrieve all agents
        viewModelScope.launch {
            try {
                getAllAgentsUseCase().collect() { agents ->
                    Log.d("SearchViewModel", "Collected agents: $agents")
                    updateState {
                        copy(agentList = agents)
                    }
                }

            } catch (exception: Exception) {
                Log.e("SearchViewModel", "Error collecting agents", exception)
                /*handleError(AddPropertyError.GeneralError(exception))}*/
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
                minNumberOfRooms =  minNumberOfRooms.copy(value = newMinNumberOfRooms, error = error)
            )
        }
    }

    fun updateMaxNumberOfRooms(newMaxNumberOfRooms: String) {
            val error = newMaxNumberOfRooms.validatePositiveNumber()
        updateState {
            copy(
                maxNumberOfRooms =  maxNumberOfRooms.copy(value = newMaxNumberOfRooms, error = error)
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

    fun updateEntryDate(newEntryDate: Long?)  {
        /*val error = newMinVideos.validatePositiveNumber() + newMinVideos.validateNonEmpty()*/
        updateState {
            copy(
                entryDate = newEntryDate/*, error = error)*/
            )
        }
    }

    fun updateEntryDateDialogShown(newIsDialogShown: Boolean)  {
        /*val error = newMinVideos.validatePositiveNumber() + newMinVideos.validateNonEmpty()*/
        updateState {
            copy(
                isEntryDatePickerShown = newIsDialogShown/*, error = error)*/
            )
        }
    }

    fun updateSaleDate(newSaleDate: Long?)  {
        /*val error = newMinVideos.validatePositiveNumber() + newMinVideos.validateNonEmpty()*/
        updateState {
            copy(
                saleDate = newSaleDate/*, error = error)*/
            )
        }
    }

    fun updateSaleDateDialogShown(newIsDialogShown: Boolean)  {
        /*val error = newMinVideos.validatePositiveNumber() + newMinVideos.validateNonEmpty()*/
        updateState {
            copy(
                isSaleDatePickerShown = newIsDialogShown/*, error = error)*/
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



    private fun updateState(update: SearchPropertiesUiState.Editing.() -> SearchPropertiesUiState.Editing) {
        val currentState = _uiState.value
        if (currentState is SearchPropertiesUiState.Editing) {
            val newState = currentState.update()
            _uiState.value = newState.copy(
                isFormValid = isFormValid(newState))
        }
    }

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

    fun getCurrentCriteria(): PropertySearchCriteria {
        val state = (_uiState.value as SearchPropertiesUiState.Editing)
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

    sealed class SearchPropertiesError {
        data class FieldError(val fieldId: Int) : SearchPropertiesError()
        data class GeneralError(val exception: Throwable) : SearchPropertiesError()
    }

    sealed interface SearchPropertiesUiState {
        data class Success(val properties: List<Property>): SearchPropertiesUiState
        data class Error(val error: SearchPropertiesViewModel.SearchPropertiesError): SearchPropertiesUiState
        data class Editing(
            val typeSet: Set<String> = emptySet(),
            val allTypes: List<String> = emptyList(),
            val minPrice: FormField = FormField(),
            val maxPrice: FormField = FormField(),
            val minArea: FormField = FormField(),
            val maxArea: FormField = FormField(),
            val minNumberOfRooms: FormField = FormField(),
            val maxNumberOfRooms: FormField = FormField(),
            val minPhotos: FormField = FormField(),
            val minVideos: FormField = FormField(),
            val nearbyPointSet: Set<PointOfInterest> = emptySet(),
            val entryDate: Long? = null,
            val isEntryDatePickerShown: Boolean = false,
            val saleDate: Long? = null,
            val isSaleDatePickerShown: Boolean = false,
            val agent: Agent? = null,
            val agentList: List<Agent> = emptyList(),
            val isFormValid: Boolean = false): SearchPropertiesUiState {}

    }

}

