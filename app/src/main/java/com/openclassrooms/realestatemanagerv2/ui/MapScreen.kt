package com.openclassrooms.realestatemanagerv2.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel
import kotlinx.coroutines.launch


@Composable
fun MapScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navController: NavController,
    onNavigateToAdd: () -> Unit,
    viewModel: PropertySharedViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val navBarsColor = if (
        uiState is PropertySharedViewModel.PropertyUiState.Success
        && (uiState as PropertySharedViewModel.PropertyUiState.Success).isFiltered
    ) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    AppTopBar(
        onNavigationClick = { TODO() },
        onAddClick = onNavigateToAdd,
        onModifyClick = { TODO() },
        showModifyButton = false,
        navBarsColor = navBarsColor
    ) { innerPadding ->

        ListDetailPaneTest2(
            innerPadding = innerPadding,
            listViewModel = viewModel,
            navController = rememberNavController()
        )

    }

    /*MapContent(
         innerPadding = innerPadding,
         uiState = uiState,
         onInfoWindowClick = { propertyId ->
             navController
                 .navigate("details_screen" + "/" + propertyId)
         },
         onEraseFiltersClick = {
             viewModel.resetProperties()
         }
     )
 }*/
}

@Composable
fun MapContent(
    uiState: PropertySharedViewModel.PropertyUiState,
    onInfoWindowClick: (propertyId: String) -> Unit,
    onEraseFiltersClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        when (uiState) {
            is PropertySharedViewModel.PropertyUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is PropertySharedViewModel.PropertyUiState.Error -> {
                val e = uiState.exception
                Text("Erreur : ${e.localizedMessage}")
            }

            is PropertySharedViewModel.PropertyUiState.Success -> {
                val properties =
                    uiState.properties

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            properties.firstOrNull()?.latitude ?: 0.0,
                            properties.firstOrNull()?.longitude ?: 0.0
                        ),
                        12f
                    )
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    properties.forEach { property ->
                        if (property.latitude != null && property.longitude != null) {
                            Marker(
                                state = MarkerState(
                                    position = LatLng(property.latitude, property.longitude)
                                ),
                                title = property.address,
                                snippet = property.type + " - " + "${property.price} €",
                                onInfoWindowClick = { onInfoWindowClick(property.id) }
                            )
                        }
                    }
                }
            }
        }

        if (uiState is PropertySharedViewModel.PropertyUiState.Success &&
            uiState.isFiltered
        ) {
            TextButton(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                onClick = onEraseFiltersClick,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.erase_filters))
            }
        }
    }
}

//Test
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListDetailPaneTest2(
    innerPadding: PaddingValues,
    navController: NavHostController,
    onBackClicked: () -> Unit = { navController.popBackStack() },
    listViewModel: PropertySharedViewModel,
    detailsViewModel: PropertyDetailsViewModel = hiltViewModel()
) {

    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()
    val isListAndDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded &&
                navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

    // List values
    val listUiState by listViewModel.uiState.collectAsState()



    // Details values
    // State for video player visibility and URL, managed within this stateful composable
    var isVideoDisplayed by remember { mutableStateOf(false) }
    var currentVideoUrl by remember { mutableStateOf("") }

    // Fetch property details when propertyId changes


    // Collect UI state from ViewModel
    val detailsUiState by detailsViewModel.uiState.collectAsState()


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
                MapContent(
                    uiState = listUiState,
                    onInfoWindowClick = { propertyId ->
                        scope.launch {
                            navigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                propertyId
                            )
                        }
                    },
                    onEraseFiltersClick = {
                        listViewModel.resetProperties()
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane(modifier = Modifier) {
                navigator.currentDestination?.contentKey?.let { propertyId ->
                    detailsViewModel.getPropertyById(propertyId)
                }
                if (navigator.currentDestination?.contentKey != null) {
                    DetailsContent(
                        uiState = detailsUiState,
                        innerPadding = PaddingValues(0.dp),
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
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.select_property_to_details))
                    }
                }
            }
        },
        paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
        paneExpansionDragHandle = {}

    )
}


@Preview(showBackground = true, showSystemUi = true, backgroundColor = -1)
@Composable
fun MapContentPreview() {

    MapContent(
        uiState = PropertySharedViewModel.PropertyUiState.Success(emptyList(), "", isFiltered = false),
        onInfoWindowClick = {},
        onEraseFiltersClick = {})
}