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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.DoubleBackToExitHandler
import com.openclassrooms.realestatemanagerv2.ui.composables.ErrorStateContent
import com.openclassrooms.realestatemanagerv2.ui.composables.SearchContentOnePane
import com.openclassrooms.realestatemanagerv2.ui.composables.SearchContentTwoPane
import com.openclassrooms.realestatemanagerv2.ui.states.SearchPropertiesUiState
import com.openclassrooms.realestatemanagerv2.viewmodels.SearchPropertiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navController: NavController,
    onNavigateToAdd: () -> Unit,
    searchPropertiesViewModel: SearchPropertiesViewModel = hiltViewModel()
) {

    val uiState by searchPropertiesViewModel.uiState.collectAsStateWithLifecycle()
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

        when (val state = uiState) {
            is SearchPropertiesUiState.Error -> {
                ErrorStateContent(state.message)
            }

            is SearchPropertiesUiState.Editing -> {

                if (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
                    SearchContentTwoPane(
                        modifier = Modifier
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState()),
                        minPrice = state.minPrice.value,
                        minPriceError = state.minPrice.error ?: "",
                        onMinPriceChange = { newMinPrice ->
                            searchPropertiesViewModel.updateMinPrice(
                                newMinPrice
                            )
                        },
                        maxPrice = state.maxPrice.value,
                        maxPriceError = state.maxPrice.error ?: "",
                        onMaxPriceChange = { newMaxPrice ->
                            searchPropertiesViewModel.updateMaxPrice(
                                newMaxPrice
                            )
                        },
                        types = state.allTypes,
                        selectedTypes = state.typeSet ,
                        onTypeSelected = { type, isSelected ->
                            searchPropertiesViewModel.updateTypeSelection(
                                type,
                                isSelected
                            )
                        },
                        nearbyPointSelectedSet = state.nearbyPointSet,
                        nearbyPointList = searchPropertiesViewModel.allPointOfInterestList,
                        onNearbyPointChange = { pointOfInterest, isSelected ->
                            searchPropertiesViewModel.updatePointOfInterestSelection(
                                pointOfInterest,
                                isSelected
                            )
                        },
                        minArea = state.minArea.value,
                        minAreaError = state.minArea.error ?: "",
                        onMinAreaChange = { newMinArea ->
                            searchPropertiesViewModel.updateMinArea(
                                newMinArea
                            )
                        },
                        maxArea = state.maxArea.value,
                        maxAreaError = state.maxArea.error ?: "",
                        onMaxAreaChange = { newMaxArea ->
                            searchPropertiesViewModel.updateMaxArea(
                                newMaxArea
                            )
                        },
                        minNumberOfRooms = state.minNumberOfRooms.value,
                        minNumberOfRoomsError = state.minNumberOfRooms.error ?: "",
                        onMinNumberOfRoomsChange = { newMinNumberOfRooms ->
                            searchPropertiesViewModel.updateMinNumberOfRooms(newMinNumberOfRooms)
                        },
                        maxNumberOfRooms = state.maxNumberOfRooms.value,
                        maxNumberOfRoomsError = state.maxNumberOfRooms.error ?: "",
                        onMaxNumberOfRoomsChange = { newMaxNumberOfRooms ->
                            searchPropertiesViewModel.updateMaxNumberOfRooms(newMaxNumberOfRooms)
                        },
                        minPhotos = state.minPhotos.value,
                        minPhotosError = state.minPhotos.error ?: "",
                        onMinPhotosChange = { newMinPhotos ->
                            searchPropertiesViewModel.updateMinPhotos(
                                newMinPhotos
                            )
                        },
                        minVideos = state.minVideos.value,
                        minVideosError = state.minVideos.error ?: "",
                        onMinVideosChange = { newMinVideos ->
                            searchPropertiesViewModel.updateMinVideos(
                                newMinVideos
                            )
                        },
                        //date Picker for entry and sale date
                        selectedEntryDate = state.entryDate,
                        onEntryDateSelected = { newEntryDate ->
                            searchPropertiesViewModel.updateEntryDate(
                                newEntryDate
                            )
                        },
                        isEntryDateDialogShown = state.isEntryDatePickerShown,
                        onShowEntryDateDialog = {
                            searchPropertiesViewModel.updateEntryDateDialogShown(
                                true
                            )
                        },
                        onDismissEntryDateDialog = {
                            searchPropertiesViewModel.updateEntryDateDialogShown(
                                false
                            )
                        },
                        entryDatePickerState = rememberDatePickerState(),
                        selectedSaleDate = state.saleDate,
                        onSaleDateSelected = { newSaleDate ->
                            searchPropertiesViewModel.updateSaleDate(
                                newSaleDate
                            )
                        },
                        isSaleDateDialogShown = state.isSaleDatePickerShown,
                        onShowSaleDateDialog = {
                            searchPropertiesViewModel.updateSaleDateDialogShown(
                                true
                            )
                        },
                        onDismissSaleDateDialog = {
                            searchPropertiesViewModel.updateSaleDateDialogShown(
                                false
                            )
                        },
                        saleDatePickerState = rememberDatePickerState(),

                        agent = state.agent,
                        agentList = state.agentList,
                        onAgentSelected = { searchPropertiesViewModel.updateAgent(it) },
                        onResetAgent = { searchPropertiesViewModel.updateAgent(null) },

                        isSearchClickEnabled = state.isFormValid,
                        onSearchClicked = {
                            val criterias = searchPropertiesViewModel.getCurrentCriteria()
                            if (criterias != null) {
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    "Criterias",
                                    criterias
                                )
                                navController.popBackStack()
                            }
                        }
                    )
                } else {
                    SearchContentOnePane(
                        modifier = Modifier
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState()),
                        minPrice = state.minPrice.value,
                        minPriceError = state.minPrice.error ?: "",
                        onMinPriceChange = { newMinPrice ->
                            searchPropertiesViewModel.updateMinPrice(
                                newMinPrice
                            )
                        },
                        maxPrice = state.maxPrice.value,
                        maxPriceError = state.maxPrice.error ?: "",
                        onMaxPriceChange = { newMaxPrice ->
                            searchPropertiesViewModel.updateMaxPrice(
                                newMaxPrice
                            )
                        },
                        types = state.allTypes,
                        selectedTypes = state.typeSet,
                        onTypeSelected = { type, isSelected ->
                            searchPropertiesViewModel.updateTypeSelection(
                                type,
                                isSelected
                            )
                        },
                        nearbyPointSelectedSet = state.nearbyPointSet,
                        nearbyPointList = searchPropertiesViewModel.allPointOfInterestList,
                        onNearbyPointChange = { pointOfInterest, isSelected ->
                            searchPropertiesViewModel.updatePointOfInterestSelection(
                                pointOfInterest,
                                isSelected
                            )
                        },
                        minArea = state.minArea.value,
                        minAreaError = state.minArea.error ?: "",
                        onMinAreaChange = { newMinArea ->
                            searchPropertiesViewModel.updateMinArea(
                                newMinArea
                            )
                        },
                        maxArea = state.maxArea.value,
                        maxAreaError = state.maxArea.error ?: "",
                        onMaxAreaChange = { newMaxArea ->
                            searchPropertiesViewModel.updateMaxArea(
                                newMaxArea
                            )
                        },
                        minNumberOfRooms = state.minNumberOfRooms.value,
                        minNumberOfRoomsError = state.minNumberOfRooms.error ?: "",
                        onMinNumberOfRoomsChange = { newMinNumberOfRooms ->
                            searchPropertiesViewModel.updateMinNumberOfRooms(newMinNumberOfRooms)
                        },
                        maxNumberOfRooms = state.maxNumberOfRooms.value,
                        maxNumberOfRoomsError = state.maxNumberOfRooms.error ?: "",
                        onMaxNumberOfRoomsChange = { newMaxNumberOfRooms ->
                            searchPropertiesViewModel.updateMaxNumberOfRooms(newMaxNumberOfRooms)
                        },
                        minPhotos = state.minPhotos.value,
                        minPhotosError = state.minPhotos.error ?: "",
                        onMinPhotosChange = { newMinPhoto ->
                            searchPropertiesViewModel.updateMinPhotos(
                                newMinPhoto
                            )
                        },
                        minVideos = state.minVideos.value,
                        minVideosError = state.minVideos.error ?: "",
                        onMinVideosChange = { newMinVideo ->
                            searchPropertiesViewModel.updateMinVideos(
                                newMinVideo
                            )
                        },
                        //date Picker for entry and sale date
                        selectedEntryDate = state.entryDate,
                        onEntryDateSelected = { newEntryDate ->
                            searchPropertiesViewModel.updateEntryDate(
                                newEntryDate
                            )
                        },
                        isEntryDateDialogShown = state.isEntryDatePickerShown,
                        onShowEntryDateDialog = {
                            searchPropertiesViewModel.updateEntryDateDialogShown(
                                true
                            )
                        },
                        onDismissEntryDateDialog = {
                            searchPropertiesViewModel.updateEntryDateDialogShown(
                                false
                            )
                        },
                        entryDatePickerState = rememberDatePickerState(),
                        selectedSaleDate = state.saleDate,
                        onSaleDateSelected = { newSaleDate ->
                            searchPropertiesViewModel.updateSaleDate(
                                newSaleDate
                            )
                        },
                        isSaleDateDialogShown = state.isSaleDatePickerShown,
                        onShowSaleDateDialog = {
                            searchPropertiesViewModel.updateSaleDateDialogShown(
                                true
                            )
                        },
                        onDismissSaleDateDialog = {
                            searchPropertiesViewModel.updateSaleDateDialogShown(
                                false
                            )
                        },
                        saleDatePickerState = rememberDatePickerState(),

                        agent = state.agent,
                        agentList = state.agentList,
                        onAgentSelected = { searchPropertiesViewModel.updateAgent(it) },
                        onResetAgent = { searchPropertiesViewModel.updateAgent(null) },

                        isSearchClickEnabled = state.isFormValid,
                        onSearchClicked = {
                            val criterias = searchPropertiesViewModel.getCurrentCriteria()
                            if (criterias != null) {
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    "Criterias",
                                    criterias
                                )
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }
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
        minPhotos = "",
        minPhotosError = "",
        onMinPhotosChange = {},
        minVideos = "",
        minVideosError = "",
        onMinVideosChange = {},
        agentList = listOf(Agent("1", "John", "Doe", "test@gmail.com")),
        agent = Agent("1", "John", "Doe", "test@gmail.com"),
        onAgentSelected = {},
        onResetAgent = {},
        isSearchClickEnabled = true,
        onSearchClicked = {}
    )
}