package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllPropertiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyListViewModel @Inject constructor
    (private val getAllPropertiesUseCase: GetAllPropertiesUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<PropertyUiState>(PropertyUiState.Success(emptyList()))

    val uiState: StateFlow<PropertyUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                    getAllPropertiesUseCase().collect() { properties ->
                        Log.d("ListViewModel", "Collected properties: $properties")
                        _uiState.value = PropertyUiState.Success(properties)
                    }
            } catch (exception: Exception) {Log.e("ListViewModel", "Error collecting properties", exception)
                _uiState.value = PropertyUiState.Error(exception)}
        }
    }

    sealed class PropertyUiState {
        data class Success(val properties: List<Property>): PropertyUiState()
        data class Error(val exception: Exception): PropertyUiState()
    }

}



        /*val propertyListLiveData: StateFlow<List<Property>> =
            propertyRepository.getAllProperties().map { propertyLocalList ->
                propertyLocalList.map { propertyLocal ->
                    Property(
                        id = propertyLocal.id,
                        type = propertyLocal.type,
                        price = propertyLocal.price,
                        area = propertyLocal.area,
                        numberOfRooms = propertyLocal.numberOfRooms,
                        description = propertyLocal.description,
                        photos = propertyLocal.photos,
                        videoUrl = propertyLocal.videoUrl,
                        address = propertyLocal.address,
                        nearbyPointsOfInterest = propertyLocal.nearByPointsOfInterest,
                        status = propertyLocal.status,
                        entryDate = propertyLocal.entryDate,
                        saleDate = propertyLocal.saleDate,
                        agent = propertyLocal.agent
                    )
                }

            }.stateIn(scope =  CoroutineScope(Context.))*/

    /*fun PropertyLocal.toProperty(): Property {
        return Property(id, type, price, area, numberOfRooms, description, photos, videoUrl,
            address, nearbyPointsOfInterest, status, entryDate, saleDate, agent)
    }*/





