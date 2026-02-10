package com.openclassrooms.realestatemanagerv2.ui

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsContent
import com.openclassrooms.realestatemanagerv2.ui.composables.DoubleBackToExitHandler
import com.openclassrooms.realestatemanagerv2.ui.composables.PropertyListItem
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
    // Collect UI state from ViewModels
    val detailsUiState by detailsViewModel.uiState.collectAsStateWithLifecycle()
    val successDetailsState = detailsUiState as? PropertyDetailsViewModel.PropertyDetailsUiState.Success
    val listUiState by listViewModel.uiState.collectAsStateWithLifecycle()
    val successListState = listUiState as? PropertySharedViewModel.PropertyUiState.Success

    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()
    // State for video player visibility and URL, managed within this stateful composable
    var isVideoDisplayed by remember { mutableStateOf(false) }
    var currentVideoUrl by remember { mutableStateOf("") }

    val propertyIdToDisplay: String? = successListState?.addedPropertyId

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val criterias = savedStateHandle?.get<PropertySearchCriteria>("Criterias") //TODO: as const

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

    val activity = LocalContext.current as? Activity

    LaunchedEffect(closeVersion) {
        if (closeVersion > lastHandledVersion) {
            if (navigator.canNavigateBack(BackNavigationBehavior.PopUntilCurrentDestinationChange)) {
                navigator.navigateBack(BackNavigationBehavior.PopUntilCurrentDestinationChange)
            }
            lastHandledVersion = closeVersion
        }
    }

    LaunchedEffect(propertyIdToDisplay) {
        propertyIdToDisplay?.let { id ->
            scope.launch {
                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, id)
            }
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

    LifecycleResumeEffect(Unit) {
        Log.d("HomeScreenDebug", "HomeScreen resumed, refreshing property list")
        listViewModel.refreshProperties()
        onPauseOrDispose {}
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
                    HomeContent(
                        uiState = listUiState,
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
            },
            detailPane = {
                AnimatedPane(modifier = Modifier) {
                    val propertyId = navigator.currentDestination?.contentKey
                    LaunchedEffect(propertyId) {
                        propertyId?.let { id ->
                            detailsViewModel.getPropertyById(id)
                        }
                    }
                    if (propertyId != null) {
                        DetailsContent(
                            uiState = detailsUiState,
                            innerPadding = PaddingValues(0.dp),
                            onPhotoClicked = { photoIndex ->
                                detailsViewModel.updateSelectedPhotoIndex(photoIndex)
                                detailsViewModel.updatePhotoViewerShown(true)
                            },
                            onPhotoViewerClosed = {
                                detailsViewModel.updatePhotoViewerShown(false)
                            },
                            isPhotoViewerDisplayed = successDetailsState?.isPhotoViewerShown ?: false,
                            selectedPhotoIndex = successDetailsState?.selectedPhotoIndex ?: 0,
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
                    } else if (isListAndDetailVisible
                        && listUiState is PropertySharedViewModel.PropertyUiState.Success
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.select_property_to_details))
                            //TODO: review use of vm method here, might not work in all cases
                        }


                    } else {
                        Box(Modifier.fillMaxSize())
                    }
                }
            },
            paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
            paneExpansionDragHandle = {}
        )
    }
}

