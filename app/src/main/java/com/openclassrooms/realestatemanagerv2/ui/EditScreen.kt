package com.openclassrooms.realestatemanagerv2.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.ui.composables.AddContentOnePane
import com.openclassrooms.realestatemanagerv2.ui.composables.AddContentTwoPane
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.ErrorStateContent
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyFormUiState
import com.openclassrooms.realestatemanagerv2.viewmodels.EditPropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    onUpClicked: () -> Unit,
    editViewModel: EditPropertyViewModel
) {
    val uiState = editViewModel.uiState.collectAsState().value
    val updatedPropertyId = (uiState as? PropertyFormUiState.Success)?.propertyId
    val context = LocalContext.current

    LaunchedEffect(updatedPropertyId) {
        updatedPropertyId ?: return@LaunchedEffect
        // 1. Navigate and pass the id to home screen
        navController.navigate(BottomNavItem.List.routeWith(updatedPropertyId)) {
            popUpTo("edit_screen") { inclusive = true }
        }
        // 2. Clear local state
        editViewModel.returnToEditingState()
        Toast.makeText(context, "Property edited succesfully", Toast.LENGTH_LONG).show()
    }

    AppTopBar(
        title = stringResource(id = R.string.edit_property),
        showUpButton = true,
        onUpClick = onUpClicked,
        showAddButton = false,
        onAddClick = {},
    ) { paddingValues ->

        when (val state = uiState) {
            is PropertyFormUiState.Loading -> {
                Box(
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is PropertyFormUiState.Error -> {
                ErrorStateContent(
                    message = state.message,
                    onRetry = { editViewModel.returnToEditingState() }
                )
            }

            is PropertyFormUiState.Success -> Box(
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            )

            is PropertyFormUiState.Editing -> {

                if (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
                    AddContentTwoPane(
                        paddingValues = paddingValues,
                        title = stringResource(id = R.string.edit_this_property),
                        onCreatePropertyClick = { editViewModel.updateProperty() },

                        photoUri = state.photoUri,
                        photoDescription = state.photoDescription,
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
                        onVideoDeleteClick = { videoToDelete ->
                            editViewModel.deletePhoto(
                                videoToDelete
                            )
                        },

                        //Values
                        description = state.description.value,
                        type = state.type.value,
                        area = state.area.value,
                        price = state.price.value,
                        numberOfRooms = state.numberOfRooms.value,
                        photoList = state.photoList,
                        videoList = state.videoList,
                        address = state.address.value,
                        nearbyPointList = editViewModel.allPointOfInterestList,
                        nearbyPointSelectedSet = state.nearbyPointSet,
                        agent = state.agent,
                        agentList = state.agentList,
                        showSaveButton = state.isFormValid,
                        //DatePickers
                        selectedEntryDate = state.entryDate,
                        onEntryDateSelected = { newEntryDate ->
                            editViewModel.updateEntryDate(
                                newEntryDate
                            )
                        },
                        isEntryDateDialogShown = state.isEntryDatePickerShown,
                        onShowEntryDateDialog = { editViewModel.updateEntryDateDialogShown(true) },
                        onDismissEntryDateDialog = { editViewModel.updateEntryDateDialogShown(false) },
                        entryDatePickerState = rememberDatePickerState(),
                        selectedSaleDate = state.saleDate,
                        onSaleDateSelected = { newSaleDate ->
                            editViewModel.updateSaleDate(
                                newSaleDate
                            )
                        },
                        isSaleDateDialogShown = state.isSaleDatePickerShown,
                        onShowSaleDateDialog = { editViewModel.updateSaleDateDialogShown(true) },
                        onDismissSaleDateDialog = { editViewModel.updateSaleDateDialogShown(false) },
                        saleDatePickerState = rememberDatePickerState(),

                        // Error Values
                        descriptionError = state.description.error ?: "",
                        typeError = state.type.error ?: "",
                        priceError = state.price.error ?: "",
                        addressError = state.address.error ?: "",
                        areaError = state.area.error ?: "",
                        numberOfRoomsError = state.numberOfRooms.error ?: "",

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
                            editViewModel.updatePointOfInterestSelection(
                                pointOfInterest,
                                isSelected
                            )
                        },
                        onAgentSelected = { selectedAgent -> editViewModel.updateAgent(selectedAgent) }
                    )
                } else {
                    AddContentOnePane(
                        paddingValues = paddingValues,

                        title = stringResource(id = R.string.edit_this_property),
                        onCreatePropertyClick = { editViewModel.updateProperty() },

                        photoUri = state.photoUri,
                        photoDescription = state.photoDescription,
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
                        onVideoDeleteClick = { videoToDelete ->
                            editViewModel.deletePhoto(
                                videoToDelete
                            )
                        },

                        //Values
                        description = state.description.value,
                        type = state.type.value,
                        area = state.area.value,
                        price = state.price.value,
                        numberOfRooms = state.numberOfRooms.value,
                        photoList = state.photoList,
                        videoList = state.videoList,
                        address = state.address.value,
                        nearbyPointList = editViewModel.allPointOfInterestList,
                        nearbyPointSelectedSet = state.nearbyPointSet,
                        agent = state.agent,
                        agentList = state.agentList,
                        showSaveButton = state.isFormValid,
                        //DatePickers
                        selectedEntryDate = state.entryDate,
                        onEntryDateSelected = { newEntryDate ->
                            editViewModel.updateEntryDate(
                                newEntryDate
                            )
                        },
                        isEntryDateDialogShown = state.isEntryDatePickerShown,
                        onShowEntryDateDialog = { editViewModel.updateEntryDateDialogShown(true) },
                        onDismissEntryDateDialog = { editViewModel.updateEntryDateDialogShown(false) },
                        entryDatePickerState = rememberDatePickerState(),
                        selectedSaleDate = state.saleDate,
                        onSaleDateSelected = { newSaleDate ->
                            editViewModel.updateSaleDate(
                                newSaleDate
                            )
                        },
                        isSaleDateDialogShown = state.isSaleDatePickerShown,
                        onShowSaleDateDialog = { editViewModel.updateSaleDateDialogShown(true) },
                        onDismissSaleDateDialog = { editViewModel.updateSaleDateDialogShown(false) },
                        saleDatePickerState = rememberDatePickerState(),

                        // Error Values
                        descriptionError = state.description.error ?: "",
                        typeError = state.type.error ?: "",
                        priceError = state.price.error ?: "",
                        addressError = state.address.error ?: "",
                        areaError = state.area.error ?: "",
                        numberOfRoomsError = state.numberOfRooms.error ?: "",

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
                            editViewModel.updatePointOfInterestSelection(
                                pointOfInterest,
                                isSelected
                            )
                        },
                        onAgentSelected = { selectedAgent -> editViewModel.updateAgent(selectedAgent) }
                    )
                }
            }
        }
    }
}