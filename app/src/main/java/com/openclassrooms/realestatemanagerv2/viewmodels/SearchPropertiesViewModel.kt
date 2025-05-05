package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyTypesUseCase
import com.openclassrooms.realestatemanagerv2.utils.validateNonEmpty
import com.openclassrooms.realestatemanagerv2.utils.validatePositiveNumber
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel.FormField
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

    fun updateAnimateText(newIsAnimated: Boolean)  {
        updateState {
            copy(
                animateHeader = newIsAnimated
            )
        }
    }

    fun updateMinPrice(newMinPrice: String) {
        val error = newMinPrice.validatePositiveNumber() + newMinPrice.validateNonEmpty()
        updateState {
            copy(
                minPrice = minPrice.copy(value = newMinPrice/*, error = error*/)
            )
        }
    }

    fun updateMaxPrice(newMaxPrice: String) {
        val error = newMaxPrice.validatePositiveNumber() + newMaxPrice.validateNonEmpty()
        updateState {
            copy(
                maxPrice = maxPrice.copy(value = newMaxPrice/*, error = error*/)
            )
        }
    }

    fun updateAreaRange(newAreaRange: ClosedFloatingPointRange<Float>) {
/*
    val error = newAreaRange.validatePositiveNumber() + newAreaRange.validateNonEmpty()
*/
        updateState {
            copy(
                areaRange =  newAreaRange/*, error = error*/)
        }
    }

    fun updateNumberOfRooms(newNumberOfRoms: Float) {
        /*
            val error = newAreaRange.validatePositiveNumber() + newAreaRange.validateNonEmpty()
        */
        updateState {
            copy(
                numberOfRooms =  newNumberOfRoms/*, error = error*/)
        }
    }

    fun updateMinPhotos(newMinPhotos: String) {
        val error = newMinPhotos.validatePositiveNumber() + newMinPhotos.validateNonEmpty()
        updateState {
            copy(
                minPhotos = minPhotos.copy(value = newMinPhotos/*, error = error*/)
            )
        }
    }

    fun updateMinVideos(newMinVideos: String) {
        val error = newMinVideos.validatePositiveNumber() + newMinVideos.validateNonEmpty()
        updateState {
            copy(
                minVideos = minVideos.copy(value = newMinVideos/*, error = error*/)
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
        return listOf(
            /*state.type.validateNonEmpty(),*/
            state.address.error,
            /*state.area.error,
            state.numberOfRooms.error,
            state.entryDate.error,*/
        ).all { it.isNullOrBlank() && state.agent != null
                && state.nearbyPointSet.isNotEmpty()}
    }

    sealed class SearchPropertiesError {
        data class FieldError(val fieldId: Int) : SearchPropertiesError()
        data class GeneralError(val exception: Throwable) : SearchPropertiesError()
    }

    sealed interface SearchPropertiesUiState {
        data class Success(val properties: List<Property>): SearchPropertiesUiState
        data class Error(val error: SearchPropertiesViewModel.SearchPropertiesError): SearchPropertiesUiState
        data class Editing(
            val animateHeader: Boolean = true,
            val typeSet: Set<String> = emptySet(),
            val allTypes: List<String> = emptyList(),
            val minPrice: FormField = FormField(),
            val maxPrice: FormField = FormField(),
            val areaRange: ClosedFloatingPointRange<Float> = 30f.rangeTo(1000f),
            val numberOfRooms: Float = 0f,
            val minPhotos: FormField = FormField(),
            val minVideos: FormField = FormField(),
            val address: FormField = FormField(),
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

