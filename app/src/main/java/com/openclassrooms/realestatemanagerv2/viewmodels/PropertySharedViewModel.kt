package com.openclassrooms.realestatemanagerv2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.usecases.GetAllPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.ObserveNetworkStatusUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.SearchPropertiesUseCase
import com.openclassrooms.realestatemanagerv2.domain.usecases.UpdateMissingLocationUseCase
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyUiState
import com.openclassrooms.realestatemanagerv2.utils.DatabaseStatusTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Shared ViewModel responsible for orchestrating the property list state, search filtering,
 * and real-time synchronization across the application.
 *
 * This ViewModel serves as a central hub for:
 * 1. **Data Orchestration**: Combining the property list state with real-time network status
 *    monitoring via [ObserveNetworkStatusUseCase].
 * 2. **Adaptive Layout Management**: Controls `detailPaneCloseVersion` to synchronize
 *    the visibility of the detail panel during major state transitions (e.g., new search).
 * 3. **Smart Data Loading**: Ensuring the database is fully initialized via [DatabaseStatusTracker]
 *    before any queries are executed.
 * 4. **Geocoding Maintenance**: Automatically triggering [UpdateMissingLocationUseCase]
 *    on startup to resolve coordinates for properties added offline.
 *
 * @property getAllPropertiesUseCase Use case to retrieve the complete list of properties.
 * @property searchPropertiesUseCase Use case for multi-criteria property filtering.
 * @property updateMissingLocationUseCase Use case to geocode addresses that lack coordinates.
 * @property observeNetworkStatusUseCase Use case to provide a real-time stream of connectivity status.
 * @property databaseStatusTracker Utility to ensure Room DB readiness before data collection.
 */
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

    /**
     * Exposes the unified UI state.
     * Uses [combine] to merge the property internal state with the live network status flow.
     * Implements [stateIn] to share the state efficiently across multiple screens
     * while maintaining a 5-second buffer for configuration changes.
     */
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

    /**
     * Core private method to fetch properties from the local data source.
     *
     * @param isSearch If true, applies [searchCriteria] instead of a full fetch.
     * @param searchCriteria The filter parameters provided by the user.
     * @param incrementCloseVersion If true, increments the close version
     * in [PropertyUiState.Success] to signal the UI to close the Detail Pane.
     */
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
            _uiState.value = PropertyUiState.Error(exception.message ?: "Unknown error")
        }
    }

    fun updateSelectedProperty(propertyId: String) {
        updateState {
            copy(selectedPropertyId = propertyId)
        }
    }

    fun updateAddedProperty(propertyId: String?) {
        updateState {
            copy(addedPropertyId = propertyId)
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

    /**
     * Checks for missing geographic coordinates and refreshes data if any are resolved.
     * This ensures that UI components like Map Markers and Static Map previews
     * are correctly displayed and up-to-date.
     */
    suspend fun updateAndRefreshIfNeeded() {

        val updatedCount = updateMissingLocationUseCase()
        if (updatedCount > 0) {
            refreshProperties()
        }
    }

    /**
     * Internal helper for atomic updates of the [PropertyUiState.Success] state.
     * Ensures thread-safety and prevents state loss during concurrent updates.
     */
    private fun updateState(update: PropertyUiState.Success.() -> PropertyUiState.Success) {
        _uiState.update { currentState ->
            if (currentState is PropertyUiState.Success) {
                currentState.update()
            } else {
                currentState
            }
        }
    }
}