package com.openclassrooms.realestatemanagerv2.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsContent
import com.openclassrooms.realestatemanagerv2.ui.composables.DoubleBackToExitHandler
import com.openclassrooms.realestatemanagerv2.utils.convertToLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.formatToLocalCurrency
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterial3AdaptiveApi
@Composable
fun MapScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    navController: NavController,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (propertyId: String) -> Unit,
    propertiesViewModel: PropertySharedViewModel,
    detailsViewModel: PropertyDetailsViewModel
) {
    val listUiState by propertiesViewModel.uiState.collectAsStateWithLifecycle()
    val successListState = listUiState as? PropertySharedViewModel.PropertyUiState.Success

    val detailsUiState by detailsViewModel.uiState.collectAsStateWithLifecycle()
    val successDetailsState = detailsUiState as? PropertyDetailsViewModel.PropertyDetailsUiState.Success

    // State for video player visibility and URL, managed within this stateful composable
    var isVideoDisplayed by remember { mutableStateOf(false) }
    var currentVideoUrl by remember { mutableStateOf("") }

    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()
    val isListAndDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded &&
                navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

    val topBarTitle = when {
        (navigator.currentDestination?.contentKey == null
                || isListAndDetailVisible)
                && successListState?.isFiltered == true -> stringResource(R.string.filtered_map)
        navigator.currentDestination?.contentKey == null
                || isListAndDetailVisible -> stringResource(R.string.map)
        else -> stringResource(R.string.property_details)
    }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val criterias =
        savedStateHandle?.get<PropertySearchCriteria>("Criterias")

    //Track latest close version of the detail pane
    val closeVersion = successListState?.detailPaneCloseVersion ?: 0
    var lastHandledVersion by rememberSaveable { mutableIntStateOf(0) }

    val activity = LocalContext.current as? Activity

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var isInitialCameraMoveDone by rememberSaveable { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(48.85, 2.35), 5f)
    }

    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted =
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted || isInitialCameraMoveDone) {
            return@LaunchedEffect
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)
                    )
                    isInitialCameraMoveDone = true
                }
            }
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

    LaunchedEffect(criterias) {
        if (criterias != null) {
            propertiesViewModel.searchProperties(criterias)
            savedStateHandle.remove<PropertySearchCriteria>("Criterias")
        }
    }

    val navBarsColor = if (
        listUiState is PropertySharedViewModel.PropertyUiState.Success
        && (listUiState as PropertySharedViewModel.PropertyUiState.Success).isFiltered
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
        onEraseFiltersClick = { propertiesViewModel.resetProperties() },
        showUpButton = navigator.currentDestination?.contentKey != null && !isListAndDetailVisible,
        onUpClick = { scope.launch { navigator.navigateBack() } },
        onAddClick = onNavigateToAdd,
        onModifyClick = { navigator.currentDestination?.contentKey?.let { onNavigateToEdit(it) } },
        showModifyButton = if (navigator.currentDestination?.contentKey != null) {
            true
        } else false,
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
                        is PropertySharedViewModel.PropertyUiState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        is PropertySharedViewModel.PropertyUiState.Error -> {
                            val e = state.exception
                            Text("Erreur : ${e.localizedMessage}")
                        }

                        is PropertySharedViewModel.PropertyUiState.Success -> {
                            MapContent(
                                properties = state.properties,
                                locationPermissionGranted = locationPermissionGranted,
                                cameraPositionState = cameraPositionState,
                                onInfoWindowClick = { propertyId ->
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
                    navigator.currentDestination?.contentKey?.let { propertyId ->
                        detailsViewModel.getPropertyById(propertyId)
                    }
                    if (navigator.currentDestination?.contentKey != null) {
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
fun MapContent(
    properties: List<Property>,
    locationPermissionGranted: Boolean,
    cameraPositionState: CameraPositionState,
    onInfoWindowClick: (propertyId: String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = locationPermissionGranted),
            uiSettings = MapUiSettings(myLocationButtonEnabled = locationPermissionGranted)
        ) {
            properties.forEach { property ->
                if (property.latitude != null && property.longitude != null) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(property.latitude, property.longitude)
                        ),
                        title = property.address,
                        snippet = property.type + " - " + "${
                            property.price.convertToLocalCurrency().toString()
                                .formatToLocalCurrency()
                        }",
                        onInfoWindowClick = { onInfoWindowClick(property.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = -1)
@Composable
fun MapContentPreview() {

    MapContent(
        properties = emptyList(),
        locationPermissionGranted = false,
        cameraPositionState = rememberCameraPositionState(),
        onInfoWindowClick = {})
}