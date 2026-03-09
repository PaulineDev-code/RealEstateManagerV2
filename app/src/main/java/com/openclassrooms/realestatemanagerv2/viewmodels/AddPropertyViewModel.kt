package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Video
import com.openclassrooms.realestatemanagerv2.domain.usecases.AddPropertyUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetLocationUseCase
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyFormUiState
import com.openclassrooms.realestatemanagerv2.utils.convertFromLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.validateLength
import com.openclassrooms.realestatemanagerv2.utils.validateNonEmpty
import com.openclassrooms.realestatemanagerv2.utils.validatePositiveNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel responsible for the business logic of adding a new real estate property.
 *
 * It manages the property creation flow using a reactive [PropertyFormUiState].
 * Key responsibilities include:
 * 1. Fetching the list of available real estate agents.
 * 2. Handling real-time form validation for various property fields (price, area, etc.).
 * 3. Converting physical addresses into geographic coordinates (Geocoding).
 * 4. Managing temporary media lists (Photos and Videos) before persistence.
 * 5. Persisting the finalized [Property] object via specialized Use Cases.
 *
 * @property addPropertyUseCase The use case used to persist a new property in the data source.
 * @property getAllAgentsUseCase The use case used to retrieve all registered agents.
 * @property getLocationUseCase The use case used to fetch [LatLng] coordinates from a string address.
 */
