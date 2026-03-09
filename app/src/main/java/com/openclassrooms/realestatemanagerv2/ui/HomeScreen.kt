package com.openclassrooms.realestatemanagerv2.ui

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsContent
import com.openclassrooms.realestatemanagerv2.ui.composables.DoubleBackToExitHandler
import com.openclassrooms.realestatemanagerv2.ui.composables.ErrorStateContent
import com.openclassrooms.realestatemanagerv2.ui.composables.HomeContent
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyDetailsUiState
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyUiState
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navController: NavController,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (propertyId: String) -> Unit,
    listViewModel: PropertySharedViewModel,
    detailsViewModel: PropertyDetailsViewModel
) {
    // Collect UI state from ViewModel
    val listUiState by listViewModel.uiState.collectAsStateWithLifecycle()
    val successListState = listUiState as? PropertyUiState.Success

    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()

    // State for video player visibility and URL, managed within this stateful composable
    var isVideoDisplayed by remember { mutableStateOf(false) }
    var currentVideoUrl by remember { mutableStateOf("") }

    val propertyIdToDisplay: String? = successListState?.addedPropertyId
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val criterias = savedStateHandle?.get<PropertySearchCriteria>("Criterias")

    val isListAndDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded &&
                navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

    val topBarTitle = when {
        (navigator.currentDestination?.contentKey == null
                || isListAndDetailVisible)
                && successListState?.isFiltered == true -> stringResource(R.string.filtered_list)

        navigator.currentDestination?.contentKey == null
                || isListAndDetailVisible -> stringResource(R.string.list)

        else -> stringResource(R.string.property_details)
    }

    //Track latest close version of the detail pane
    val closeVersion = successListState?.detailPaneCloseVersion ?: 0
    var lastHandledVersion by rememberSaveable { mutableIntStateOf(0) }
    val networkStatus = successListState?.networkStatus

    val activity = LocalContext.current as? Activity

    LaunchedEffect(networkStatus) {
        if (networkStatus == NetworkStatus.Available) {
            listViewModel.updateAndRefreshIfNeeded()
        }
    }

    LaunchedEffect(closeVersion) {
        if (closeVersion > lastHandledVersion) {
            if (navigator.canNavigateBack(BackNavigationBehavior.PopUntilCurrentDestinationChange)) {
                navigator.navigateBack(BackNavigationBehavior.PopUntilCurrentDestinationChange)
            }
            lastHandledVersion = closeVersion
        }
    }

    LaunchedEffect(propertyIdToDisplay) {
        if (propertyIdToDisplay != null) {
            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, propertyIdToDisplay)
            listViewModel.updateAddedProperty(null)
        }
    }

    LaunchedEffect(criterias) {
        if (criterias != null) {
            listViewModel.searchProperties(criterias)
            savedStateHandle.remove<PropertySearchCriteria>("Criterias")
        }
    }

    val navBarsColor = if (
        successListState?.isFiltered == true
    ) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    DoubleBackToExitHandler(
        enabled = !navigator.canNavigateBack(),
        message = stringResource(R.string.press_again_to_exit),
        exit = { activity?.finish() }
    )

    AppTopBar(
        title = topBarTitle,
        showEraseFiltersButton = successListState?.isFiltered == true
                && (navigator.currentDestination?.contentKey == null || isListAndDetailVisible),
        onEraseFiltersClick = { listViewModel.resetProperties() },
        showUpButton = navigator.currentDestination?.contentKey != null && !isListAndDetailVisible,
        onUpClick = { scope.launch { navigator.navigateBack() } },
        onAddClick = onNavigateToAdd,
        showModifyButton = navigator.currentDestination?.contentKey != null,
        onModifyClick = { navigator.currentDestination?.contentKey?.let { onNavigateToEdit(it) } },
        showNetworkWarning = successListState?.networkStatus == NetworkStatus.Unavailable,
        navBarsColor = navBarsColor
    ) { innerPadding ->

        NavigableListDetailPaneScaffold(
            navigator = navigator,
            defaultBackBehavior = if (isListAndDetailVisible) {
                BackNavigationBehavior.PopUntilContentChange
            } else {
                BackNavigationBehavior.PopUntilScaffoldValueChange
            },
            modifier = Modifier.padding(innerPadding),
            listPane = {

                AnimatedPane(modifier = Modifier) {
                    when (val state = listUiState) {
                        is PropertyUiState.Loading -> {
                            Box(
                                Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is PropertyUiState.Error -> {
                            ErrorStateContent(
                                message = state.message,
                                onRetry = { listViewModel.resetProperties() }
                            )
                        }

                        is PropertyUiState.Success -> {
                            HomeContent(
                                properties = state.properties,
                                isFiltered = state.isFiltered,
                                innerPadding = PaddingValues(0.dp),
                                itemIdSelected = navigator.currentDestination?.contentKey ?: "",
                                onPropertyItemClick = { propertyId ->
                                    scope.launch {
                                        navigator.navigateTo(
                                            ListDetailPaneScaffoldRole.Detail,
                                            propertyId
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            },
            detailPane = {
                AnimatedPane(modifier = Modifier) {
                    val detailsUiState by detailsViewModel.uiState.collectAsStateWithLifecycle()
                    val propertyId = navigator.currentDestination?.contentKey
                    LaunchedEffect(propertyId) {
                        propertyId?.let { id ->
                            detailsViewModel.getPropertyById(id)
                        }
                    }
                    when(val state = detailsUiState) {
                        is PropertyDetailsUiState.Loading -> {
                            Box(
                                Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is PropertyDetailsUiState.Error -> {
                            ErrorStateContent(
                                message = state.message,
                                onRetry = {
                                    propertyId?.let { id ->
                                        detailsViewModel.getPropertyById(id)
                                    }
                                })
                        }

                        is PropertyDetailsUiState.Success -> {
                            val property = state.property

                            if (propertyId != null && property != null) {
                                DetailsContent(
                                    property = property,
                                    innerPadding = PaddingValues(0.dp),
                                    onPhotoClicked = { photoIndex ->
                                        detailsViewModel.updateSelectedPhotoIndex(photoIndex)
                                        detailsViewModel.updatePhotoViewerShown(true)
                                    },
                                    onPhotoViewerClosed = {
                                        detailsViewModel.updatePhotoViewerShown(false)
                                    },
                                    isPhotoViewerDisplayed = state.isPhotoViewerShown,
                                    selectedPhotoIndex = state.selectedPhotoIndex,
                                    onPhotoIndexChanged = { index ->
                                        detailsViewModel.updateSelectedPhotoIndex(index)
                                    },
                                    onVideoClicked = { videoUrl ->
                                        currentVideoUrl = videoUrl
                                        isVideoDisplayed = true
                                    },
                                    onVideoPlayerClosed = {
                                        currentVideoUrl = ""
                                        isVideoDisplayed = false
                                    },
                                    isVideoDisplayed = isVideoDisplayed,
                                    currentVideoUrl = currentVideoUrl
                                )
                            } else if (isListAndDetailVisible) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(stringResource(R.string.select_property_to_details))
                                }
                            } else {
                                Box(Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            },
            paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
            paneExpansionDragHandle = {}
        )
    }
}