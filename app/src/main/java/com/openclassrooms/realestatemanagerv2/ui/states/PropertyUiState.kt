package com.openclassrooms.realestatemanagerv2.ui.states

import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Property


/**
 * Sealed interface representing the global UI state for the property list and map screens.
 *
 * This state machine orchestrates the data flow for the main application screens,
 * integrating real-time network status and managing adaptive layout transitions.
 */
sealed interface PropertyUiState {

    /**
     * Initial state indicating that the property collection is currently being
     * fetched from the local or remote data source.
     */
    object Loading : PropertyUiState

    /**
     * Represents a successfully loaded state with active data.
     *
     * @property properties The list of [Property] domain models to be displayed.
     * @property networkStatus The current real-time connectivity status of the device.
     * @property selectedPropertyId The ID of the property currently selected by the user.
     * @property addedPropertyId The ID of a newly created property, used for highlighting or navigation.
     * @property isFiltered Boolean flag indicating if the current list is the result of a search query.
     * @property detailPaneCloseVersion A versioning counter used to synchronize the closing of
     * the detail panel in Adaptive Layouts (Material 3 Adaptive Scaffold).
     */
    data class Success(val properties: List<Property>,
                       val networkStatus: NetworkStatus = NetworkStatus.Unknown,
                       val selectedPropertyId: String = "",
                       val addedPropertyId: String? = null,
                       val isFiltered: Boolean = false,
                       val detailPaneCloseVersion: Int = 0
    ) : PropertyUiState

    /**
     * Represents an error state encountered during property data operations.
     *
     * @property message A descriptive, user-friendly error message.
     */
    data class Error(val message: String): PropertyUiState
}