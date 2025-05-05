package com.openclassrooms.realestatemanagerv2.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Video
import com.openclassrooms.realestatemanagerv2.ui.composables.AddPhotoDescriptionDialog
import com.openclassrooms.realestatemanagerv2.ui.composables.AddTextFields
import com.openclassrooms.realestatemanagerv2.ui.composables.AgentSpinner
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.CameraGalleryChooser
import com.openclassrooms.realestatemanagerv2.ui.composables.DebugRecompositions
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsMediaContent
import com.openclassrooms.realestatemanagerv2.ui.composables.VideoPlayer
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel

@Composable
fun AddScreen(
    navController: NavController,
    addViewModel: AddPropertyViewModel = hiltViewModel()
) {
    val state = addViewModel.uiState.collectAsState().value
    val editingState = state as? AddPropertyViewModel.AddPropertyUiState.Editing
    var errorMessage by remember { mutableStateOf<String?>(null) }

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

    AddContent(navController = navController,

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
            addViewModel.handleAction(
                AddScreenUiAction.OnPhotoDeleteClick(
                    photoToRemove
                )
            )
        },

        onAddPhotoDescriptionClick = { addViewModel.addPhoto() },

        onVideoUriChange = { newVideoUri -> addViewModel.updateVideoUri(newVideoUri) },
        onVideoAdded = { videoToAdd -> addViewModel.addVideo(videoToAdd)},
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
        entryDate = editingState?.entryDate?.value ?: "",
        saleDate = editingState?.saleDate?.value ?: "",
        agent = editingState?.agent,
        agentList = editingState?.agentList ?: emptyList(),
        showSaveButton = editingState?.isFormValid,

        // Error Values
        descriptionError = editingState?.description?.error ?: "",
        typeError = editingState?.type?.error ?: "",
        priceError = editingState?.price?.error ?: "",
        addressError = editingState?.address?.error ?: "",
        areaError = editingState?.area?.error ?: "",
        numberOfRoomsError = editingState?.numberOfRooms?.error ?: "",
        entryDateError = editingState?.entryDate?.error ?: "",
        saleDateError = editingState?.saleDate?.error ?: "",

        //On change callback functions
        onDescriptionChange = { newDescription -> addViewModel.updateDescription(newDescription) },
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
            addViewModel.updatePointOfInterestSelection(pointOfInterest, isSelected) },
        onEntryDateChange = { newEntryDate -> addViewModel.updateEntryDate(newEntryDate) },
        onSaleDateChange = { newSaleDate -> addViewModel.updateSaleDate(newSaleDate) },
        onAgentSelected = { selectedAgent -> addViewModel.updateAgent(selectedAgent) }

    )

}

