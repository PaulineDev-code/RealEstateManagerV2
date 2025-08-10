package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.ui.AppNavigationSuiteScaffold
import com.openclassrooms.realestatemanagerv2.ui.BottomNavItem
import com.openclassrooms.realestatemanagerv2.ui.DetailsContent
import com.openclassrooms.realestatemanagerv2.ui.HomeContent
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListDetailPaneTest(
    innerPadding: PaddingValues,
    navController: NavHostController,
    onBackClicked: () -> Unit = { navController.popBackStack() },
    listViewModel: PropertySharedViewModel = hiltViewModel(),
    detailsViewModel: PropertyDetailsViewModel = hiltViewModel()
) {

    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val scope = rememberCoroutineScope()
    val isListAndDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded &&
                navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

    // List values
    val listUiState by listViewModel.uiState.collectAsState()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val savedState = navBackStackEntry?.savedStateHandle
    val criteria = remember(savedState) {
        savedState?.get<PropertySearchCriteria>("criterias")
    }
    // Details values
    // State for video player visibility and URL, managed within this stateful composable
    var isVideoDisplayed by remember { mutableStateOf(false) }
    var currentVideoUrl by remember { mutableStateOf("") }

    // Fetch property details when propertyId changes


    // Collect UI state from ViewModel
    val detailsUiState by detailsViewModel.uiState.collectAsState()

    LaunchedEffect(criteria) {
        criteria?.let {
            listViewModel.searchProperties(it)
            savedState?.remove<PropertySearchCriteria>("criterias")
        }
    }

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
                    onPropertyItemClick = { propertyId ->
                        scope.launch {
                            navigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                propertyId
                            )
                        }
                    },
                    onResetFiltersClick = {
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
