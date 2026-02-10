package com.openclassrooms.realestatemanagerv2.ui.composables

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Video

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContentOnePane(
    paddingValues: PaddingValues,

    title: String,
    onCreatePropertyClick: () -> Unit,
    errorMessage: String?,
    onDismissError: () -> Unit,

    photoUri: String,
    photoDescription: String,
    onPhotoUriChange: (String) -> Unit,
    onPhotoDescriptionChange: (String) -> Unit,
    onPhotoDeleteClick: (Media) -> Unit,
    onAddPhotoDescriptionClick: () -> Unit,
    onDismissAddPhotoDescriptionDialog: () -> Unit,

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
    agent: Agent?,
    agentList: List<Agent>,
    showSaveButton: Boolean?,
    //DatePickers
    selectedEntryDate: Long?,
    isEntryDateDialogShown: Boolean,
    onShowEntryDateDialog: () -> Unit,
    onDismissEntryDateDialog: () -> Unit,
    onEntryDateSelected: (Long?) -> Unit,
    entryDatePickerState: DatePickerState,

    //SaleDatePicker
    selectedSaleDate: Long?,
    isSaleDateDialogShown: Boolean,
    onShowSaleDateDialog: () -> Unit,
    onDismissSaleDateDialog: () -> Unit,
    onSaleDateSelected: (Long?) -> Unit,
    saleDatePickerState: DatePickerState,

    // Error Values
    descriptionError: String,
    typeError: String,
    priceError: String,
    addressError: String,
    areaError: String,
    numberOfRoomsError: String,

// OnChange functions
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onNumberOfRoomsChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onNearbyPointChange: (PointOfInterest, Boolean) -> Unit,
    onAgentSelected: (Agent) -> Unit

) {


    var isVideoDisplayed by remember { mutableStateOf(false) }
    var videoUri: String by remember { mutableStateOf("") }

    val context = LocalContext.current

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

        AddPropertyAnimation(modifier = Modifier, iterations = 1)

        Text(
            text = title,
            fontSize = androidx.compose.material3.MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.fill_all_fields),
            fontStyle = FontStyle.Italic
        )

        DetailsMediaContent(
            photoList = photoList,
            videoList = videoList,
            isInEditMode = true,
            onPhotoDeleted = onPhotoDeleteClick,
            onPhotoClicked = {},
            onVideoDeleted = onVideoDeleteClick,
            onVideoClicked = { uri ->
                videoUri = uri
                isVideoDisplayed = true
            },
            context = context,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
                .align(CenterHorizontally)
                .fillMaxWidth()
        ) {
            CameraGalleryChooser(onPhotoSelected = onPhotoUriChange)

            VideoPicker(onVideoAdded = onVideoAdded)
        }

        if (photoUri != "") {
            AddPhotoDescriptionDialog(
                photoUri = photoUri,
                photoDescription = photoDescription,
                onPhotoUriChange = onPhotoUriChange,
                onPhotoDescriptionChange = onPhotoDescriptionChange,
                onConfirm = onAddPhotoDescriptionClick,
                onDismiss = onDismissAddPhotoDescriptionDialog
            )
        }

        AddTextFields(
            // Values
            description = description,
            type = type,
            price = price,
            area = area,
            numberOfRooms = numberOfRooms,
            // Error Values
            descriptionError = descriptionError,
            typeError = typeError,
            priceError = priceError,
            areaError = areaError,
            numberOfRoomsError = numberOfRoomsError,
            // OnChange functions
            onDescriptionChange = onDescriptionChange,
            onTypeChange = onTypeChange,
            onPriceChange = onPriceChange,
            onAreaChange = onAreaChange,
            onNumberOfRoomsChange = onNumberOfRoomsChange
        )

        AddPropertyInfos(
            // Values
            address = address,
            nearbyPointSelectedSet = nearbyPointSelectedSet,
            nearbyPointList = nearbyPointList,
            //DatePickers
            selectedEntryDate = selectedEntryDate,
            isEntryDateDialogShown = isEntryDateDialogShown,
            onShowEntryDateDialog = onShowEntryDateDialog,
            onDismissEntryDateDialog = onDismissEntryDateDialog,
            onEntryDateSelected = onEntryDateSelected,
            entryDatePickerState = entryDatePickerState,

            //SaleDatePicker
            selectedSaleDate = selectedSaleDate,
            isSaleDateDialogShown = isSaleDateDialogShown,
            onShowSaleDateDialog = onShowSaleDateDialog,
            onDismissSaleDateDialog = onDismissSaleDateDialog,
            onSaleDateSelected = onSaleDateSelected,
            saleDatePickerState = saleDatePickerState,
            // Error Values
            addressError = addressError,
            // OnChange functions

            onAddressChange = onAddressChange,
            onNearbyPointChange = onNearbyPointChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        TitleText(
            text = stringResource(id = R.string.agent),
            modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
        )

        AgentSpinner(
            modifier = Modifier.padding(8.dp),
            agents = agentList,
            selectedAgent = agent,
            onAgentSelected = onAgentSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (showSaveButton != null) {
            Button(
                onClick = onCreatePropertyClick,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(CenterHorizontally),
                enabled = showSaveButton
            ) {
                Text("Save this property")
            }
        }
    }

    if (isVideoDisplayed && videoUri.isNotBlank()) {
        VideoPlayer(
            videoUri = videoUri, context = context,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            onClose = { isVideoDisplayed = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun AddContentOnePanePreview() {
    var photoUri = "8"

    AddContentOnePane(
        paddingValues = PaddingValues(0.dp),
        title = stringResource(id = R.string.edit_this_property),
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

        photoUri = photoUri,
        photoDescription = "",
        onPhotoUriChange = {},
        onPhotoDescriptionChange = {},
        onPhotoDeleteClick = {},
        onAddPhotoDescriptionClick = {},
        onDismissAddPhotoDescriptionDialog = {photoUri = "" },

        onVideoUriChange = {},
        onVideoAdded = {},
        onVideoDeleteClick = {},

        onCreatePropertyClick = {})
}