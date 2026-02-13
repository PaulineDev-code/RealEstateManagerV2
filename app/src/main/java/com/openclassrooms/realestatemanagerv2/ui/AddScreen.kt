package com.openclassrooms.realestatemanagerv2.ui

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navController: NavController,
    onUpClicked: () -> Unit,
    addViewModel: AddPropertyViewModel = hiltViewModel()
) {
    val state by addViewModel.uiState.collectAsStateWithLifecycle()
    val editingState = state as? AddPropertyViewModel.AddPropertyUiState.Editing
    var errorMessage by remember { mutableStateOf<String?>(null) } //TODO : à transmettre au VM
    val addedPropertyId = (state as? AddPropertyViewModel.AddPropertyUiState.Success)?.propertyId
    val context = LocalContext.current

    if (state is AddPropertyViewModel.AddPropertyUiState.Error) {
        val errorState = state as AddPropertyViewModel.AddPropertyUiState.Error
        when (val error = errorState.error) {

            is AddPropertyViewModel.AddPropertyError.GeneralError -> {
                // Vous pouvez afficher un message générique ou utiliser le message de l'exception
                errorMessage = error.exception.message ?: "unknown error"
            }
            is AddPropertyViewModel.AddPropertyError.FieldError -> TODO()
            null -> TODO()
        }
    }

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
        onModifyClick = { /*TODO*/ },
        showModifyButton = false,
    ) { paddingValues ->

        if (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
            AddContentTwoPane(
                paddingValues = paddingValues,
                title = stringResource(id = R.string.add_a_new_property),
                onCreatePropertyClick = { addViewModel.createProperty() },
                errorMessage = errorMessage,
                onDismissError = {
                    errorMessage = null
                    addViewModel.returnToEditingState()
                },

                photoUri = editingState?.photoUri ?: "",
                photoDescription = editingState?.photoDescription ?: "",
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
                onVideoDeleteClick = { videoToDelete -> addViewModel.deleteMedia(videoToDelete) },

                //Values
                description = editingState?.description?.value ?: "",
                type = editingState?.type?.value ?: "",
                area = editingState?.area?.value ?: "",
                price = editingState?.price?.value ?: "",
                numberOfRooms = editingState?.numberOfRooms?.value ?: "",
                photoList = editingState?.photoList ?: emptyList(),
                videoList = editingState?.videoList ?: emptyList(),
                address = editingState?.address?.value ?: "",
                nearbyPointList = addViewModel.allPointOfInterestList,
                nearbyPointSelectedSet = editingState?.nearbyPointSet ?: emptySet(),
                agent = editingState?.agent,
                agentList = editingState?.agentList ?: emptyList(),
                showSaveButton = editingState?.isFormValid,
                //DatePickers
                selectedEntryDate = editingState?.entryDate,
                onEntryDateSelected = { newEntryDate -> addViewModel.updateEntryDate(newEntryDate) },
                isEntryDateDialogShown = editingState?.isEntryDatePickerShown ?: false,
                onShowEntryDateDialog = { addViewModel.updateEntryDateDialogShown(true) },
                onDismissEntryDateDialog = { addViewModel.updateEntryDateDialogShown(false) },
                entryDatePickerState = rememberDatePickerState(),
                selectedSaleDate = editingState?.saleDate,
                onSaleDateSelected = { newSaleDate -> addViewModel.updateSaleDate(newSaleDate) },
                isSaleDateDialogShown = editingState?.isSaleDatePickerShown ?: false,
                onShowSaleDateDialog = { addViewModel.updateSaleDateDialogShown(true) },
                onDismissSaleDateDialog = { addViewModel.updateSaleDateDialogShown(false) },
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
                errorMessage = errorMessage,
                onDismissError = {
                    errorMessage = null
                    addViewModel.returnToEditingState()
                },

                photoUri = editingState?.photoUri ?: "",
                photoDescription = editingState?.photoDescription ?: "",
                onPhotoUriChange = { newPhotoUri -> addViewModel.updatePhotoUri(newPhotoUri)},
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
                onVideoDeleteClick = { videoToDelete -> addViewModel.deleteMedia(videoToDelete) },

                //Values
                description = editingState?.description?.value ?: "",
                type = editingState?.type?.value ?: "",
                area = editingState?.area?.value ?: "",
                price = editingState?.price?.value ?: "",
                numberOfRooms = editingState?.numberOfRooms?.value ?: "",
                photoList = editingState?.photoList ?: emptyList(),
                videoList = editingState?.videoList ?: emptyList(),
                address = editingState?.address?.value ?: "",
                nearbyPointList = addViewModel.allPointOfInterestList,
                nearbyPointSelectedSet = editingState?.nearbyPointSet ?: emptySet(),
                agent = editingState?.agent,
                agentList = editingState?.agentList ?: emptyList(),
                showSaveButton = editingState?.isFormValid,
                //DatePickers
                selectedEntryDate = editingState?.entryDate,
                onEntryDateSelected = { newEntryDate -> addViewModel.updateEntryDate(newEntryDate) },
                isEntryDateDialogShown = editingState?.isEntryDatePickerShown ?: false,
                onShowEntryDateDialog = { addViewModel.updateEntryDateDialogShown(true) },
                onDismissEntryDateDialog = { addViewModel.updateEntryDateDialogShown(false) },
                entryDatePickerState = rememberDatePickerState(),
                selectedSaleDate = editingState?.saleDate,
                onSaleDateSelected = { newSaleDate -> addViewModel.updateSaleDate(newSaleDate) },
                isSaleDateDialogShown = editingState?.isSaleDatePickerShown ?: false,
                onShowSaleDateDialog = { addViewModel.updateSaleDateDialogShown(true) },
                onDismissSaleDateDialog = { addViewModel.updateSaleDateDialogShown(false) },
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

        errorMessage = "errorMessage",
        onDismissError = {},

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
/*
description = "description",
type = "type",
price = "price",
area = "area",
numberOfRooms = "numberOfRooms",
photoList = emptyList(),
videoUri = null,
address = "address",
nearbyPoint = "nearByPoint",
nearbyPointList = emptyList<String>(),
entryDate = "entryDate",
saleDate = "SaleDate",
agent = null,
agentList = emptyList<Agent>(),
*/
