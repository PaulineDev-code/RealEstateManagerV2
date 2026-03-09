package com.openclassrooms.realestatemanagerv2.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.ui.composables.AddContentOnePane
import com.openclassrooms.realestatemanagerv2.ui.composables.AddContentTwoPane
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.ErrorStateContent
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyFormUiState
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navController: NavController,
    onUpClicked: () -> Unit,
    addViewModel: AddPropertyViewModel = hiltViewModel()
) {
    val uiState by addViewModel.uiState.collectAsStateWithLifecycle()
    val addedPropertyId = (uiState as? PropertyFormUiState.Success)?.propertyId
    val context = LocalContext.current



    LaunchedEffect(addedPropertyId) {
        addedPropertyId ?: return@LaunchedEffect
        // 1. Navigate and pass the id to home screen
        navController.navigate(BottomNavItem.List.routeWith(addedPropertyId)) {
            popUpTo("add_screen") { inclusive = true }
        }
        // 2. Clear local state
        addViewModel.returnToEditingState()
        Toast.makeText(context, "Property added successfully", Toast.LENGTH_LONG).show()
    }

    AppTopBar(
        title = stringResource(R.string.add_property),
        showUpButton = true,
        onUpClick = onUpClicked,
        showAddButton = false,
        onAddClick = {},
        onModifyClick = {},
        showModifyButton = false,
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
                    message= state.message,
                    onRetry = { addViewModel.returnToEditingState() }
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
                        title = stringResource(id = R.string.add_a_new_property),
                        onCreatePropertyClick = { addViewModel.createProperty() },

                        photoUri = state.photoUri,
                        photoDescription = state.photoDescription,
                        onPhotoUriChange = { newPhotoUri -> addViewModel.updatePhotoUri(newPhotoUri) },
                        onPhotoDescriptionChange = { newDescription ->
                            addViewModel.updatePhotoDescription(
                                newDescription
                            )
                        },
                        onPhotoDeleteClick = { photoToRemove ->
                            addViewModel.deleteMedia(photoToRemove)
                        },

                        onAddPhotoDescriptionClick = { addViewModel.addPhoto() },
                        onDismissAddPhotoDescriptionDialog = {
                            addViewModel.updatePhotoUri("")
                            addViewModel.updatePhotoDescription("")
                        },
                        onVideoUriChange = { newVideoUri -> addViewModel.updateVideoUri(newVideoUri) },
                        onVideoAdded = { videoToAdd -> addViewModel.addVideo(videoToAdd) },
                        onVideoDeleteClick = { videoToDelete ->
                            addViewModel.deleteMedia(
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
                        nearbyPointList = addViewModel.allPointOfInterestList,
                        nearbyPointSelectedSet = state.nearbyPointSet,
                        agent = state.agent,
                        agentList = state.agentList,
                        showSaveButton = state.isFormValid,
                        //DatePickers
                        selectedEntryDate = state.entryDate,
                        onEntryDateSelected = { newEntryDate ->
                            addViewModel.updateEntryDate(
                                newEntryDate
                            )
                        },
                        isEntryDateDialogShown = state.isEntryDatePickerShown,
                        onShowEntryDateDialog = { addViewModel.updateEntryDateDialogShown(true) },
                        onDismissEntryDateDialog = { addViewModel.updateEntryDateDialogShown(false) },
                        entryDatePickerState = rememberDatePickerState(),
                        selectedSaleDate = state.saleDate,
                        onSaleDateSelected = { newSaleDate ->
                            addViewModel.updateSaleDate(
                                newSaleDate
                            )
                        },
                        isSaleDateDialogShown = state.isSaleDatePickerShown,
                        onShowSaleDateDialog = { addViewModel.updateSaleDateDialogShown(true) },
                        onDismissSaleDateDialog = { addViewModel.updateSaleDateDialogShown(false) },
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
                            addViewModel.updateDescription(
                                newDescription
                            )
                        },
                        onTypeChange = { newType -> addViewModel.updateType(newType) },
                        onAreaChange = { newArea -> addViewModel.updateArea(newArea) },
                        onPriceChange = { newPrice -> addViewModel.updatePrice(newPrice) },
                        onNumberOfRoomsChange = { newNumberOfRooms ->
                            addViewModel.updateNumberOfRooms(
                                newNumberOfRooms
                            )
                        },
                        onAddressChange = { newAddress -> addViewModel.updateAddress(newAddress) },
                        onNearbyPointChange = { pointOfInterest, isSelected ->
                            addViewModel.updatePointOfInterestSelection(pointOfInterest, isSelected)
                        },
                        onAgentSelected = { selectedAgent -> addViewModel.updateAgent(selectedAgent) }
                    )
                } else {
                    AddContentOnePane(
                        paddingValues = paddingValues,

                        title = stringResource(id = R.string.add_a_new_property),
                        onCreatePropertyClick = { addViewModel.createProperty() },

                        photoUri = state.photoUri,
                        photoDescription = state.photoDescription,
                        onPhotoUriChange = { newPhotoUri -> addViewModel.updatePhotoUri(newPhotoUri) },
                        onPhotoDescriptionChange = { newDescription ->
                            addViewModel.updatePhotoDescription(
                                newDescription
                            )
                        },
                        onPhotoDeleteClick = { photoToRemove ->
                            addViewModel.deleteMedia(photoToRemove)
                        },

                        onAddPhotoDescriptionClick = { addViewModel.addPhoto() },
                        onDismissAddPhotoDescriptionDialog = {
                            addViewModel.updatePhotoUri("")
                            addViewModel.updatePhotoDescription("")
                        },

                        onVideoUriChange = { newVideoUri -> addViewModel.updateVideoUri(newVideoUri) },
                        onVideoAdded = { videoToAdd -> addViewModel.addVideo(videoToAdd) },
                        onVideoDeleteClick = { videoToDelete ->
                            addViewModel.deleteMedia(
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
                        nearbyPointList = addViewModel.allPointOfInterestList,
                        nearbyPointSelectedSet = state.nearbyPointSet,
                        agent = state.agent,
                        agentList = state.agentList,
                        showSaveButton = state.isFormValid,
                        //DatePickers
                        selectedEntryDate = state.entryDate,
                        onEntryDateSelected = { newEntryDate ->
                            addViewModel.updateEntryDate(
                                newEntryDate
                            )
                        },
                        isEntryDateDialogShown = state.isEntryDatePickerShown,
                        onShowEntryDateDialog = { addViewModel.updateEntryDateDialogShown(true) },
                        onDismissEntryDateDialog = { addViewModel.updateEntryDateDialogShown(false) },
                        entryDatePickerState = rememberDatePickerState(),
                        selectedSaleDate = state.saleDate,
                        onSaleDateSelected = { newSaleDate ->
                            addViewModel.updateSaleDate(
                                newSaleDate
                            )
                        },
                        isSaleDateDialogShown = state.isSaleDatePickerShown,
                        onShowSaleDateDialog = { addViewModel.updateSaleDateDialogShown(true) },
                        onDismissSaleDateDialog = { addViewModel.updateSaleDateDialogShown(false) },
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
                            addViewModel.updateDescription(
                                newDescription
                            )
                        },
                        onTypeChange = { newType -> addViewModel.updateType(newType) },
                        onAreaChange = { newArea -> addViewModel.updateArea(newArea) },
                        onPriceChange = { newPrice -> addViewModel.updatePrice(newPrice) },
                        onNumberOfRoomsChange = { newNumberOfRooms ->
                            addViewModel.updateNumberOfRooms(
                                newNumberOfRooms
                            )
                        },
                        onAddressChange = { newAddress -> addViewModel.updateAddress(newAddress) },
                        onNearbyPointChange = { pointOfInterest, isSelected ->
                            addViewModel.updatePointOfInterestSelection(pointOfInterest, isSelected)
                        },
                        onAgentSelected = { selectedAgent -> addViewModel.updateAgent(selectedAgent) }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun AddScreenPreview() {
    AddContentOnePane(
        paddingValues = PaddingValues(0.dp),
        title = stringResource(id = R.string.add_a_new_property),
        address = "address",
        agent = Agent("1", "Smith", "6666666666", "smith@gmail.com"),
        agentList = emptyList(),
        area = "area",
        description = "description",
        type = "type",
        price = "price",
        numberOfRooms = "numberOfRooms",
        photoList = emptyList(),
        videoList = emptyList(),
        nearbyPointSelectedSet = setOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
        nearbyPointList = emptyList(),
        selectedEntryDate = null,
        onEntryDateSelected = {},
        isEntryDateDialogShown = false,
        onShowEntryDateDialog = {},
        onDismissEntryDateDialog = {},
        entryDatePickerState = rememberDatePickerState(),
        selectedSaleDate = null,
        onSaleDateSelected = {},
        isSaleDateDialogShown = false,
        onShowSaleDateDialog = {},
        onDismissSaleDateDialog = {},
        saleDatePickerState = rememberDatePickerState(),
        showSaveButton = true,
        // Error Values
        descriptionError = "descriptionError",
        typeError = "typeError",
        priceError = "priceError",
        addressError = "addressError",
        areaError = "areaError",
        numberOfRoomsError = "numberOfRoomsError",

        // OnChange functions
        onDescriptionChange = {},
        onTypeChange = {},
        onPriceChange = {},
        onAreaChange = {},
        onNumberOfRoomsChange = {},
        onAddressChange = {},
        onNearbyPointChange = { poi, bool -> },
        onAgentSelected = {},

        //Other functions
        photoUri = "",
        photoDescription = "",
        onPhotoUriChange = {},
        onPhotoDescriptionChange = {},
        onPhotoDeleteClick = {},
        onAddPhotoDescriptionClick = {},
        onDismissAddPhotoDescriptionDialog = {},

        onVideoUriChange = {},
        onVideoAdded = {},
        onVideoDeleteClick = {},

        onCreatePropertyClick = {}
    )
}