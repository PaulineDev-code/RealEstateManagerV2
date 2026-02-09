package com.openclassrooms.realestatemanagerv2.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.ui.composables.AddContentOnePane
import com.openclassrooms.realestatemanagerv2.ui.composables.AddContentTwoPane
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.EditPropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    onBackClicked: () -> Unit,
    onNavigateToAdd: () -> Unit,
    editViewModel: EditPropertyViewModel
) {
    val state = editViewModel.uiState.collectAsState().value
    val editingState = state as? EditPropertyViewModel.EditPropertyUiState.Editing
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val updatedPropertyId = (state as? EditPropertyViewModel.EditPropertyUiState.Success)?.propertyId


    if (state is EditPropertyViewModel.EditPropertyUiState.Error) {
        val errorState = state as EditPropertyViewModel.EditPropertyUiState.Error
        when (val error = errorState.error) {

            is EditPropertyViewModel.EditPropertyError.GeneralError -> {
                // Vous pouvez afficher un message générique ou utiliser le message de l'exception
                errorMessage = error.exception.message ?: "unknown error"
            }

            is EditPropertyViewModel.EditPropertyError.FieldError -> TODO()
            null -> TODO()
        }
    }

    LaunchedEffect(updatedPropertyId) {
        updatedPropertyId ?: return@LaunchedEffect
        // 1. Navigate and pass the id to home screen
        navController.navigate(BottomNavItem.List.routeWith(updatedPropertyId)) {
            popUpTo("edit_screen") { inclusive = true }
        }
        // 2. Clear local state
        editViewModel.returnToEditingState()
    }

    AppTopBar(
        onNavigationClick = onBackClicked,
        onAddClick = onNavigateToAdd,
        onModifyClick = {},
        showModifyButton = false,
    ) { paddingValues ->

        if (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
            AddContentTwoPane(
                paddingValues = paddingValues,
                title = stringResource(id = R.string.edit_property),
                onCreatePropertyClick = { editViewModel.updateProperty() },
                errorMessage = errorMessage,
                onDismissError = {
                    errorMessage = null
                    editViewModel.returnToEditingState()
                },

                photoUri = editingState?.photoUri ?: "",
                photoDescription = editingState?.photoDescription ?: "",
                onPhotoUriChange = { newPhotoUri -> editViewModel.updatePhotoUri(newPhotoUri) },
                onPhotoDescriptionChange = { newDescription ->
                    editViewModel.updatePhotoDescription(
                        newDescription
                    )
                },
                onPhotoDeleteClick = { photoToRemove ->
                    editViewModel.deletePhoto(photoToRemove)
                },

                onAddPhotoDescriptionClick = { editViewModel.addPhoto() },
                onDismissAddPhotoDescriptionDialog = {
                    editViewModel.updatePhotoUri("")
                    editViewModel.updatePhotoDescription("")
                },

                onVideoUriChange = { newVideoUri -> editViewModel.updateVideoUri(newVideoUri) },
                onVideoAdded = { videoToAdd -> editViewModel.addVideo(videoToAdd) },
                onVideoDeleteClick = { videoToDelete -> editViewModel.deletePhoto(videoToDelete) },

                //Values
                description = editingState?.description?.value ?: "",
                type = editingState?.type?.value ?: "",
                area = editingState?.area?.value ?: "",
                price = editingState?.price?.value ?: "",
                numberOfRooms = editingState?.numberOfRooms?.value ?: "",
                photoList = editingState?.photoList ?: emptyList(),
                videoList = editingState?.videoList ?: emptyList(),
                address = editingState?.address?.value ?: "",
                nearbyPointList = editViewModel.allPointOfInterestList,
                nearbyPointSelectedSet = editingState?.nearbyPointSet ?: emptySet(),
                agent = editingState?.agent,
                agentList = editingState?.agentList ?: emptyList(),
                showSaveButton = editingState?.isFormValid,
                //DatePickers
                selectedEntryDate = editingState?.entryDate,
                onEntryDateSelected = { newEntryDate -> editViewModel.updateEntryDate(newEntryDate) },
                isEntryDateDialogShown = editingState?.isEntryDatePickerShown ?: false,
                onShowEntryDateDialog = { editViewModel.updateEntryDateDialogShown(true) },
                onDismissEntryDateDialog = { editViewModel.updateEntryDateDialogShown(false) },
                entryDatePickerState = rememberDatePickerState(),
                selectedSaleDate = editingState?.saleDate,
                onSaleDateSelected = { newSaleDate -> editViewModel.updateSaleDate(newSaleDate) },
                isSaleDateDialogShown = editingState?.isSaleDatePickerShown ?: false,
                onShowSaleDateDialog = { editViewModel.updateSaleDateDialogShown(true) },
                onDismissSaleDateDialog = { editViewModel.updateSaleDateDialogShown(false) },
                saleDatePickerState = rememberDatePickerState(),

                // Error Values
                descriptionError = editingState?.description?.error ?: "",
                typeError = editingState?.type?.error ?: "",
                priceError = editingState?.price?.error ?: "",
                addressError = editingState?.address?.error ?: "",
                areaError = editingState?.area?.error ?: "",
                numberOfRoomsError = editingState?.numberOfRooms?.error ?: "",

                //On change callback functions
                onDescriptionChange = { newDescription ->
                    editViewModel.updateDescription(
                        newDescription
                    )
                },
                onTypeChange = { newType -> editViewModel.updateType(newType) },
                onAreaChange = { newArea -> editViewModel.updateArea(newArea) },
                onPriceChange = { newPrice -> editViewModel.updatePrice(newPrice) },
                onNumberOfRoomsChange = { newNumberOfRooms ->
                    editViewModel.updateNumberOfRooms(
                        newNumberOfRooms
                    )
                },
                onAddressChange = { newAddress -> editViewModel.updateAddress(newAddress) },
                onNearbyPointChange = { pointOfInterest, isSelected ->
                    editViewModel.updatePointOfInterestSelection(pointOfInterest, isSelected)
                },
                onAgentSelected = { selectedAgent -> editViewModel.updateAgent(selectedAgent) }
            )
        } else {
            AddContentOnePane(
                paddingValues = paddingValues,

                title = stringResource(id = R.string.edit_property),
                onCreatePropertyClick = { editViewModel.updateProperty() },
                errorMessage = errorMessage,
                onDismissError = {
                    errorMessage = null
                    editViewModel.returnToEditingState()
                },

                photoUri = editingState?.photoUri ?: "",
                photoDescription = editingState?.photoDescription ?: "",
                onPhotoUriChange = { newPhotoUri -> editViewModel.updatePhotoUri(newPhotoUri) },
                onPhotoDescriptionChange = { newDescription ->
                    editViewModel.updatePhotoDescription(
                        newDescription
                    )
                },
                onPhotoDeleteClick = { photoToRemove ->
                    editViewModel.deletePhoto(photoToRemove)
                },

                onAddPhotoDescriptionClick = { editViewModel.addPhoto() },
                onDismissAddPhotoDescriptionDialog = {
                    editViewModel.updatePhotoUri("")
                    editViewModel.updatePhotoDescription("")
                },

                onVideoUriChange = { newVideoUri -> editViewModel.updateVideoUri(newVideoUri) },
                onVideoAdded = { videoToAdd -> editViewModel.addVideo(videoToAdd) },
                onVideoDeleteClick = { videoToDelete -> editViewModel.deletePhoto(videoToDelete) },

                //Values
                description = editingState?.description?.value ?: "",
                type = editingState?.type?.value ?: "",
                area = editingState?.area?.value ?: "",
                price = editingState?.price?.value ?: "",
                numberOfRooms = editingState?.numberOfRooms?.value ?: "",
                photoList = editingState?.photoList ?: emptyList(),
                videoList = editingState?.videoList ?: emptyList(),
                address = editingState?.address?.value ?: "",
                nearbyPointList = editViewModel.allPointOfInterestList,
                nearbyPointSelectedSet = editingState?.nearbyPointSet ?: emptySet(),
                agent = editingState?.agent,
                agentList = editingState?.agentList ?: emptyList(),
                showSaveButton = editingState?.isFormValid,
                //DatePickers
                selectedEntryDate = editingState?.entryDate,
                onEntryDateSelected = { newEntryDate -> editViewModel.updateEntryDate(newEntryDate) },
                isEntryDateDialogShown = editingState?.isEntryDatePickerShown ?: false,
                onShowEntryDateDialog = { editViewModel.updateEntryDateDialogShown(true) },
                onDismissEntryDateDialog = { editViewModel.updateEntryDateDialogShown(false) },
                entryDatePickerState = rememberDatePickerState(),
                selectedSaleDate = editingState?.saleDate,
                onSaleDateSelected = { newSaleDate -> editViewModel.updateSaleDate(newSaleDate) },
                isSaleDateDialogShown = editingState?.isSaleDatePickerShown ?: false,
                onShowSaleDateDialog = { editViewModel.updateSaleDateDialogShown(true) },
                onDismissSaleDateDialog = { editViewModel.updateSaleDateDialogShown(false) },
                saleDatePickerState = rememberDatePickerState(),

                // Error Values
                descriptionError = editingState?.description?.error ?: "",
                typeError = editingState?.type?.error ?: "",
                priceError = editingState?.price?.error ?: "",
                addressError = editingState?.address?.error ?: "",
                areaError = editingState?.area?.error ?: "",
                numberOfRoomsError = editingState?.numberOfRooms?.error ?: "",

                //On change callback functions
                onDescriptionChange = { newDescription ->
                    editViewModel.updateDescription(
                        newDescription
                    )
                },
                onTypeChange = { newType -> editViewModel.updateType(newType) },
                onAreaChange = { newArea -> editViewModel.updateArea(newArea) },
                onPriceChange = { newPrice -> editViewModel.updatePrice(newPrice) },
                onNumberOfRoomsChange = { newNumberOfRooms ->
                    editViewModel.updateNumberOfRooms(
                        newNumberOfRooms
                    )
                },
                onAddressChange = { newAddress -> editViewModel.updateAddress(newAddress) },
                onNearbyPointChange = { pointOfInterest, isSelected ->
                    editViewModel.updatePointOfInterestSelection(pointOfInterest, isSelected)
                },
                onAgentSelected = { selectedAgent -> editViewModel.updateAgent(selectedAgent) }
            )
        }
    }
}