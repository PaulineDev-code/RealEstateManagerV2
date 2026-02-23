package com.openclassrooms.realestatemanagerv2.ui

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.DoubleBackToExitHandler
import com.openclassrooms.realestatemanagerv2.ui.composables.SearchContentOnePane
import com.openclassrooms.realestatemanagerv2.ui.composables.SearchContentTwoPane
import com.openclassrooms.realestatemanagerv2.viewmodels.SearchPropertiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navController: NavController,
    onNavigateToAdd: () -> Unit,
    searchPropertiesViewModel: SearchPropertiesViewModel = hiltViewModel()
) {

    val state = searchPropertiesViewModel.uiState.collectAsState().value
    val editingState = state as? SearchPropertiesViewModel.SearchPropertiesUiState.Editing
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (state is SearchPropertiesViewModel.SearchPropertiesUiState.Error) {
        val errorState = state as SearchPropertiesViewModel.SearchPropertiesUiState.Error
        when (val error = errorState.error) {

            is SearchPropertiesViewModel.SearchPropertiesError.GeneralError -> {
                errorMessage = error.exception.message ?: "unknown general error"
            }

            is SearchPropertiesViewModel.SearchPropertiesError.FieldError -> {
                TODO()
            }
        }
    }

    val activity = LocalContext.current as? Activity

    DoubleBackToExitHandler(
        enabled = true,
        message = stringResource(R.string.press_again_to_exit),
        exit = { activity?.finish() }
    )

    AppTopBar(
        title = stringResource(R.string.search),
        onAddClick = onNavigateToAdd,
        navBarsColor = MaterialTheme.colorScheme.surfaceVariant
    ) { paddingValues ->

        if(windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
            SearchContentTwoPane(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                minPrice = editingState?.minPrice?.value ?: "",
                minPriceError = editingState?.minPrice?.error ?: "",
                onMinPriceChange = { newMinPrice ->
                    searchPropertiesViewModel.updateMinPrice(
                        newMinPrice
                    )
                },
                maxPrice = editingState?.maxPrice?.value ?: "",
                maxPriceError = editingState?.maxPrice?.error ?: "",
                onMaxPriceChange = { newMaxPrice ->
                    searchPropertiesViewModel.updateMaxPrice(
                        newMaxPrice
                    )
                },
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
                    searchPropertiesViewModel.updatePointOfInterestSelection(
                        pointOfInterest,
                        isSelected
                    )
                },
                minArea = editingState?.minArea?.value ?: "",
                minAreaError = editingState?.minArea?.error ?: "",
                onMinAreaChange = { newMinArea -> searchPropertiesViewModel.updateMinArea(newMinArea) },
                maxArea = editingState?.maxArea?.value ?: "",
                maxAreaError = editingState?.maxArea?.error ?: "",
                onMaxAreaChange = { newMaxArea -> searchPropertiesViewModel.updateMaxArea(newMaxArea) },
                minNumberOfRooms = editingState?.minNumberOfRooms?.value ?: "",
                minNumberOfRoomsError = editingState?.minNumberOfRooms?.error ?: "",
                onMinNumberOfRoomsChange = { newMinNumberOfRooms ->
                    searchPropertiesViewModel.updateMinNumberOfRooms(newMinNumberOfRooms)
                },
                maxNumberOfRooms = editingState?.maxNumberOfRooms?.value ?: "",
                maxNumberOfRoomsError = editingState?.maxNumberOfRooms?.error ?: "",
                onMaxNumberOfRoomsChange = { newMaxNumberOfRooms ->
                    searchPropertiesViewModel.updateMaxNumberOfRooms(newMaxNumberOfRooms)
                },
                minPhoto = editingState?.minPhotos?.value ?: "",
                minPhotoError = editingState?.minPhotos?.error ?: "",
                onMinPhotoChange = { newMinPhoto ->
                    searchPropertiesViewModel.updateMinPhotos(
                        newMinPhoto
                    )
                },
                minVideo = editingState?.minVideos?.value ?: "",
                minVideoError = editingState?.minVideos?.error ?: "",
                onMinVideoChange = { newMinVideo ->
                    searchPropertiesViewModel.updateMinVideos(
                        newMinVideo
                    )
                },
                //date Picker for entry and sale date
                selectedEntryDate = editingState?.entryDate,
                onEntryDateSelected = { newEntryDate ->
                    searchPropertiesViewModel.updateEntryDate(
                        newEntryDate
                    )
                },
                isEntryDateDialogShown = editingState?.isEntryDatePickerShown ?: false,
                onShowEntryDateDialog = { searchPropertiesViewModel.updateEntryDateDialogShown(true) },
                onDismissEntryDateDialog = {
                    searchPropertiesViewModel.updateEntryDateDialogShown(
                        false
                    )
                },
                entryDatePickerState = rememberDatePickerState(),
                selectedSaleDate = editingState?.saleDate,
                onSaleDateSelected = { newSaleDate ->
                    searchPropertiesViewModel.updateSaleDate(
                        newSaleDate
                    )
                },
                isSaleDateDialogShown = editingState?.isSaleDatePickerShown ?: false,
                onShowSaleDateDialog = { searchPropertiesViewModel.updateSaleDateDialogShown(true) },
                onDismissSaleDateDialog = {
                    searchPropertiesViewModel.updateSaleDateDialogShown(
                        false
                    )
                },
                saleDatePickerState = rememberDatePickerState(),

                agent = editingState?.agent,
                agentList = editingState?.agentList ?: emptyList(),
                onAgentSelected = { searchPropertiesViewModel.updateAgent(it) },

                isSearchClickEnabled = editingState?.isFormValid ?: false,
                onSearchClicked = {
                    val criterias = searchPropertiesViewModel.getCurrentCriteria()
                    navController.previousBackStackEntry?.savedStateHandle?.set("Criterias", criterias)
                    navController.popBackStack()
                }
            )
        } else {
            SearchContentOnePane(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                minPrice = editingState?.minPrice?.value ?: "",
                minPriceError = editingState?.minPrice?.error ?: "",
                onMinPriceChange = { newMinPrice ->
                    searchPropertiesViewModel.updateMinPrice(
                        newMinPrice
                    )
                },
                maxPrice = editingState?.maxPrice?.value ?: "",
                maxPriceError = editingState?.maxPrice?.error ?: "",
                onMaxPriceChange = { newMaxPrice ->
                    searchPropertiesViewModel.updateMaxPrice(
                        newMaxPrice
                    )
                },
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
                    searchPropertiesViewModel.updatePointOfInterestSelection(
                        pointOfInterest,
                        isSelected
                    )
                },
                minArea = editingState?.minArea?.value ?: "",
                minAreaError = editingState?.minArea?.error ?: "",
                onMinAreaChange = { newMinArea -> searchPropertiesViewModel.updateMinArea(newMinArea) },
                maxArea = editingState?.maxArea?.value ?: "",
                maxAreaError = editingState?.maxArea?.error ?: "",
                onMaxAreaChange = { newMaxArea -> searchPropertiesViewModel.updateMaxArea(newMaxArea) },
                minNumberOfRooms = editingState?.minNumberOfRooms?.value ?: "",
                minNumberOfRoomsError = editingState?.minNumberOfRooms?.error ?: "",
                onMinNumberOfRoomsChange = { newMinNumberOfRooms ->
                    searchPropertiesViewModel.updateMinNumberOfRooms(newMinNumberOfRooms)
                },
                maxNumberOfRooms = editingState?.maxNumberOfRooms?.value ?: "",
                maxNumberOfRoomsError = editingState?.maxNumberOfRooms?.error ?: "",
                onMaxNumberOfRoomsChange = { newMaxNumberOfRooms ->
                    searchPropertiesViewModel.updateMaxNumberOfRooms(newMaxNumberOfRooms)
                },
                minPhoto = editingState?.minPhotos?.value ?: "",
                minPhotoError = editingState?.minPhotos?.error ?: "",
                onMinPhotoChange = { newMinPhoto ->
                    searchPropertiesViewModel.updateMinPhotos(
                        newMinPhoto
                    )
                },
                minVideo = editingState?.minVideos?.value ?: "",
                minVideoError = editingState?.minVideos?.error ?: "",
                onMinVideoChange = { newMinVideo ->
                    searchPropertiesViewModel.updateMinVideos(
                        newMinVideo
                    )
                },
                //date Picker for entry and sale date
                selectedEntryDate = editingState?.entryDate,
                onEntryDateSelected = { newEntryDate ->
                    searchPropertiesViewModel.updateEntryDate(
                        newEntryDate
                    )
                },
                isEntryDateDialogShown = editingState?.isEntryDatePickerShown ?: false,
                onShowEntryDateDialog = { searchPropertiesViewModel.updateEntryDateDialogShown(true) },
                onDismissEntryDateDialog = {
                    searchPropertiesViewModel.updateEntryDateDialogShown(
                        false
                    )
                },
                entryDatePickerState = rememberDatePickerState(),
                selectedSaleDate = editingState?.saleDate,
                onSaleDateSelected = { newSaleDate ->
                    searchPropertiesViewModel.updateSaleDate(
                        newSaleDate
                    )
                },
                isSaleDateDialogShown = editingState?.isSaleDatePickerShown ?: false,
                onShowSaleDateDialog = { searchPropertiesViewModel.updateSaleDateDialogShown(true) },
                onDismissSaleDateDialog = {
                    searchPropertiesViewModel.updateSaleDateDialogShown(
                        false
                    )
                },
                saleDatePickerState = rememberDatePickerState(),

                agent = editingState?.agent,
                agentList = editingState?.agentList ?: emptyList(),
                onAgentSelected = { searchPropertiesViewModel.updateAgent(it) },

                isSearchClickEnabled = editingState?.isFormValid ?: false,
                onSearchClicked = {
                    val criterias = searchPropertiesViewModel.getCurrentCriteria()
                    navController.previousBackStackEntry?.savedStateHandle?.set("Criterias", criterias)
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = false, backgroundColor = -1)
@Composable
fun SearchScreenPreview() {
    SearchContentOnePane(
        modifier = Modifier
            .wrapContentHeight()
            .verticalScroll(rememberScrollState()),
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
        onTypeSelected = { type, bool -> },
        nearbyPointSelectedSet = setOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
        nearbyPointList = emptyList(),
        onNearbyPointChange = { poi, bool -> },
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
