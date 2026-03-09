package com.openclassrooms.realestatemanagerv2.ui.states

import com.openclassrooms.realestatemanagerv2.domain.model.Property

/**
 * Sealed interface representing the various UI states of the Property Details screen.
 *
 * This state machine ensures a predictable data flow within the detail pane,
 * handling everything from initial data fetching to photo gallery interactions.
 */
sealed interface PropertyDetailsUiState {

    /**
     * Initial or transitional state indicating that the property details
     * are currently being fetched from the data source.
     */
    object Loading: PropertyDetailsUiState

    /**
     * Represents a successfully loaded state where the property details are available.
     *
     * @property property The [Property] domain model to display. Can be null if the ID is invalid.
     * @property selectedPhotoIndex The current index of the photo being viewed in the gallery.
     * @property isPhotoViewerShown Whether the full-screen photo viewer overlay is currently visible.
     */
    data class Success(
        val property: Property?,
        val selectedPhotoIndex: Int = 0,
        val isPhotoViewerShown: Boolean = false
    ): PropertyDetailsUiState

    /**
     * Represents an error state encountered during property retrieval.
     *
     * @property message A user-friendly error message describing the failure.
     */
    data class Error(val message: String): PropertyDetailsUiState
}