@HiltViewModel
class AddPropertyViewModel @Inject constructor
    (
    private val addPropertyUseCase: AddPropertyUseCase,
    private val getAllAgentsUseCase: GetAllAgentsUseCase,
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    /**
     * Cache used to restore user input when returning from an Error state.
     */
    private var previousEditingState: PropertyFormUiState.Editing? = null

    private val _uiState = MutableStateFlow<PropertyFormUiState>(PropertyFormUiState.Editing())
    val uiState: StateFlow<PropertyFormUiState> = _uiState
    val allPointOfInterestList: List<PointOfInterest> = PointOfInterest.entries


    init {
        viewModelScope.launch {
            try {
                val agents = getAllAgentsUseCase()
                Log.d("AddViewModel", "Collected agents: $agents")
                updateState {
                    copy(agentList = agents)
                }

            } catch (exception: Exception) {
                Log.e("ViewModel", "Error collecting agents", exception)
                handleError(exception)
            }
        }
    }

    /**
     * Validates form data, performs geocoding, and persists the property.
     * Transitions state to [PropertyFormUiState.Success] upon completion.
     */
    fun createProperty() {
        val currentState = _uiState.value as? PropertyFormUiState.Editing ?: return
        if (isFormValid(currentState)) {
            previousEditingState = currentState

            viewModelScope.launch {
                _uiState.value = PropertyFormUiState.Loading

                val coordinates: LatLng? = try {
                    getLocationUseCase(currentState.address.value)
                } catch (e: Exception) {
                    null
                }

                try {
                    val newProperty = Property(
                        id = UUID.randomUUID().toString(),
                        type = currentState.type.value,
                        price = currentState.price.value.toDouble().convertFromLocalCurrency(),
                        area = currentState.area.value.toDouble(),
                        numberOfRooms = currentState.numberOfRooms.value.toInt(),
                        description = currentState.description.value,
                        media = currentState.mediaLists,
                        address = currentState.address.value,
                        latitude = coordinates?.latitude,
                        longitude = coordinates?.longitude,
                        nearbyPointsOfInterest = currentState.nearbyPointSet.toList(),
                        status = if (currentState.saleDate != null) PropertyStatus.Sold
                        else PropertyStatus.Available,
                        entryDate = requireNotNull(currentState.entryDate) { "Entry date cannot be null" },
                        saleDate = currentState.saleDate,
                        agent = requireNotNull(currentState.agent) { "Agent must be selected" }
                    )

                    addPropertyUseCase(newProperty)
                    _uiState.value = PropertyFormUiState.Success(newProperty.id)
                } catch (exception: Exception) {
                    handleError((exception))
                }
            }
        }
    }

    private fun handleError(exception: Exception) {
        _uiState.value = PropertyFormUiState.Error(
            exception.message ?: "An unexpected error occurred"
        )
    }


    fun deleteMedia(media: Media) {
        updateState {
            copy(mediaLists = mediaLists.filterNot { it == media })
        }
    }

    fun addPhoto() {
        updateState {
            copy(
                mediaLists = mediaLists + Photo(photoUri, photoDescription),
                photoUri = "",
                photoDescription = ""
            )
        }
    }

    fun addVideo(videoUri: String, videoDescription: String = "") {
        updateState {
            copy(
                mediaLists = mediaLists + Video(videoUri, videoDescription)
            )
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

    fun updatePhotoUri(photoUri: String) {
        updateState {
            copy(photoUri = photoUri)
        }
    }

    fun updatePhotoDescription(photoDescription: String) {
        updateState {
            copy(photoDescription = photoDescription)
        }
    }

    fun updateDescription(newDescription: String) {
        val error = newDescription.validateNonEmpty() + " " + newDescription.validateLength()
        updateState {
            copy(
                description = description.copy(value = newDescription, error = error)
            )
        }
    }

    fun updateType(newType: String) {
        val error = newType.validateNonEmpty()
        updateState {
            copy(
                type = type.copy(value = newType, error = error)
            )
        }
    }

    fun updatePrice(newPrice: String) {
        val error = newPrice.validatePositiveNumber() + " " + newPrice.validateNonEmpty()
        updateState {
            copy(
                price = price.copy(value = newPrice, error = error)
            )
        }
    }

    fun updateArea(newArea: String) {
        val error = newArea.validatePositiveNumber() + " " + newArea.validateNonEmpty()
        updateState {
            copy(
                area = area.copy(value = newArea, error = error)
            )
        }
    }

    fun updateNumberOfRooms(newNumberOfRooms: String) {
        val error =
            newNumberOfRooms.validateNonEmpty() + " " + newNumberOfRooms.validatePositiveNumber()
        updateState {
            copy(numberOfRooms = numberOfRooms.copy(value = newNumberOfRooms, error = error))
        }
    }

    fun updateVideoUri(newVideoUri: String) {
        updateState {
            copy(videoUri = newVideoUri)
        }
    }

    fun updateAddress(newAddress: String) {
        val error = newAddress.validateNonEmpty()
        updateState {
            copy(address = address.copy(value = newAddress, error = error))
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

    fun updateAgent(selectedAgent: Agent) {
        updateState {
            copy(agent = selectedAgent)
        }
    }

    /**
     * Atomically updates the editing state and refreshes form validity.
     */
    private fun updateState(update: PropertyFormUiState.Editing.() -> PropertyFormUiState.Editing) {
        _uiState.update { currentState ->
            if (currentState is PropertyFormUiState.Editing) {
                previousEditingState = currentState
                val newState = currentState.update()
                newState.copy(
                    isFormValid = isFormValid(newState)
                )
            } else {
                currentState
            }
        }
    }

    /**
     * Returns the UI to the last valid editing state.
     */
    fun returnToEditingState() {
        val currentState = _uiState.value
        if (currentState is PropertyFormUiState.Error && previousEditingState != null) {
            _uiState.value = previousEditingState as PropertyFormUiState.Editing
        }
    }

    /**
     * Business logic for form validation (checks both field errors and mandatory data).
     */
    private fun isFormValid(state: PropertyFormUiState.Editing): Boolean {
        val hasNoErrors = listOf(
            state.description.error,
            state.type.error,
            state.price.error,
            state.address.error,
            state.area.error,
            state.numberOfRooms.error,
        ).all { it.isNullOrBlank() }

        val hasRequiredFields = state.agent != null &&
                state.mediaLists.isNotEmpty() &&
                state.nearbyPointSet.isNotEmpty() &&
                state.entryDate != null

        return hasNoErrors && hasRequiredFields
    }
}