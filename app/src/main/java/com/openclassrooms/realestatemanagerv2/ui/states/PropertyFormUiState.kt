package com.openclassrooms.realestatemanagerv2.ui.states

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Video
import com.openclassrooms.realestatemanagerv2.ui.models.FormField

/**
 * Unified UI state representing a property form.
 *
 * This sealed interface defines the possible states for both the "Add" and "Edit" property screens,
 * ensuring a predictable data flow during the lifecycle of a property entry or modification.
 */
sealed interface PropertyFormUiState {

    /**
     * Transitional state indicating that a background operation (e.g., geocoding or saving)
     * is currently in progress.
     */
    object Loading : PropertyFormUiState

    /**
     * Terminal state indicating a successful save or update operation.
     *
     * @property propertyId The unique identifier of the property that was successfully persisted.
     */
    data class Success(val propertyId: String) : PropertyFormUiState

    /**
     * Error state containing a message to be displayed to the user.
     *
     * @property message A user-friendly error description.
     */
    data class Error(val message: String) : PropertyFormUiState

    /**
     * Active state where the user interacts with the form.
     *
     * This state implements a "Resilient UI" pattern, holding all form data and
     * validation status. It is used for both adding new properties (where [id] is null)
     * and editing existing ones.
     *
     * @property id The property ID (non-null if in edition mode).
     * @property description [FormField] holding the property description and its validation error.
     * @property type [FormField] holding the property type (e.g., Manor, House).
     * @property price [FormField] holding the formatted price value.
     * @property area [FormField] holding the surface area in square meters.
     * @property numberOfRooms [FormField] holding the total room count.
     * @property photoUri Temporary storage for a newly selected photo's URI.
     * @property videoUri Temporary storage for a newly selected video's URI.
     * @property photoDescription Temporary storage for a new photo's caption.
     * @property mediaLists The complete collection of [Media] items attached to the form.
     * @property address [FormField] holding the physical address for geocoding.
     * @property nearbyPointSet A set of selected [PointOfInterest] located near the property.
     * @property entryDate The timestamp when the property was listed.
     * @property isEntryDatePickerShown Controls the visibility of the entry date picker dialog.
     * @property saleDate The optional timestamp when the property was sold.
     * @property isSaleDatePickerShown Controls the visibility of the sale date picker dialog.
     * @property agent The [Agent] assigned to the property.
     * @property agentList The list of available agents for selection.
     * @property isFormValid Computed flag indicating if the form meets all business requirements for submission.
     */
    data class Editing(
        val id: String? = null,
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
    ) : PropertyFormUiState {

        /**
         * Helper property that filters [mediaLists] to return only [Photo] items.
         */
        val photoList: List<Photo>
            get() = mediaLists.filterIsInstance<Photo>()

        /**
         * Helper property that filters [mediaLists] to return only [Video] items.
         */
        val videoList: List<Video>
            get() = mediaLists.filterIsInstance<Video>()

    }
}