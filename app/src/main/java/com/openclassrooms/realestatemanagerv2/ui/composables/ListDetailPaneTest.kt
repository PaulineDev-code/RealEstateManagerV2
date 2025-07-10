package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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
    navController: NavHostController,
    onBackClicked: () -> Unit = { navController.popBackStack() },
    listViewModel: PropertySharedViewModel = hiltViewModel(),
    detailsViewModel: PropertyDetailsViewModel = hiltViewModel()
) {

    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val scope = rememberCoroutineScope()
    val isListAndDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded &&
                navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

    BackHandler(enabled = navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }

    // List values
    val listUiState by listViewModel.uiState.collectAsState()

    val navBarsColor = if (
        listUiState is PropertySharedViewModel.PropertyUiState.Success
        && (listUiState as PropertySharedViewModel.PropertyUiState.Success).isFiltered
    ) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val savedState = navBackStackEntry?.savedStateHandle
    val criteria = remember(savedState) {
        savedState?.get<PropertySearchCriteria>("criterias")
    }
    val selectedPropertyId = remember { mutableStateOf<String?>(null) }
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


    AppTopBar(
        navController = navController,
        onNavigationClick = onBackClicked,
        onAddClick = {},
        onModifyClick = {},
        showModifyButton = false,
        navBarsColor = navBarsColor,
        showBottomBar = false
    ) { innerPadding ->

        ListDetailPaneScaffold(
            modifier = Modifier.padding(innerPadding),
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                HomeContent(
                    uiState = listUiState,
                    innerPadding = PaddingValues(0.dp),
                    onPropertyItemClick = { propertyId ->
                        selectedPropertyId.value = propertyId
                        detailsViewModel.getPropertyById(selectedPropertyId.value!!)
                    },
                    onResetFiltersClick = {
                        listViewModel.resetProperties()
                    }
                )
            },
            detailPane = {

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
            },
            paneExpansionState = rememberPaneExpansionState(navigator.scaffoldValue),
            paneExpansionDragHandle = {}

        )

    }
}