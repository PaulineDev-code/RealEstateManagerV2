package com.openclassrooms.realestatemanagerv2.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.data.network.NetworkMonitor
import com.openclassrooms.realestatemanagerv2.data.network.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.SearchPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdateMissingLocationUseCase
import com.openclassrooms.realestatemanagerv2.ui.BottomNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertySharedViewModel @Inject constructor
    (private val getAllPropertiesUseCase: GetAllPropertiesUseCase,
     private val searchPropertiesUseCase: SearchPropertiesUseCase,
     private val updateMissingLocationUseCase: UpdateMissingLocationUseCase,
     private val networkMonitor: NetworkMonitor,
     private val savedState: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<PropertyUiState>(PropertyUiState.Loading)

    val uiState: StateFlow<PropertyUiState> = _uiState

    init {
        observeNetworkChanges()
        loadAllProperties()
    }

    private fun observeNetworkChanges() {
        viewModelScope.launch {
            networkMonitor.networkStatus.collectLatest { status ->
                if (status == NetworkStatus.Available) {
                    Log.d("LocationUpdateVM", "Network available. Processing pending location updates.")
                    updateMissingLocationUseCase()
                } else {
                    Log.d("LocationUpdateVM", "Network unavailable.")
                }
            }
        }
    }

    private fun loadAllProperties() {
        val newId: String? = Uri.decode(savedState[BottomNavItem.List.ARG_NEW_ID])

        viewModelScope.launch {
            try {
                val properties = getAllPropertiesUseCase()
                Log.d("ListViewModel", "Collected properties: $properties")
                _uiState.value = PropertyUiState.Success(
                    properties = properties,
                    addedPropertyId = newId,
                    isFiltered = false)

                newId?.let {
                    savedState.remove<String>(BottomNavItem.List.ARG_NEW_ID)
                }

            } catch (exception: Exception) {
                Log.e("ListViewModel", "Error collecting properties", exception)
                _uiState.value = PropertyUiState.Error(exception)
            }
        }
    }

    fun updateSelectedProperty(propertyId: String) {
        _uiState.value = (_uiState.value as PropertyUiState.Success).copy(selectedPropertyId = propertyId)
    }

    fun updateAddedProperty(propertyId: String?) {
        _uiState.value = (_uiState.value as PropertyUiState.Success).copy(addedPropertyId = propertyId)
    }

    fun searchProperties(searchCriterias: PropertySearchCriteria) {
        viewModelScope.launch {
            _uiState.value = PropertyUiState.Loading
            try {
                val filtered = searchPropertiesUseCase(searchCriterias)
                _uiState.value = PropertyUiState.Success(filtered, isFiltered = true)
            } catch (e: Exception) {
                _uiState.value = PropertyUiState.Error(e)
            }
        }
    }

    fun resetProperties() {
        loadAllProperties()
    }

    fun refreshProperties() {
        if ((uiState.value as? PropertySharedViewModel.PropertyUiState.Success)?.isFiltered != true) {
            loadAllProperties()
        }
    }

    sealed class PropertyUiState {
        object Loading : PropertyUiState()
        data class Success(val properties: List<Property>,
                           val selectedPropertyId: String = "",
                           val addedPropertyId: String? = null,
                           val isFiltered: Boolean = false): PropertyUiState()
        data class Error(val exception: Throwable): PropertyUiState()
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





