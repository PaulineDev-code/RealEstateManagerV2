package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Video
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdatePropertyUseCase
import com.openclassrooms.realestatemanagerv2.ui.models.FormField
import com.openclassrooms.realestatemanagerv2.utils.validateLength
import com.openclassrooms.realestatemanagerv2.utils.validateNonEmpty
import com.openclassrooms.realestatemanagerv2.utils.validatePositiveNumber
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel.AddPropertyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPropertyViewModel @Inject constructor
    (
    private val updatePropertyUseCase: UpdatePropertyUseCase,
    private val getAllAgentsUseCase: GetAllAgentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditPropertyUiState>(EditPropertyUiState.Editing())
    val uiState: StateFlow<EditPropertyUiState> = _uiState


    init {
        viewModelScope.launch {
            try {
                val agents = getAllAgentsUseCase()
                Log.d("EditViewModel", "Collected agents: $agents")
                updateState {
                    copy(agentList = agents)
                }
            } catch (exception: Exception) {
                Log.e("ViewModel", "Error collecting agents", exception)
                handleError(exception)
            }
        }
    }


    private fun validatePropertyData(currentState: EditPropertyUiState.Editing): ValidationResult {
        // Ensure all required fields are filled
        if (currentState.type.value.isBlank() ||
            currentState.price.value.toDoubleOrNull() == null ||
            currentState.area.value.toDoubleOrNull() == null ||
            currentState.numberOfRooms.value.toIntOrNull() == null ||
            currentState.description.value.isBlank() ||
            currentState.mediaLists.isEmpty() ||
            currentState.address.value.isBlank() ||
            currentState.nearbyPointSet.toList().isEmpty() ||
            currentState.entryDate == null ||
            currentState.agent == null
        ) {
            return ValidationResult.Error(
                "Please fill all required fields and make sure lists are not empty"
            )
        }

        val newProperty = Property(
            id = currentState.id,
            type = currentState.type.value,
            price = currentState.price.value.toDouble(),
            area = currentState.area.value.toDouble(),
            numberOfRooms = currentState.numberOfRooms.value.toInt(),
            description = currentState.description.value,
            media = currentState.mediaLists,
            address = currentState.address.value,
            latitude = null,
            longitude = null,
            nearbyPointsOfInterest = currentState.nearbyPointSet.toList(),
            status = if (currentState.saleDate != null) PropertyStatus.Sold
            else PropertyStatus.Available,
            entryDate = requireNotNull(currentState.entryDate) { "Entry date cannot be null" },
            saleDate = if (currentState.saleDate != null) currentState.saleDate
            else null,
            agent = currentState.agent
        )

        return ValidationResult.Success(newProperty)
    }

    fun updateProperty() {
        val currentState = _uiState.value
        if (currentState is EditPropertyUiState.Editing) {
            when (val validationResult = validatePropertyData(currentState)) {
                is ValidationResult.Success -> {
                    viewModelScope.launch {
                        try {
                            val propertyId = validationResult.property.id

                            updatePropertyUseCase(validationResult.property)

                            //Pass the property id to the UI in order to navigate
                            _uiState.value = EditPropertyUiState.Success(propertyId)
                        } catch (e: Exception) {
                            handleError(e)
                        }
                    }
                }
                is ValidationResult.Error -> {
                    handleError(Exception(validationResult.errorMessage))
                }
            }
        }
    }

    private fun handleError(exception: Exception) {
        val currentState = _uiState.value as? EditPropertyUiState.Editing
        _uiState.value = EditPropertyUiState.Error(exception, currentState)
    }

    //ADD AND DELETE A PHOTO
    fun deletePhoto(media: Media) {
        updateState {
            copy(mediaLists = mediaLists.filterNot { it == media })
        }
    }

    fun addPhoto() {
        updateState {
            copy(mediaLists = mediaLists + Photo(photoUri, photoDescription),
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
            copy(videoUri = null)
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

    /*fun addNearbyPoint() {
        updateState {
            copy(nearbyPointList = nearbyPointList + nearbyPoint,
                nearbyPoint = ""
            )
        }
    }

    fun deleteNearbyPoint(point: String) {
        updateState {
            copy(nearbyPointList = nearbyPointList.filterNot { it == point })
        }
    }*/

    // UPDATE FIELDS
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
        val error = newDescription.validateNonEmpty() + newDescription.validateLength()
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
        val error = newPrice.validatePositiveNumber() + newPrice.validateNonEmpty()
        updateState {
            copy(
                price = price.copy(value = newPrice, error = error)
            )
        }
    }

    fun updateArea(newArea: String) {
        val error = newArea.validatePositiveNumber() + newArea.validateNonEmpty()
        updateState {
            copy(
                area = area.copy(value = newArea, error = error)
            )
        }
    }

    fun updateNumberOfRooms(newNumberOfRooms: String) {
        val error = newNumberOfRooms.validateNonEmpty() + newNumberOfRooms.validatePositiveNumber()
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

    fun updateEntryDate(newEntryDate: Long?)  {
        updateState {
            copy(
                entryDate = newEntryDate
            )
        }
    }

    fun updateEntryDateDialogShown(newIsDialogShown: Boolean)  {
        updateState {
            copy(
                isEntryDatePickerShown = newIsDialogShown
            )
        }
    }

    fun updateSaleDate(newSaleDate: Long?)  {
        updateState {
            copy(
                saleDate = newSaleDate
            )
        }
    }

    fun updateSaleDateDialogShown(newIsDialogShown: Boolean)  {
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

    private fun updateState(update: EditPropertyUiState.Editing.() -> EditPropertyUiState.Editing) {
        val currentState = _uiState.value
        if (currentState is EditPropertyUiState.Editing) {
            _uiState.value = currentState.update()
        }
    }

    fun returnToEditingState() {
        val currentState = _uiState.value
        if (currentState is EditPropertyUiState.Error && currentState.previousEditingState != null) {
            _uiState.value = currentState.previousEditingState
        }
    }

    sealed class ValidationResult {
        data class Success(val property: Property) : ValidationResult()
        data class Error(val errorMessage: String) : ValidationResult()
    }

    sealed interface EditPropertyUiState {
        /*data class Success(val properties: List<Property>): AddPropertyUiState()
        data class Error(val exception: Exception): AddPropertyUiState()*/
        data class Success(val propertyId: String) : EditPropertyUiState
        data class Error(val exception: Exception, val previousEditingState: Editing? = null): EditPropertyUiState
        data class Editing(

            val id: String = "",
            val description: FormField = FormField(),
            val type: FormField = FormField(),
            val price: FormField = FormField(),
            val area: FormField = FormField(),
            val numberOfRooms: FormField = FormField(),
            val photoUri: String = "",
            val photoDescription: String = "",
            val mediaLists: List<Media> = emptyList<Media>(),
            val videoUri: String? = null,
            val address: FormField = FormField(),
            val nearbyPointSet: Set<PointOfInterest> = emptySet(),
            val entryDate: Long? = null,
            val isEntryDatePickerShown: Boolean = false,
            val saleDate: Long? = null,
            val isSaleDatePickerShown: Boolean = false,
            val agent: Agent? = null,
            val agentList: List<Agent> = emptyList(),
            val isFormValid: Boolean = false

        ) : EditPropertyUiState {
            val photoList: List<Photo>
                get() = mediaLists.filterIsInstance<Photo>()

            val videoList: List<Video>
                get() = mediaLists.filterIsInstance<Video>()

        }
    }

}