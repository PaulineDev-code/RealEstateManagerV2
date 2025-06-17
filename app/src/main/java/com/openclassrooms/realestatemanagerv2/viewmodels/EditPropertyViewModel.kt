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
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdatePropertyUseCase
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

    private val _uiState = MutableStateFlow<UpdatePropertyUiState>(UpdatePropertyUiState.Editing())
    val uiState: StateFlow<UpdatePropertyUiState> = _uiState


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


    private fun validatePropertyData(currentState: UpdatePropertyUiState.Editing): ValidationResult {
        // Ensure all required fields are filled
        if (currentState.type.isBlank() ||
            currentState.price.toDoubleOrNull() == null ||
            currentState.area.toDoubleOrNull() == null ||
            currentState.numberOfRooms.toIntOrNull() == null ||
            currentState.description.isBlank() ||
            currentState.mediaLists.isEmpty() ||
            currentState.address.isBlank() ||
            currentState.nearbyPointList.isEmpty() ||
            currentState.entryDate == null ||
            currentState.agent == null
        ) {
            return ValidationResult.Error(
                "Please fill all required fields and make sure lists are not empty"
            )
        }

        val newProperty = Property(
            id = currentState.id,
            type = currentState.type,
            price = currentState.price.toDouble(),
            area = currentState.area.toDouble(),
            numberOfRooms = currentState.numberOfRooms.toInt(),
            description = currentState.description,
            media = currentState.mediaLists,
            address = currentState.address,
            latitude = null,
            longitude = null,
            nearbyPointsOfInterest = currentState.nearbyPointList,
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
        if (currentState is UpdatePropertyUiState.Editing) {
            when (val validationResult = validatePropertyData(currentState)) {
                is ValidationResult.Success -> {
                    viewModelScope.launch {
                        try {
                            val propertyId = validationResult.property.id

                            updatePropertyUseCase(validationResult.property)

                            //Pass the property id to the UI in order to navigate
                            _uiState.value = UpdatePropertyUiState.Success(propertyId)
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
        val currentState = _uiState.value as? UpdatePropertyUiState.Editing
        _uiState.value = UpdatePropertyUiState.Error(exception, currentState)
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

    fun deleteVideo() {
        updateState {
            copy(videoUri = null)
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
        updateState {
            copy(description = newDescription)
        }
    }

    fun updateType(newType: String) {
        updateState {
            copy(type = newType)
        }
    }

    fun updatePrice(newPrice: String) {
        updateState {
            copy(price =  newPrice)
        }
    }

    fun updateArea(newArea: String) {
        updateState {
            copy(area = newArea)
        }
    }

    fun updateNumberOfRooms(newNumberOfRooms: String) {
        updateState {
            copy(numberOfRooms = newNumberOfRooms)
        }
    }

    fun updateVideoUri(newVideoUri: String) {
        updateState {
            copy(videoUri = newVideoUri)
        }
    }

    fun updateAddress(newAddress: String) {
        updateState {
            copy(address = newAddress)
        }
    }

    fun updateNearbyPoint(newPoint: String) {
        updateState {
            copy(nearbyPoint = newPoint)
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

    private fun updateState(update: UpdatePropertyUiState.Editing.() -> UpdatePropertyUiState.Editing) {
        val currentState = _uiState.value
        if (currentState is UpdatePropertyUiState.Editing) {
            _uiState.value = currentState.update()
        }
    }

    fun returnToEditingState() {
        val currentState = _uiState.value
        if (currentState is UpdatePropertyUiState.Error && currentState.previousEditingState != null) {
            _uiState.value = currentState.previousEditingState
        }
    }

    sealed class ValidationResult {
        data class Success(val property: Property) : ValidationResult()
        data class Error(val errorMessage: String) : ValidationResult()
    }

    sealed class UpdatePropertyUiState {
        /*data class Success(val properties: List<Property>): AddPropertyUiState()
        data class Error(val exception: Exception): AddPropertyUiState()*/
        object Initial : UpdatePropertyUiState()
        data class Success(val propertyId: String) : UpdatePropertyUiState()
        data class Error(val exception: Exception, val previousEditingState: Editing? = null): UpdatePropertyUiState()
        data class Editing(

            val id: String = "",
            val description: String = "",
            val type: String = "",
            val price: String = "",
            val area: String = "",
            val numberOfRooms: String = "",
            val photoUri: String = "",
            val photoDescription: String = "",
            val mediaLists: List<Media> = emptyList<Media>(),
            val videoUri: String? = null,
            val address: String = "",
            val nearbyPoint: String = "",
            val nearbyPointList: List<PointOfInterest> = emptyList(),
            val entryDate: Long? = null,
            val isEntryDatePickerShown: Boolean = false,
            val saleDate: Long? = null,
            val isSaleDatePickerShown: Boolean = false,
            val agent: Agent? = null,
            val agentList: List<Agent> = emptyList()

        ) : UpdatePropertyUiState()
    }

}