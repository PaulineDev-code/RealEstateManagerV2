package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Video
import com.openclassrooms.realestatemanagerv2.domain.usecases.AddPropertyUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllAgentsUseCase
import com.openclassrooms.realestatemanagerv2.ui.AddScreenUiAction
import com.openclassrooms.realestatemanagerv2.utils.validateLength
import com.openclassrooms.realestatemanagerv2.utils.validateNonEmpty
import com.openclassrooms.realestatemanagerv2.utils.validatePositiveNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddPropertyViewModel @Inject constructor
    (
    private val addPropertyUseCase: AddPropertyUseCase,
    private val getAllAgentsUseCase: GetAllAgentsUseCase) : ViewModel() {

    private var previousEditingState: AddPropertyUiState.Editing? = null
    private val _uiState = MutableStateFlow<AddPropertyUiState>(AddPropertyUiState.Editing())
    val uiState: StateFlow<AddPropertyUiState> = _uiState
    val allPointOfInterestList: List<PointOfInterest> = PointOfInterest.values().toList()



    init {
        viewModelScope.launch {
            try {
                //Make allAgentList out of Editing state ? Keep useCase here and expose val through viewmodel?
                //Error management ok
                //Interactor to move out logic?
                getAllAgentsUseCase().collect() { agents ->
                    Log.d("AddViewModel", "Collected agents: $agents")
                    updateState {
                        copy(agentList = agents)
                    }
                }
            } catch (exception: Exception) {
                Log.e("ViewModel", "Error collecting agents", exception)
                handleError(AddPropertyError.GeneralError(exception))}
        }

    }


    private fun validatePropertyData(currentState: AddPropertyUiState.Editing): ValidationResult {
        // Ensure all required fields are filled
        val invalidFields = mutableListOf<Int>()

        if (currentState.type.value.isBlank()) {
            invalidFields.add(R.string.type)
        }

        if (currentState.price.value.toDoubleOrNull() == null) {
            invalidFields.add(R.string.price)
        }

        if (currentState.area.value.toDoubleOrNull() == null) {
            invalidFields.add(R.string.area)
        }

        if (currentState.numberOfRooms.value.toIntOrNull() == null) {
            invalidFields.add(R.string.number_of_rooms)
        }

        if (currentState.description.value.isBlank()) {
            invalidFields.add(R.string.description)
        }

        if (currentState.mediaLists.isEmpty()) {
            invalidFields.add(R.string.media)
        }

        if (currentState.address.value.isBlank()) {
            invalidFields.add(R.string.location)
        }

        if (currentState.nearbyPointSet.isEmpty()) {
            invalidFields.add(R.string.nearby_points_of_interest)
        }

        if (currentState.entryDate == null) {
            invalidFields.add(R.string.entry_date)
        }

        if (currentState.agent == null) {
            invalidFields.add(R.string.agent)
        }
        return if (invalidFields.isEmpty()) {


        val newProperty = Property(
            id = UUID.randomUUID().toString(),
            type = currentState.type.value,
            price = currentState.price.value.toDouble(),
            area = currentState.area.value.toDouble(),
            numberOfRooms = currentState.numberOfRooms.value.toInt(),
            description = currentState.description.value,
            media = currentState.mediaLists,
            address = currentState.address.value,
            nearbyPointsOfInterest = currentState.nearbyPointSet.toList(),
            status = if (currentState.saleDate != null) PropertyStatus.Sold
                        else PropertyStatus.Available,
            entryDate = requireNotNull(currentState.entryDate) { "Entry date cannot be null" },
            saleDate = if (currentState.saleDate != null) currentState.saleDate
                        else null,
            agent = currentState.agent!!
        )
            ValidationResult.Success(newProperty)
        } else {
            ValidationResult.Error(
                    Exception
                        ("Invalid property data")
            )
        }
    }

    fun createProperty() {
        val currentState = _uiState.value
        if (currentState is AddPropertyUiState.Editing) {
            when (val validationResult = validatePropertyData(currentState)) {
                is ValidationResult.Success -> {
                    viewModelScope.launch {
                        try {
                            val propertyId = validationResult.property.id

                            addPropertyUseCase(validationResult.property)

                            //Pass the property id to the UI in order to navigate
                            _uiState.value = AddPropertyUiState.Success(propertyId)
                        } catch (exception: Exception) {
                            handleError(AddPropertyError.GeneralError(exception))
                        }
                    }
                }
                is ValidationResult.Error -> {
                    handleError(AddPropertyError.GeneralError(validationResult.error))
                }
            }
        }
    }

    fun handleAction(action: AddScreenUiAction) {
        when(action) {
            is AddScreenUiAction.OnCreatePropertyClick -> { createProperty() }
            is AddScreenUiAction.OnPhotoDeleteClick -> { deleteMedia(action.mediaToRemove) }
            is AddScreenUiAction.OnAddPhotoDescriptionClick -> {}
            is AddScreenUiAction.OnVideoDeleteClick -> {}
            is AddScreenUiAction.OnAddNearbyPoint -> {}
            is AddScreenUiAction.OnDeleteNearbyPoint -> {}
            is AddScreenUiAction.OnPhotoUriChange -> {}
            is AddScreenUiAction.OnPhotoDescriptionChange -> {}
            is AddScreenUiAction.OnVideoUriChange -> {}
            is AddScreenUiAction.OnDescriptionChange -> {}
            is AddScreenUiAction.OnTypeChange -> {}
            is AddScreenUiAction.OnAreaChange -> {}
            is AddScreenUiAction.OnPriceChange -> {}
            is AddScreenUiAction.OnNumberOfRoomsChange -> {}
            is AddScreenUiAction.OnAddressChange -> {}
            is AddScreenUiAction.OnPointOfInterestSelectionChange -> {
                updatePointOfInterestSelection(action.pointOfInterest, action.isSelected)}
            is AddScreenUiAction.OnEntryDateChange -> {}
            is AddScreenUiAction.OnSaleDateChange -> {}
            is AddScreenUiAction.OnAgentSelected -> {}

        }
    }

    private fun handleError(error: AddPropertyError) {
/*
        val currentState = _uiState.value as? AddPropertyUiState.Editing
*/
        _uiState.value = AddPropertyUiState.Error(error)
    }

    //ADD AND DELETE A PHOTO
    fun deleteMedia(media: Media) {
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

    /*fun addNearbyPoint() {
        updateState {
            copy(nearbyPointList = nearbyPointList + nearbyPoint,
                nearbyPoint = ""
            )
        }
    }*/

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

    /*fun deleteNearbyPoint(point: PointOfInterest) {
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

    /*fun updateNearbyPoint(newPoint: String) {
        updateState {
            copy(nearbyPoint = newPoint)
        }
    }*/

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

    private fun updateState(update: AddPropertyUiState.Editing.() -> AddPropertyUiState.Editing) {
        val currentState = _uiState.value
        if (currentState is AddPropertyUiState.Editing) {
            previousEditingState = currentState
            val newState = currentState.update()
            _uiState.value = newState.copy(
                isFormValid = isFormValid(newState))
        }
    }

    fun returnToEditingState() {
        val currentState = _uiState.value
        if (currentState is AddPropertyUiState.Error && previousEditingState != null) {
            _uiState.value = previousEditingState as AddPropertyUiState.Editing
        }
    }

    sealed class AddPropertyError {
        data class FieldError(val fieldId: Int) : AddPropertyError()
        data class GeneralError(val exception: Throwable) : AddPropertyError()
    }

    sealed class ValidationResult {
        data class Success(val property: Property) : ValidationResult()
        data class Error(val error: Exception) : ValidationResult()
    }

    data class FormField(
        val value: String = "",
        val error: String? = ""
    )
    private fun isFormValid(state: AddPropertyUiState.Editing): Boolean {
        return listOf(
            state.description.error,
            state.type.error,
            state.price.error,
            state.address.error,
            state.area.error,
            state.numberOfRooms.error,
        ).all { it.isNullOrBlank() && state.agent != null && state.mediaLists.isNotEmpty()
                && state.nearbyPointSet.isNotEmpty() && state.entryDate != null}
    }






    /*data class Error(
        override val errors: List<AddPropertyError>
    ) : AddPropertyUiState*/

    sealed interface AddPropertyUiState {
        /*data class Success(val properties: List<Property>): AddPropertyUiState()
        data class Error(val exception: Exception): AddPropertyUiState()*/

        data class Success(val propertyId: String) : AddPropertyUiState
        data class Error(val error: AddPropertyError?) : AddPropertyUiState
        data class Editing(
            val description: FormField = FormField(),
            val type: FormField = FormField(),
            val price: FormField = FormField(),
            val area: FormField = FormField(),
            val numberOfRooms: FormField = FormField(),
            val photoUri: String = "",
            val videoUri: String = "",
            val photoDescription: String = "",
            val mediaLists: List<Media> = emptyList<Media>(),
            val address: FormField = FormField(),
            val nearbyPointSet: Set<PointOfInterest> = emptySet(),
            val entryDate: Long? = null,
            val isEntryDatePickerShown: Boolean = false,
            val saleDate: Long? = null,
            val isSaleDatePickerShown: Boolean = false,
            val agent: Agent? = null,
            val agentList: List<Agent> = emptyList(),
            val isFormValid: Boolean = false
/*
            val errors: List<AddPropertyError>? = null,
*/


        ) : AddPropertyUiState {
            val photoList: List<Photo>
                get() = mediaLists.filterIsInstance<Photo>()

            val videoList: List<Video>
                get() = mediaLists.filterIsInstance<Video>()

        }
    }

}





