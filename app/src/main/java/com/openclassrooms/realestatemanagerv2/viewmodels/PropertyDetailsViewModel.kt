package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyByIdUseCase
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the details of a specific property.
 *
 * It coordinates the fetching of property data and maintains the state of the
 * photo viewer (selected index and visibility).
 *
 * @property getPropertyByIdUseCase Use case to retrieve a single property by its unique ID.
 */
@HiltViewModel
class PropertyDetailsViewModel @Inject constructor
    (private val getPropertyByIdUseCase: GetPropertyByIdUseCase) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PropertyDetailsUiState>(PropertyDetailsUiState.Success(null))

    /**
     * Observable UI state representing the details of the property,
     * including loading and error states.
     */
    val uiState: StateFlow<PropertyDetailsUiState> = _uiState

    /**
     * Fetches a property by its ID and updates the UI state.
     *
     * To optimize performance, it skips the fetching process if the
     * requested property is already loaded in the current state.
     *
     * @param id The unique identifier of the property to retrieve.
     */
    fun getPropertyById(id: String) {
        val currentState = _uiState.value
        if (currentState is PropertyDetailsUiState.Success && currentState.property?.id == id) {
            return
        }
        viewModelScope.launch {
            _uiState.value = PropertyDetailsUiState.Loading
            try {
                val property = getPropertyByIdUseCase(id)
                Log.d("DetailsViewModel", "Collected property: $property")
                _uiState.value = PropertyDetailsUiState.Success(
                    property = property,
                    selectedPhotoIndex = 0,
                    isPhotoViewerShown = false
                )
            } catch (exception: Exception) {
                Log.e("DetailsViewModel", "Error collecting property by id", exception)
                _uiState.value = PropertyDetailsUiState.Error(
                    exception.message ?: "Unknown error"
                )
            }
        }
    }

    /**
     * Updates the index of the currently selected photo in the viewer.
     * @param newPhotoIndex The index of the photo to display.
     */
    fun updateSelectedPhotoIndex(newPhotoIndex: Int) {
        updateState {
            copy(selectedPhotoIndex = newPhotoIndex)
        }
    }

    /**
     * Toggles the visibility of the photo viewer overlay.
     * @param newIsPhotoViewerShown True to show the viewer, false to hide it.
     */
    fun updatePhotoViewerShown(newIsPhotoViewerShown: Boolean) {
        updateState {
            copy(isPhotoViewerShown = newIsPhotoViewerShown)
        }
    }

    /**
     * Atomically updates the [PropertyDetailsUiState.Success] state.
     * Ensures updates only occur when the UI is in a successful state.
     */
    private fun updateState(update: PropertyDetailsUiState.Success.() -> PropertyDetailsUiState.Success) {
        _uiState.update { currentState ->
            if (currentState is PropertyDetailsUiState.Success) {
                currentState.update()
            } else {
                currentState
            }
        }
    }
}