package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetLocationUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetPropertyByIdUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdatePropertyUseCase
import com.openclassrooms.realestatemanagerv2.ui.models.FormField
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyFormUiState
import com.openclassrooms.realestatemanagerv2.utils.convertFromLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.convertToLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.validateLength
import com.openclassrooms.realestatemanagerv2.utils.validateNonEmpty
import com.openclassrooms.realestatemanagerv2.utils.validatePositiveNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for the business logic of editing an existing real estate property. *
 * It manages the update flow by:
 * 1. Retrieving existing property data via [GetPropertyByIdUseCase] and [SavedStateHandle].
 * 2. Pre-filling the [PropertyFormUiState] with current values.
 * 3. Handling conditional geocoding (only if the address has changed).
 * 4. Persisting changes through [UpdatePropertyUseCase].
 *
 * @property updatePropertyUseCase Use case to persist modified property data.
 * @property getAllAgentsUseCase Use case to retrieve the list of agents for the spinner.
 * @property getPropertyByIdUseCase Use case to fetch the initial data of the property to edit.
 * @property getLocationUseCase Use case to resolve new addresses to geographic coordinates.
 * @property savedState Handle to retrieve the property ID passed through navigation.
 */
@HiltViewModel
class EditPropertyViewModel @Inject constructor
    (
    private val updatePropertyUseCase: UpdatePropertyUseCase,
    private val getAllAgentsUseCase: GetAllAgentsUseCase,
    private val getPropertyByIdUseCase: GetPropertyByIdUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val savedState: SavedStateHandle
) : ViewModel() {

    /**
     * Cache used to restore user input when returning from an Error state.
     */
    private var previousEditingState: PropertyFormUiState.Editing? = null

    /**
     * The original property data before any edits, used for address comparison.
     */
    private var initialProperty: Property? = null

    private val _uiState = MutableStateFlow<PropertyFormUiState>(PropertyFormUiState.Editing())

    val uiState: StateFlow<PropertyFormUiState> = _uiState
    val allPointOfInterestList: List<PointOfInterest> = PointOfInterest.entries

    init {
        val propertyId = savedState.get<String>("propertyId")
        viewModelScope.launch {
            try {
                if (propertyId != null) {
                    val property = getPropertyByIdUseCase(propertyId)

                    val agents = getAllAgentsUseCase()
                    Log.d("EditViewModel", "Collected agents: $agents")
                    val newState = PropertyFormUiState.Editing(
                        id = property.id,
                        description = FormField(value = property.description),
                        type = FormField(value = property.type),
                        price = FormField(
                            value = property.price.convertToLocalCurrency().toString()
                        ),
                        area = FormField(value = property.area.toString()),
                        numberOfRooms = FormField(value = property.numberOfRooms.toString()),
                        mediaLists = property.media,
                        videoUri = property.media.find { it is Video }?.mediaUrl ?: "",
                        address = FormField(value = property.address),
                        nearbyPointSet = property.nearbyPointsOfInterest.toSet(),
                        entryDate = property.entryDate,
                        saleDate = property.saleDate,
                        agent = property.agent,
                        agentList = agents,
                        isFormValid = false,
                    )
                    _uiState.value = newState.copy(isFormValid = isFormValid(newState))

                    savedState.remove<String>("propertyId")
                    initialProperty = property
                }

            } catch (exception: Exception) {
                Log.e("ViewModel", "Error collecting agents", exception)
                handleError(exception)
            }
        }
    }

    /**
     * Triggers the property update process.
     *
     * Performs an optimization: it only calls the geocoding service if the address
     * has been modified by the user compared to [initialProperty].
     */
    fun updateProperty() {
        val currentState = _uiState.value as? PropertyFormUiState.Editing ?: return
        if (isFormValid(currentState)) {
            previousEditingState = currentState
            viewModelScope.launch {
                _uiState.value = PropertyFormUiState.Loading

                val coordinates: LatLng?
                if (initialProperty?.address != currentState.address.value) {
                    coordinates = try {
                        getLocationUseCase(currentState.address.value)
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    coordinates = initialProperty?.let {
                        if (it.latitude != null && it.longitude != null) {
                            LatLng(it.latitude, it.longitude)
                        } else null
                    }
                }

                val newProperty = Property(
                    id = currentState.id ?: initialProperty?.id ?: throw IllegalStateException("Property ID is missing"),
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
                    saleDate = if (currentState.saleDate != null) currentState.saleDate
                    else null,
                    agent = currentState.agent!!
                )
                try {
                    val propertyId = newProperty.id
                    updatePropertyUseCase(newProperty)
                    //Pass the property id to the UI in order to navigate
                    _uiState.value = PropertyFormUiState.Success(propertyId)
                } catch (e: Exception) {
                    handleError(e)
                }
            }
        }

    }


    private fun handleError(exception: Exception) {
        _uiState.value = PropertyFormUiState.Error(
            exception.message ?: "An unexpected error occurred"
        )
    }

    fun deletePhoto(media: Media) {
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

    fun deleteVideo() {
        updateState {
            copy(videoUri = "")
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
     * Returns the UI to the last valid editing state using [previousEditingState].
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