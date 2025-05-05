package com.openclassrooms.realestatemanagerv2.ui

import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest

sealed interface AddScreenUiAction {

    //OnClick functions
    data object OnCreatePropertyClick : AddScreenUiAction
    //Fully implemented callback with parameter function
    data class OnPhotoDeleteClick(val mediaToRemove: Media) : AddScreenUiAction
    data object OnAddPhotoDescriptionClick : AddScreenUiAction
    data object OnVideoDeleteClick : AddScreenUiAction
    data object OnAddNearbyPoint : AddScreenUiAction
    data object OnDeleteNearbyPoint : AddScreenUiAction
    data object OnPhotoUriChange : AddScreenUiAction
    data object OnPhotoDescriptionChange : AddScreenUiAction
    data object OnVideoUriChange : AddScreenUiAction

    //OnChange functions
    data object OnDescriptionChange : AddScreenUiAction
    data object OnTypeChange : AddScreenUiAction
    data object OnAreaChange : AddScreenUiAction
    data object OnPriceChange : AddScreenUiAction
    data object OnNumberOfRoomsChange : AddScreenUiAction
    data object OnAddressChange : AddScreenUiAction
    data object OnEntryDateChange : AddScreenUiAction
    data object OnSaleDateChange : AddScreenUiAction
    data object OnAgentSelected : AddScreenUiAction

    //Point of interest selection change
    data class OnPointOfInterestSelectionChange(val pointOfInterest: PointOfInterest,
                                                val isSelected: Boolean) : AddScreenUiAction





}
