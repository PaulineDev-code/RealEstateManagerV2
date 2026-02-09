package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanagerv2.R
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
import com.openclassrooms.realestatemanagerv2.ui.BottomNavItem
import com.openclassrooms.realestatemanagerv2.ui.models.FormField
import com.openclassrooms.realestatemanagerv2.utils.validateLength
import com.openclassrooms.realestatemanagerv2.utils.validateNonEmpty
import com.openclassrooms.realestatemanagerv2.utils.validatePositiveNumber
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel.AddPropertyError
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel.AddPropertyUiState
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel.ValidationResult
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel.PropertyDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPropertyViewModel @Inject constructor
    (
    private val updatePropertyUseCase: UpdatePropertyUseCase,
    private val getAllAgentsUseCase: GetAllAgentsUseCase,
    private val getPropertyByIdUseCase: GetPropertyByIdUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val savedState: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditPropertyUiState>(EditPropertyUiState.Editing())
    private var previousEditingState: EditPropertyUiState.Editing? = null

    val uiState: StateFlow<EditPropertyUiState> = _uiState
    val allPointOfInterestList: List<PointOfInterest> = PointOfInterest.entries

    init {
        val propertyId = savedState.get<String>("propertyId")
        viewModelScope.launch {
            try {
                if( propertyId != null) {
                    val property = getPropertyByIdUseCase(propertyId)

                    val agents = getAllAgentsUseCase()
                    Log.d("EditViewModel", "Collected agents: $agents")
                    _uiState.value = EditPropertyUiState.Editing(
                        id = property.id,
                        description = FormField(value = property.description),
                        type = FormField(value = property.type),
                        price = FormField(value = property.price.toString()),
                        area = FormField(value = property.area.toString()),
                        numberOfRooms = FormField(value = property.numberOfRooms.toString()),
                        mediaLists = property.media,
                        videoUri = property.media.find { it is Video }?.mediaUrl,
                        address = FormField(value = property.address),
                        nearbyPointSet = property.nearbyPointsOfInterest.toSet(),
                        entryDate = property.entryDate,
                        saleDate = property.saleDate,
                        agent = property.agent,
                        agentList = agents,
                        isFormValid = false,
                    )

                    savedState.remove<String>("propertyId")
                }

            } catch (exception: Exception) {
                Log.e("ViewModel", "Error collecting agents", exception)
                handleError(exception)
            }
        }
    }

    private fun validatePropertyData(currentState: EditPropertyUiState.Editing): ValidationResult {
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


            ValidationResult.Success()
        } else {
            ValidationResult.Error(
                Exception
                    ("Invalid property data")
            )
        }
    }

    fun updateProperty() {
        val currentState = _uiState.value
        if (currentState is EditPropertyUiState.Editing) {
            when (val validationResult = validatePropertyData(currentState)) {
                is ValidationResult.Success -> {
                    viewModelScope.launch {
                        val coordinates: LatLng? = try {
                            getLocationUseCase(currentState.address.value)
                        }catch (e: Exception){
                            null
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
                            _uiState.value = EditPropertyUiState.Success(propertyId)
                        } catch (e: Exception) {
                            handleError(e)
                        }
                    }
                }
                is ValidationResult.Error -> {
                    handleError(Exception(validationResult.exception))
                }
            }
        }
    }

    private fun handleError(exception: Exception) {
        val currentState = _uiState.value as? EditPropertyUiState.Editing
        _uiState.value = EditPropertyUiState.Error(EditPropertyError.GeneralError(exception))
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
        val error = newNumberOfRooms.validateNonEmpty() + " " + newNumberOfRooms.validatePositiveNumber()
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
            previousEditingState = currentState
            val newState = currentState.update()
            _uiState.value = newState.copy(
                isFormValid = isFormValid(newState))
        }
    }

    fun returnToEditingState() {
        val currentState = _uiState.value
        if (currentState is EditPropertyUiState.Error && previousEditingState != null) {
            _uiState.value = previousEditingState as EditPropertyUiState.Editing
        }
    }

    private fun isFormValid(state: EditPropertyUiState.Editing): Boolean {
        return listOf(
            state.description.error,
            state.type.error,
            state.price.error,
            state.address.error,
            state.area.error,
            state.numberOfRooms.error,
        ).all { it.isNullOrBlank() && state.agent != null && state.mediaLists.filterIsInstance<Photo>().isNotEmpty()
                && state.nearbyPointSet.isNotEmpty() && state.entryDate != null}
    }

    sealed class EditPropertyError {
        data class FieldError(val fieldId: Int) : EditPropertyError()
        data class GeneralError(val exception: Throwable) : EditPropertyError()
    }

    sealed class ValidationResult {
        class Success() : ValidationResult()
        data class Error(val exception: Exception) : ValidationResult()
    }

    sealed interface EditPropertyUiState {
        /*data class Success(val properties: List<Property>): AddPropertyUiState()
        data class Error(val exception: Exception): AddPropertyUiState()*/
        data class Success(val propertyId: String) : EditPropertyUiState
        data class Error(val error: EditPropertyError?) : EditPropertyUiState
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