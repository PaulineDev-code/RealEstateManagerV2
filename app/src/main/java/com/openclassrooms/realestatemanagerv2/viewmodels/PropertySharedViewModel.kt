package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.ObserveNetworkStatusUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.SearchPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdateMissingLocationUseCase
import com.openclassrooms.realestatemanagerv2.utils.DatabaseStatusTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertySharedViewModel @Inject constructor
    (private val getAllPropertiesUseCase: GetAllPropertiesUseCase,
     private val searchPropertiesUseCase: SearchPropertiesUseCase,
     private val updateMissingLocationUseCase: UpdateMissingLocationUseCase,
     private val observeNetworkStatusUseCase: ObserveNetworkStatusUseCase,
     private val databaseStatusTracker: DatabaseStatusTracker
) : ViewModel() {

    private val networkFlow = observeNetworkStatusUseCase()
    private val _uiState = MutableStateFlow<PropertyUiState>(PropertyUiState.Loading)
    val uiState: StateFlow<PropertyUiState> = combine(_uiState, networkFlow) {
            propertyUiState, networkStatus ->

        Log.d("ViewModel", "[Combine] Combining networkStatus: $networkStatus ")
        val successState = propertyUiState as? PropertyUiState.Success
        if(successState != null) {
            successState.copy(networkStatus = networkStatus)
        } else propertyUiState
    }.catch { Log.e("PropertySharedViewModel", "error combining flows", it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PropertyUiState.Loading)

    init {
        viewModelScope.launch {
            databaseStatusTracker.isReady.first { it }
            loadProperties()
            updateAndRefreshIfNeeded()
        }
    }

    private suspend fun loadProperties(
        isSearch: Boolean = false,
        searchCriteria: PropertySearchCriteria? = null,
        incrementCloseVersion: Boolean = false
    ) {

        val previousState = _uiState.value as? PropertyUiState.Success
        if (previousState == null) {
            _uiState.value = PropertyUiState.Loading
        }

        try {
            val properties = if (isSearch && searchCriteria != null) {
                searchPropertiesUseCase(searchCriteria)
            } else {
                getAllPropertiesUseCase()
            }

            val newCloseVersion = if (isSearch || incrementCloseVersion) {
                (previousState?.detailPaneCloseVersion ?: 0) + 1
            } else {
                previousState?.detailPaneCloseVersion ?: 0
            }

            _uiState.value = PropertyUiState.Success(
                properties = properties,
                isFiltered = isSearch,
                detailPaneCloseVersion = newCloseVersion,
                addedPropertyId = previousState?.addedPropertyId
            )
            Log.d("ListViewModel", "Collected properties: $properties")
        } catch (exception: Exception) {
            Log.e("ListViewModel", "Error collecting properties", exception)
            _uiState.value = PropertyUiState.Error(exception)
        }
    }

    fun updateSelectedProperty(propertyId: String) {
        val currentState = _uiState.value
        if (currentState is PropertyUiState.Success) {
            _uiState.value =
                currentState.copy(selectedPropertyId = propertyId)
        }
    }

    fun updateAddedProperty(propertyId: String?) {
        val currentState = _uiState.value
        if (currentState is PropertyUiState.Success) {
            _uiState.value = currentState.copy(addedPropertyId = propertyId)
        }
        Log.d(
            "ListViewModel",
            "succes added property ID: ${(_uiState.value as? PropertyUiState.Success)?.addedPropertyId}"
        )
    }

    fun searchProperties(searchCriterias: PropertySearchCriteria) {
        viewModelScope.launch {
            loadProperties(isSearch = true, searchCriteria = searchCriterias)
        }
    }

    fun resetProperties() {
        viewModelScope.launch {
        loadProperties(incrementCloseVersion = true)
            }
    }

    suspend fun refreshProperties() {
        if ((_uiState.value as? PropertyUiState.Success)?.isFiltered != true) {
            loadProperties()
        }
    }

    suspend fun updateAndRefreshIfNeeded() {

        val updatedCount = updateMissingLocationUseCase()
        if (updatedCount > 0) {
            refreshProperties()
        }
    }

    sealed class PropertyUiState {
        object Loading : PropertyUiState()
        data class Success(val properties: List<Property>,
                           val networkStatus: NetworkStatus = NetworkStatus.Unknown,
                           val selectedPropertyId: String = "",
                           val addedPropertyId: String? = null,
                           val isFiltered: Boolean = false,
                           val detailPaneCloseVersion: Int = 0
        ) : PropertyUiState()
        data class Error(val exception: Throwable): PropertyUiState()
    }
}