@Composable
fun HomeContent(
    uiState: PropertySharedViewModel.PropertyUiState,
    innerPadding: PaddingValues, // Padding from AppTopBar
    itemIdSelected: String,
    onPropertyItemClick: (propertyId: String) -> Unit
) {

    Box(modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()) {
        when (uiState) {
            is PropertySharedViewModel.PropertyUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is PropertySharedViewModel.PropertyUiState.Success ->
                if(uiState.properties.isEmpty() && uiState.isFiltered == true) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center),
                        text = stringResource(id = R.string.no_property_found),
                        fontStyle = FontStyle.Italic
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(items = uiState.properties) { _, item ->
                            PropertyListItem(
                                property = item,
                                isItemSelected = itemIdSelected == item.id,
                                onItemClick = { onPropertyItemClick(item.id) }
                            )
                        }
                    }
                }

            is PropertySharedViewModel.PropertyUiState.Error -> {
                val e = uiState.exception
                Text(
                    text = stringResource(id = R.string.error_loading_properties,
                        e.localizedMessage ?: "Unknown error"),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
                Log.e("HomeScreenContent", "PropertyUiState.Error: ${e.message}")
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    //Preview of the HomeScreen in success state and without filters
    val sampleProperties = listOf( Property("1",
        "Apartment",
        300000.0,
        90.0,
        3,
        "A spacious flat in the middle of Brooklyn "+"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed mollis, massa euismod tempor rhoncus, nulla neque luctus sapien, at mollis purus ligula in libero. Sed libero augue, consequat eu mauris a, pulvinar venenatis nunc. Donec faucibus ligula ac mattis luctus. Morbi purus urna, ullamcorper ac volutpat ac, sodales id nulla. Nunc ultrices nisi ex, eget lacinia purus suscipit congue. In quis facilisis nisl, vel pharetra leo. Vivamus mollis massa at ligula consequat lacinia eget a neque. Maecenas volutpat blandit purus luctus egestas. Donec et iaculis libero. Donec quis mi sed magna sollicitudin tempus. Etiam efficitur suscipit consequat. Integer ante nisi, placerat id orci ut, eleifend sollicitudin ipsum. Integer posuere, risus ac ultrices porta, nibh quam ultricies quam, eget lobortis magna lorem id erat. Maecenas lorem purus, varius finibus odio in, accumsan imperdiet leo.",
        listOf(
            Photo(
                "https://unsplash.com/fr/photos/edificio-in-cemento-bianco-e-blu-sotto-il-cielo-blu-durante-il-giorno-jfRrtH1hDTo",
                "façade"
            ),
            Photo(
                "https://unsplash.com/fr/photos/divano-componibile-grigio-A4U4dEuN-hw",
                "LivingRoom"
            ),
            Photo(
                "https://unsplash.com/fr/photos/une-salle-de-bain-avec-baignoire-lavabo-et-miroir--4muZDx4-dM",
                "Bathroom"
            )
        ),

        "833 Ocean Ave, Brooklyn, NY 11226, États-Unis",
        latitude = 40.652,
        longitude = -73.961,
        listOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
        PropertyStatus.Available,
        4477894645,
        null,
        Agent("1", "Will", "911", "willagent@brooklyn.com")
    ), Property("2",
    "Apartment",
    400000.0,
    120.0,
    4,
    "A spacious flat in the middle of Brooklyn "+"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed mollis, massa euismod tempor rhoncus, nulla neque luctus sapien, at mollis purus ligula in libero. Sed libero augue, consequat eu mauris a, pulvinar venenatis nunc. Donec faucibus ligula ac mattis luctus. Morbi purus urna, ullamcorper ac volutpat ac, sodales id nulla. Nunc ultrices nisi ex, eget lacinia purus suscipit congue. In quis facilisis nisl, vel pharetra leo. Vivamus mollis massa at ligula consequat lacinia eget a neque. Maecenas volutpat blandit purus luctus egestas. Donec et iaculis libero. Donec quis mi sed magna sollicitudin tempus. Etiam efficitur suscipit consequat. Integer ante nisi, placerat id orci ut, eleifend sollicitudin ipsum. Integer posuere, risus ac ultrices porta, nibh quam ultricies quam, eget lobortis magna lorem id erat. Maecenas lorem purus, varius finibus odio in, accumsan imperdiet leo.",
    listOf(
        Photo(
            "https://unsplash.com/fr/photos/edificio-in-cemento-bianco-e-blu-sotto-il-cielo-blu-durante-il-giorno-jfRrtH1hDTo",
            "façade"
        ),
        Photo(
            "https://unsplash.com/fr/photos/divano-componibile-grigio-A4U4dEuN-hw",
            "LivingRoom"
        ),
        Photo(
            "https://unsplash.com/fr/photos/une-salle-de-bain-avec-baignoire-lavabo-et-miroir--4muZDx4-dM",
            "Bathroom"
        )
    ),

    "900 Ocean Ave, Brooklyn, NY 11226, États-Unis",
    latitude = 42.652,
    longitude = -71.961,
    listOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
    PropertyStatus.Available,
    4477894000,
    null,
    Agent("1", "Will", "911", "willagent@brooklyn.com")
    ))
    HomeContent(
        uiState = PropertySharedViewModel.PropertyUiState.Success(sampleProperties, "", isFiltered = false),
        innerPadding = PaddingValues(all = 8.dp),
        itemIdSelected = "",
        onPropertyItemClick = {}
        )
}