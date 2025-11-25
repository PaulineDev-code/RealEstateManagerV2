package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchContentTwoPane(
    modifier: Modifier,
    minPrice: String,
    minPriceError: String,
    onMinPriceChange: (String) -> Unit,
    maxPrice: String,
    maxPriceError: String,
    onMaxPriceChange: (String) -> Unit,
    types: List<String>,
    selectedTypes: Set<String>,
    onTypeSelected: (String, Boolean) -> Unit,
    nearbyPointSelectedSet: Set<PointOfInterest>,
    nearbyPointList: List<PointOfInterest>,
    onNearbyPointChange: (PointOfInterest, Boolean) -> Unit,
    minArea: String,
    minAreaError: String,
    onMinAreaChange: (String) -> Unit,
    maxArea: String,
    maxAreaError: String,
    onMaxAreaChange: (String) -> Unit,
    minNumberOfRooms: String,
    minNumberOfRoomsError: String,
    onMinNumberOfRoomsChange: (String) -> Unit,
    maxNumberOfRooms: String,
    maxNumberOfRoomsError: String,
    onMaxNumberOfRoomsChange: (String) -> Unit,
    minPhoto: String,
    minPhotoError: String,
    onMinPhotoChange: (String) -> Unit,
    minVideo: String,
    minVideoError: String,
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

    isSearchClickEnabled: Boolean,
    onSearchClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Column(modifier = Modifier.width(350.dp)) {
            SearchHeaderAnimation()
        }

        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.search_screen_introduction),
            fontStyle = FontStyle.Italic
        )
        FlowRow {

            SearchMinMaxElement(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(350.dp),
                title = stringResource(R.string.min_and_max_price),
                minValue = minPrice,
                maxValue = maxPrice,
                onMinValueChange = onMinPriceChange,
                onMaxValueChange = onMaxPriceChange,
                minValueLabel = stringResource(id = R.string.min_price),
                maxValueLabel = stringResource(id = R.string.max_price),
                minValuePlaceHolder = stringResource(id = R.string.min_price),
                maxValuePlaceHolder = stringResource(id = R.string.max_price),
                minValueError = minPriceError,
                maxValueError = maxPriceError
            )

            SearchMinMaxElement(
                modifier = Modifier.width(350.dp),
                title = stringResource(R.string.min_number_of_medias),
                minValue = minPhoto,
                maxValue = minVideo,
                onMinValueChange = onMinPhotoChange,
                onMaxValueChange = onMinVideoChange,
                minValueLabel = stringResource(id = R.string.min_photo),
                maxValueLabel = stringResource(id = R.string.min_video),
                minValuePlaceHolder = stringResource(id = R.string.min_photo),
                maxValuePlaceHolder = stringResource(id = R.string.min_video),
                minValueError = minPhotoError,
                maxValueError = minVideoError
            )

            SearchMinMaxElement(
                modifier = Modifier.width(350.dp),
                title = stringResource(R.string.min_and_max_area),
                minValue = minArea,
                maxValue = maxArea,
                onMinValueChange = onMinAreaChange,
                onMaxValueChange = onMaxAreaChange,
                minValueLabel = stringResource(id = R.string.min_area),
                maxValueLabel = stringResource(id = R.string.max_area),
                minValuePlaceHolder = stringResource(id = R.string.min_area),
                maxValuePlaceHolder = stringResource(id = R.string.max_area),
                minValueError = minAreaError,
                maxValueError = maxAreaError
            )


            SearchMinMaxElement(
                modifier = Modifier.width(350.dp),
                title = stringResource(R.string.min_and_max_number_of_rooms),
                minValue = minNumberOfRooms,
                maxValue = maxNumberOfRooms,
                onMinValueChange = onMinNumberOfRoomsChange,
                onMaxValueChange = onMaxNumberOfRoomsChange,
                minValueLabel = stringResource(id = R.string.min_rooms),
                maxValueLabel = stringResource(id = R.string.max_rooms),
                minValuePlaceHolder = stringResource(id = R.string.min_rooms),
                maxValuePlaceHolder = stringResource(id = R.string.max_rooms),
                minValueError = minNumberOfRoomsError,
                maxValueError = maxNumberOfRoomsError
            )

            Column() {
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
                        .width(350.dp)
                )
            }

            Column() {
                TitleText(
                    text = stringResource(id = R.string.select_points_of_interest),
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 24.dp)
                )

                PointsOfInterestDropdown(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(350.dp),
                    allPointOfInterestList = nearbyPointList,
                    selectedPointOfInterestSet = nearbyPointSelectedSet,
                    onSelectionChanged = onNearbyPointChange
                )
            }



                Column() {
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
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Column() {

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
                        modifier = Modifier.padding(8.dp)
                    )
                }

            Column() {
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
            }
        }
        Button(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .align(CenterHorizontally),
            onClick = onSearchClicked,
            enabled = isSearchClickEnabled
        ) {
            Text(stringResource(id = R.string.search_for_properties))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = -1, widthDp = 840, heightDp = 480)
@Composable
fun SearchContentTwoPanePreview() {
    SearchContentTwoPane(
        modifier = Modifier.fillMaxWidth(),
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
        minPrice = "",
        minPriceError = "",
        onMinPriceChange = {},
        maxPrice = "",
        maxPriceError = "",
        onMaxPriceChange = {},
        types = listOf("type1", "type2"),
        selectedTypes = setOf("type1"),
        onTypeSelected = { _, _ -> },
        nearbyPointSelectedSet = setOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
        nearbyPointList = emptyList(),
        onNearbyPointChange = { _, _ -> },
        minArea = "",
        minAreaError = "",
        onMinAreaChange = {},
        maxArea = "",
        maxAreaError = "",
        onMaxAreaChange = {},
        minNumberOfRooms = "",
        minNumberOfRoomsError = "",
        onMinNumberOfRoomsChange = {},
        maxNumberOfRooms = "",
        maxNumberOfRoomsError = "",
        onMaxNumberOfRoomsChange = {},
        minPhoto = "",
        minPhotoError = "",
        onMinPhotoChange = {},
        minVideo = "",
        minVideoError = "",
        onMinVideoChange = {},
        agentList = listOf(Agent("1", "John", "Doe", "test@gmail.com")),
        agent = Agent("1", "John", "Doe", "test@gmail.com"),
        onAgentSelected = {},
        isSearchClickEnabled = true,
        onSearchClicked = {}
    )
}