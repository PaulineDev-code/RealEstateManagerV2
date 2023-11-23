package com.openclassrooms.realestatemanagerv2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.data.entity.AgentEntity
import com.openclassrooms.realestatemanagerv2.data.entity.NearByPointOfInterestEntity
import com.openclassrooms.realestatemanagerv2.data.entity.PhotoEntity
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.repositories.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyListViewModel @Inject constructor
    (private val propertyRepository: PropertyRepository) : ViewModel() {

    //Use StateFlow plutôt que LiveData
    //Et à déplacer en useCase

    private val _uiState = MutableStateFlow(PropertyUiState.Success(emptyList()))

    val uiState: StateFlow<PropertyUiState> = _uiState

    init {
        viewModelScope.launch {
            propertyRepository.getAllProperties().collect {
                _uiState.value = PropertyUiState.Success(
                it.map { propertyLocal ->
                    Property(
                        id = propertyLocal.property.id,
                        type = propertyLocal.property.type,
                        price = propertyLocal.property.price,
                        area = propertyLocal.property.area,
                        numberOfRooms = propertyLocal.property.numberOfRooms,
                        description = propertyLocal.property.description,
                        photos = toPhotos(propertyLocal.photos),
                        videoUrl = propertyLocal.property.videoUrl,
                        address = propertyLocal.property.address,
                        nearbyPointsOfInterest = toPointOfInterest(propertyLocal.nearByPointsOfInterest),
                        status = if(propertyLocal.property.status == "AVAILABLE") { PropertyStatus.Available }
                                 else { PropertyStatus.Sold },
                        entryDate = propertyLocal.property.entryDate,
                        saleDate = propertyLocal.property.saleDate,
                        agent = toAgent(propertyLocal.agent)
                    )
                })
            }
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

    private fun toPhotos(photoEntities: List<PhotoEntity>): List<Photo> {
        return photoEntities.map { photoEntity ->
            Photo(
                imageUrl = photoEntity.photoUrl,
                description = photoEntity.description
            )
        }
    }

    private fun toPointOfInterest(nearByPointOfInterestEntities: List<NearByPointOfInterestEntity>): List<String> {
        return nearByPointOfInterestEntities.map { nearByPointOfInterestEntity ->
            nearByPointOfInterestEntity.pointOfInterest
        }
    }

    private fun toAgent(agentEntity: AgentEntity): Agent {
        return Agent(
            id = agentEntity.id,
            name = agentEntity.name,
            phoneNumber = agentEntity.phoneNumber,
            email = agentEntity.email
        )
    }

    sealed class PropertyUiState {
        data class Success(val properties: List<Property>): PropertyUiState()
        data class Error(val exception: Throwable): PropertyUiState()
    }

}

