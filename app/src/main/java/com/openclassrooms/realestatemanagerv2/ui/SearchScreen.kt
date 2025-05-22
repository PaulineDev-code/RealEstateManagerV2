package com.openclassrooms.realestatemanagerv2.ui

import android.content.res.Resources.Theme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.ui.composables.AgentSpinner
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.CustomDatePicker
import com.openclassrooms.realestatemanagerv2.ui.composables.CustomRangeSlider
import com.openclassrooms.realestatemanagerv2.ui.composables.CustomTextField
import com.openclassrooms.realestatemanagerv2.ui.composables.PointsOfInterestDropdown
import com.openclassrooms.realestatemanagerv2.ui.composables.PropertyTypeCheckBox
import com.openclassrooms.realestatemanagerv2.ui.composables.SearchHeaderAnimation
import com.openclassrooms.realestatemanagerv2.ui.composables.TitleText
import com.openclassrooms.realestatemanagerv2.ui.composables.TypewriterText
import com.openclassrooms.realestatemanagerv2.viewmodels.SearchPropertiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    searchPropertiesViewModel: SearchPropertiesViewModel = hiltViewModel()
) {

    val state = searchPropertiesViewModel.uiState.collectAsState().value
    val editingState = state as? SearchPropertiesViewModel.SearchPropertiesUiState.Editing
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (state is SearchPropertiesViewModel.SearchPropertiesUiState.Error) {
        val errorState = state as SearchPropertiesViewModel.SearchPropertiesUiState.Error
        when (val error = errorState.error) {

            is SearchPropertiesViewModel.SearchPropertiesError.GeneralError -> {
                // Vous pouvez afficher un message générique ou utiliser le message de l'exception
                errorMessage = error.exception.message ?: "unknown general error"
            }

            is SearchPropertiesViewModel.SearchPropertiesError.FieldError -> {
                TODO()
            }
            null -> TODO()
        }
    }

    SearchContent(
        navController = navController,
        minPrice = editingState?.minPrice?.value ?: "",
        onMinPriceChange = { newMinPrice -> searchPropertiesViewModel.updateMinPrice(newMinPrice) },
        maxPrice = editingState?.maxPrice?.value ?: "",
        onMaxPriceChange = { newMaxPrice -> searchPropertiesViewModel.updateMaxPrice(newMaxPrice) },
        types = editingState?.allTypes ?: emptyList(),
        selectedTypes = editingState?.typeSet ?: emptySet(),
        onTypeSelected = { type, isSelected ->
            searchPropertiesViewModel.updateTypeSelection(
                type,
                isSelected
            )
        },
        nearbyPointSelectedSet = editingState?.nearbyPointSet ?: emptySet(),
        nearbyPointList = searchPropertiesViewModel.allPointOfInterestList,
        onNearbyPointChange = { pointOfInterest, isSelected ->
            searchPropertiesViewModel.updatePointOfInterestSelection(pointOfInterest, isSelected)
        },
        areaSelectedRange = editingState?.areaRange ?: 30f..1000f,
        onAreaRangeChanged = { newAreaSelectedRange ->
            searchPropertiesViewModel.updateAreaRange(
                newAreaSelectedRange
            )
        },
        numberOfRooms = editingState?.numberOfRooms ?: 0f,
        onNumberOfRoomsChange = { newNumberOfRooms ->
            searchPropertiesViewModel.updateNumberOfRooms(newNumberOfRooms)
        },
        minPhoto = editingState?.minPhotos?.value ?: "",
        onMinPhotoChange = { newMinPhoto -> searchPropertiesViewModel.updateMinPhotos(newMinPhoto) },
        minVideo = editingState?.minVideos?.value ?: "",
        onMinVideoChange = { newMinVideo -> searchPropertiesViewModel.updateMinVideos(newMinVideo) },
        //date Picker for entry and sale
        selectedEntryDate = editingState?.entryDate,
        onEntryDateSelected = { newEntryDate ->
            searchPropertiesViewModel.updateEntryDate(
                newEntryDate
            )
        },
        isEntryDateDialogShown = editingState?.isEntryDatePickerShown ?: false,
        onShowEntryDateDialog = { searchPropertiesViewModel.updateEntryDateDialogShown(true) },
        onDismissEntryDateDialog = { searchPropertiesViewModel.updateEntryDateDialogShown(false) },
        entryDatePickerState = rememberDatePickerState(),
        selectedSaleDate = editingState?.saleDate,
        onSaleDateSelected = { newSaleDate -> searchPropertiesViewModel.updateSaleDate(newSaleDate) },
        isSaleDateDialogShown = editingState?.isSaleDatePickerShown ?: false,
        onShowSaleDateDialog = { searchPropertiesViewModel.updateSaleDateDialogShown(true) },
        onDismissSaleDateDialog = { searchPropertiesViewModel.updateSaleDateDialogShown(false) },
        saleDatePickerState = rememberDatePickerState(),

        agent = editingState?.agent,
        agentList = editingState?.agentList ?: emptyList(),
        onAgentSelected = { },

        onSearchClicked = {
            val criterias = searchPropertiesViewModel.getCurrentCriteria()
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("criterias", criterias)
            navController.navigate("home_screen")
        },
        modifier = Modifier
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    navController: NavController,
    minPrice: String,
    onMinPriceChange: (String) -> Unit,
    maxPrice: String,
    onMaxPriceChange: (String) -> Unit,
    types: List<String>,
    selectedTypes: Set<String>,
    onTypeSelected: (String, Boolean) -> Unit,
    nearbyPointSelectedSet: Set<PointOfInterest>,
    nearbyPointList: List<PointOfInterest>,
    onNearbyPointChange: (PointOfInterest, Boolean) -> Unit,
    areaSelectedRange: ClosedFloatingPointRange<Float>,
    onAreaRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    numberOfRooms: Float,
    onNumberOfRoomsChange: (Float) -> Unit,
    minPhoto: String,
    onMinPhotoChange: (String) -> Unit,
    minVideo: String,
    onMinVideoChange: (String) -> Unit,
    agent: Agent?,
    agentList: List<Agent>,
    onAgentSelected: (Agent) -> Unit,

    //EntryDatePicker
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

    onSearchClicked: () -> Unit,

    modifier: Modifier
) {
    AppTopBar(
        navController = navController,
        onNavigationClick = { /*TODO*/ },
        onModifyClick = { /*TODO*/ },
        showModifyButton = false,
        navBarsColor = MaterialTheme.colorScheme.surfaceVariant,
        showBottomBar = true
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {

            SearchHeaderAnimation()

            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = R.string.search_screen_introduction),
                fontStyle = FontStyle.Italic
            )

            TitleText(
                text = stringResource(R.string.min_and_max_price),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {

                CustomTextField(
                    label = {
                        Text(
                            text = "Min Price",
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    },
                    placeHolder = { Text(text = "Min Price") },
                    keyboardType = KeyboardType.Number,
                    text = minPrice,
                    onTextChange = onMinPriceChange,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                )

                CustomTextField(
                    label = {
                        Text(
                            text = "Max Price",
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    },
                    placeHolder = { Text(text = "Max Price") },
                    keyboardType = KeyboardType.Number,
                    text = maxPrice,
                    onTextChange = onMaxPriceChange,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                )

            }

            TitleText(
                text = stringResource(R.string.min_number_of_medias),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {

                CustomTextField(
                    label = {
                        Text(
                            text = "Min Photo",
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    },
                    placeHolder = { Text(text = "Min Photo") },
                    keyboardType = KeyboardType.Number,
                    text = minPhoto,
                    onTextChange = onMinPhotoChange,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                )

                CustomTextField(
                    label = {
                        Text(
                            text = "Min Video",
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    },
                    placeHolder = { Text(text = "Min Video") },
                    text = minVideo,
                    keyboardType = KeyboardType.Number,
                    onTextChange = onMinVideoChange,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                )

            }

            TitleText(
                text = stringResource(id = R.string.select_types_of_property),
                modifier = Modifier.padding(8.dp)
            )

            PropertyTypeCheckBox(
                types = types,
                selectedType = selectedTypes,
                onTypeSelected = onTypeSelected,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            TitleText(
                text = stringResource(id = R.string.select_points_of_interest),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 24.dp)
            )

            PointsOfInterestDropdown(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                allPointOfInterestList = nearbyPointList,
                selectedPointOfInterestSet = nearbyPointSelectedSet,
                onSelectionChanged = onNearbyPointChange
            )

            TitleText(
                text = stringResource(R.string.min_and_max_area),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 24.dp, bottom = 8.dp)
            )

            CustomRangeSlider(
                valueRange = 30f.rangeTo(1000f),
                selectedRange = areaSelectedRange,
                onRangeChanged = onAreaRangeChanged,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            TitleText(
                text = stringResource(id = R.string.min_number_of_rooms),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 24.dp)
            )

            Slider(
                value = numberOfRooms,
                onValueChange = onNumberOfRoomsChange,
                valueRange = 0f.rangeTo(50f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 64.dp, top = 8.dp)
            )
            Text(
                text = numberOfRooms.toInt().toString(),
                modifier = Modifier.padding(start = 16.dp)
            )

            TitleText(
                stringResource(id = R.string.min_date_of_entry),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 24.dp)
            )

            CustomDatePicker(
                selectedDateMillis = selectedEntryDate,
                isDialogShown = isEntryDateDialogShown,
                onShowDialog = onShowEntryDateDialog,
                onDismissDialog = onDismissEntryDateDialog,
                onDateSelected = onEntryDateSelected,
                datePickerState = entryDatePickerState,
                modifier = modifier.padding(8.dp)
            )

            TitleText(
                stringResource(R.string.min_date_of_sale),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 24.dp)
            )

            CustomDatePicker(
                selectedDateMillis = selectedSaleDate,
                isDialogShown = isSaleDateDialogShown,
                onShowDialog = onShowSaleDateDialog,
                onDismissDialog = onDismissSaleDateDialog,
                onDateSelected = onSaleDateSelected,
                datePickerState = saleDatePickerState,
                modifier = modifier.padding(8.dp)
            )

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

            Button(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .align(CenterHorizontally),
                onClick = onSearchClicked,
                enabled = true
            ) {
                Text(stringResource(id = R.string.search_for_properties))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = false, backgroundColor = -1)
@Composable
fun SearchScreenPreview() {
    SearchContent(
        navController = rememberNavController(),
        selectedEntryDate = 44738399,
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
        minPrice = "min price",
        onMinPriceChange = {},
        maxPrice = "maxPrice",
        onMaxPriceChange = {},
        types = listOf("type1", "type2"),
        selectedTypes = setOf("type1"),
        onTypeSelected = { type, bool -> },
        nearbyPointSelectedSet = setOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
        nearbyPointList = emptyList(),
        onNearbyPointChange = { poi, bool -> },
        areaSelectedRange = 30f.rangeTo(1000f),
        onAreaRangeChanged = {},
        numberOfRooms = 0f,
        onNumberOfRoomsChange = {},
        minPhoto = "minPhoto",
        onMinPhotoChange = {},
        minVideo = "minVideo",
        onMinVideoChange = {}, modifier = Modifier,
        agentList = listOf(Agent("1", "John", "Doe", "test@gmail.com")),
        agent = Agent("1", "John", "Doe", "test@gmail.com"),
        onAgentSelected = {},
        onSearchClicked = {}
    )
}
