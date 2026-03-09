package com.openclassrooms.realestatemanagerv2.ui.states

import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.ui.models.FormField


/**
 * Sealed interface representing the UI states of the Search Properties screen.
 *
 * This state machine focuses on managing a complex set of optional filters.
 * It ensures that all search constraints (price, area, rooms, etc.) are handled
 * reactively with real-time validation.
 */
sealed interface SearchPropertiesUiState {

    /**
     * Represents an error encountered during the search form initialization
     * (e.g., failure to load agents or property types).
     *
     * @property message A descriptive error message to display to the user.
     */
    data class Error(val message: String) : SearchPropertiesUiState

    /**
     * The active state where the user interacts with the search form.
     *
     * @property typeSet Selected property types (e.g., House, Apartment).
     * @property allTypes Available property types loaded from the database.
     * @property minPrice [FormField] for the minimum price constraint.
     * @property maxPrice [FormField] for the maximum price constraint.
     * @property minArea [FormField] for the minimum surface area.
     * @property maxArea [FormField] for the maximum surface area.
     * @property minNumberOfRooms [FormField] for the minimum room count.
     * @property maxNumberOfRooms [FormField] for the maximum room count.
     * @property minPhotos [FormField] for the minimum number of photos required.
     * @property minVideos [FormField] for the minimum number of videos required.
     * @property nearbyPointSet Set of [PointOfInterest] that must be near the property.
     * @property entryDate Optional timestamp for the earliest listing date.
     * @property isEntryDatePickerShown Controls the visibility of the entry date picker.
     * @property saleDate Optional timestamp for the earliest sale date.
     * @property isSaleDatePickerShown Controls the visibility of the sale date picker.
     * @property agent The specifically selected [Agent] to filter by.
     * @property agentList List of all agents available for selection.
     * @property isFormValid Flag indicating if at least one filter is set and no fields have errors.
     */
    data class Editing(
        val typeSet: Set<String> = emptySet(),
        val allTypes: List<String> = emptyList(),
        val minPrice: FormField = FormField(),
        val maxPrice: FormField = FormField(),
        val minArea: FormField = FormField(),
        val maxArea: FormField = FormField(),
        val minNumberOfRooms: FormField = FormField(),
        val maxNumberOfRooms: FormField = FormField(),
        val minPhotos: FormField = FormField(),
        val minVideos: FormField = FormField(),
        val nearbyPointSet: Set<PointOfInterest> = emptySet(),
        val entryDate: Long? = null,
        val isEntryDatePickerShown: Boolean = false,
        val saleDate: Long? = null,
        val isSaleDatePickerShown: Boolean = false,
        val agent: Agent? = null,
        val agentList: List<Agent> = emptyList(),
        val isFormValid: Boolean = false
    ) : SearchPropertiesUiState {}
}