@Composable
private fun AddContent(
    navController: NavController,

    onCreatePropertyClick: () -> Unit,
    errorMessage: String?,
    onDismissError: () -> Unit,

    photoUri: String,
    photoDescription: String,
    onPhotoUriChange: (String) -> Unit,
    onPhotoDescriptionChange: (String) -> Unit,
    onPhotoDeleteClick: (Media) -> Unit,
    onAddPhotoDescriptionClick: () -> Unit,

    onVideoUriChange: (String) -> Unit,
    onVideoAdded: (String) -> Unit,
    onVideoDeleteClick: (Media) -> Unit,

    //Values
    description: String,
    type: String,
    price: String,
    area: String,
    numberOfRooms: String,
    photoList: List<Photo>,
    videoList: List<Video>,
    address: String,
    nearbyPointSelectedSet: Set<PointOfInterest>,
    nearbyPointList: List<PointOfInterest>,
    entryDate: String,
    saleDate: String,
    agent: Agent?,
    agentList: List<Agent>,
    showSaveButton: Boolean?,

    // Error Values
    descriptionError: String,
    typeError: String,
    priceError: String,
    addressError: String,
    areaError : String,
    numberOfRoomsError: String,
    entryDateError: String,
    saleDateError: String,

// OnChange functions
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onNumberOfRoomsChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onNearbyPointChange: (PointOfInterest, Boolean) -> Unit,
    onEntryDateChange: (String) -> Unit,
    onSaleDateChange: (String) -> Unit,
    onAgentSelected: (Agent) -> Unit

) {


    AppTopBar(
        navController = navController,
        onNavigationClick = { /*TODO*/ },
        onSearchClick = { /*TODO*/ },
        onModifyClick = { /*TODO*/ },
        showModifyButton = false,
        showBottomBar = false
    ) { paddingValues ->
        var isVideoDisplayed by remember { mutableStateOf(false) }
        var videoUri: String by remember { mutableStateOf("") }

        val context = LocalContext.current

        val singleVideoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { videoPickedUri -> if(videoPickedUri.toString() != "") {
                onVideoAdded(videoPickedUri.toString())
            }
                /*onVideoUriChange(it.toString())*/
            })

        LaunchedEffect(errorMessage) {
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                onDismissError()
            }
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {

            DetailsMediaContent(
                photoList = photoList,
                videoList = videoList,
                isInEditMode = true,
                onPhotoDeleted = onPhotoDeleteClick,
                onVideoDeleted = onVideoDeleteClick,
                onVideoClicked = { uri ->
                    videoUri = uri
                    isVideoDisplayed = true
                },
                context = context
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceAround) {
                CameraGalleryChooser(onPhotoSelected = onPhotoUriChange)

                Button(
                    onClick = {
                        singleVideoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    }
                ) {
                    Text(text = "Import a video")
                }
            }

            if (photoUri != "") {
                AddPhotoDescriptionDialog(
                    photoUri = photoUri,
                    photoDescription = photoDescription,
                    onAddPhotoDescriptionClick = onAddPhotoDescriptionClick,
                    onPhotoUriChange = onPhotoUriChange,
                    onPhotoDescriptionChange = onPhotoDescriptionChange
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AddTextFields( // Values
                description = description,
                type = type,
                price = price,
                area = area,
                numberOfRooms = numberOfRooms,
                address = address,
                nearbyPointSelectedSet = nearbyPointSelectedSet,
                nearbyPointList = nearbyPointList,
                entryDate = entryDate,
                saleDate = saleDate,
                // Error Values
                descriptionError = descriptionError,
                typeError = typeError,
                priceError = priceError,
                addressError = addressError,
                areaError = areaError,
                numberOfRoomsError = numberOfRoomsError,
                entryDateError = entryDateError,
                saleDateError = saleDateError,
// OnChange functions
                onDescriptionChange = onDescriptionChange,
                onTypeChange = onTypeChange,
                onPriceChange = onPriceChange,
                onAreaChange = onAreaChange,
                onNumberOfRoomsChange = onNumberOfRoomsChange,
                onAddressChange = onAddressChange,
                onNearbyPointChange = onNearbyPointChange,
                onEntryDateChange = onEntryDateChange,
                onSaleDateChange = onSaleDateChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            AgentSpinner(
                agents = agentList,
                selectedAgent = agent,
                onAgentSelected = onAgentSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showSaveButton != null) {
                Button(onClick = onCreatePropertyClick,
                    modifier = Modifier.align(CenterHorizontally),
                    enabled = showSaveButton
                ) {
                    Text("Save this property")
                }
            }


        }

        if (isVideoDisplayed && videoUri.isNotBlank()) {
            VideoPlayer(videoUri = videoUri, context = context,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                onClose = { isVideoDisplayed = false })
        }

    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddScreenPreview() {
    AddContent(navController = rememberNavController(),
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
        entryDate = "entryDate",
        saleDate = "SaleDate",
        showSaveButton = true,
        // Error Values
        descriptionError = "descriptionError",
        typeError = "typeError",
        priceError = "priceError",
        addressError = "addressError",
        areaError = "areaError",
        numberOfRoomsError = "numberOfRoomsError",
        entryDateError = "entryDateError",
        saleDateError = "saleDateError",

        // OnChange functions

        onDescriptionChange = {},
        onTypeChange = {},
        onPriceChange = {},
        onAreaChange = {},
        onNumberOfRoomsChange = {},
        onAddressChange = {},
        onNearbyPointChange = {poi, bool ->},
        onEntryDateChange = {},
        onSaleDateChange = {},
        onAgentSelected = {},

        //Other functions

        errorMessage = "errorMessage",
        onDismissError = {},

        photoUri = "photoUri",
        photoDescription = "photoDescription",
        onPhotoUriChange = {},
        onPhotoDescriptionChange = {},
        onPhotoDeleteClick = {},
        onAddPhotoDescriptionClick = {},

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
