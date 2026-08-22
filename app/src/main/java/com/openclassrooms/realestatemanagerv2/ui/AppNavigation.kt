package com.openclassrooms.realestatemanagerv2.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.viewmodels.EditPropertyViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel
import com.openclassrooms.realestatemanagerv2.R

private const val EDITED_PROPERTY_ID = "editedPropertyId"
private const val ADDED_PROPERTY_ID = "addedPropertyId"
// Define your primary navigation destinations
private val primaryDestinations = listOf(
    TopLevelDestination(
        route = Home,
        labelRes = R.string.list,
        icon = Icons.AutoMirrored.Filled.List
    ),
    TopLevelDestination(
        route = Search,
        labelRes = R.string.search,
        icon = Icons.Filled.Search
    ),
    TopLevelDestination(
        route = Map,
        labelRes = R.string.map,
        icon = Icons.Filled.LocationOn
    )
)

private fun NavHostController.navigateToTopLevel(
    route: TopLevelRoute
) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavigation(windowAdaptiveInfo: WindowAdaptiveInfo) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // This determines if we should show the full NavSuite (BottomNav, Rail, Drawer)
    // or if we are in a different flow (e.g., AddScreen, EditScreen, DetailsScreen on compact)
    val showNavSuite =
    currentDestination == null || // Show on start
        primaryDestinations.any {
            currentDestination.hasRoute(it.route::class)
        }

    // Determine the type of navigation suite based on window size
    val navigationSuiteType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)

    NavigationSuiteScaffoldLayout(
        navigationSuite = {
            if (showNavSuite) {
                if (navigationSuiteType == NavigationSuiteType.NavigationRail) {
                    NavigationRail {
                        Spacer(Modifier.weight(1f))
                        primaryDestinations.forEach { screen ->
                            NavigationRailItem(
                                selected = currentDestination?.hasRoute(screen.route::class) == true,
                                onClick = {
                                    navController.navigateToTopLevel(screen.route)
                                },
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = stringResource(screen.labelRes)
                                    )
                                },
                                label = { Text(stringResource(screen.labelRes)) }
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                } else {
                    NavigationSuite {
                        primaryDestinations.forEach { screen ->
                            item(
                                selected = currentDestination?.hasRoute(screen.route::class) == true,
                                onClick = {
                                    navController.navigateToTopLevel(screen.route)
                                },
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = stringResource(screen.labelRes)
                                    )
                                },
                                label = { Text(stringResource(screen.labelRes)) }
                            )
                        }
                    }
                }
            }
        }
    ) {
        // Main content area for the NavigationSuiteScaffold
        AppNavHost(
            navController = navController,
            windowAdaptiveInfo = windowAdaptiveInfo,
            modifier = Modifier // This will get padding from NavigationSuiteScaffold
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {

        composable<Home> { backStackEntry ->
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(backStackEntry)
            val detailsViewModel = hiltViewModel<PropertyDetailsViewModel>()
            val addedPropertyId by backStackEntry.savedStateHandle
                .getStateFlow<String?>(
                    ADDED_PROPERTY_ID,
                    null
                )
                .collectAsStateWithLifecycle()
            val editedPropertyId by backStackEntry.savedStateHandle
                .getStateFlow<String?>(
                    EDITED_PROPERTY_ID,
                    null
                )
                .collectAsStateWithLifecycle()

            LaunchedEffect(addedPropertyId) {
                addedPropertyId?.let { propertyId ->
                    sharedViewModel.updateAddedProperty(propertyId)

                    backStackEntry.savedStateHandle[ADDED_PROPERTY_ID] = null
                }
            }

            LaunchedEffect(editedPropertyId) {
                editedPropertyId?.let { propertyId ->
                    sharedViewModel.updateAddedProperty(propertyId)

                    backStackEntry.savedStateHandle[EDITED_PROPERTY_ID] = null
                }
            }

            HomeScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                listViewModel = sharedViewModel,
                detailsViewModel = detailsViewModel,
                onNavigateToAdd = { navController.navigate(AddProperty) },
                onNavigateToEdit = { propertyId ->
                    navController.navigate(EditProperty(propertyId))
                }
            )
        }
        composable<Map> { backStackEntry ->

            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Home)
            }
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(homeEntry)
            val detailsViewModel = hiltViewModel<PropertyDetailsViewModel>()

            MapScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                onNavigateToAdd = { navController.navigate(AddProperty) },
                onNavigateToEdit = { propertyId ->
                    navController.navigate(EditProperty(propertyId))
                },
                propertiesViewModel = sharedViewModel,
                detailsViewModel = detailsViewModel
            )
        }
        composable<Search> {
            SearchScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                onNavigateToAdd = { navController.navigate(AddProperty) }
            )
        }
        composable<AddProperty> {
            AddScreen(
                navController = navController,
                windowAdaptiveInfo = windowAdaptiveInfo,
                onUpClicked = { navController.popBackStack() },
                onAddedSuccess = { propertyId ->
                    val homeEntry = navController.getBackStackEntry(Home)

                    homeEntry.savedStateHandle[ADDED_PROPERTY_ID] = propertyId

                    navController.popBackStack()
                    navController.navigateToTopLevel(Home)
                }
            )
        }
        composable<EditProperty> { backStackEntry ->

            val editViewModel = hiltViewModel<EditPropertyViewModel>(backStackEntry)

            EditScreen(
                navController = navController,
                windowAdaptiveInfo = windowAdaptiveInfo,
                onUpClicked = { navController.popBackStack() },
                onEditSuccess = { propertyId ->
                    val homeEntry = navController.getBackStackEntry(Home)

                    homeEntry.savedStateHandle[EDITED_PROPERTY_ID] = propertyId

                    navController.popBackStack()
                    navController.navigateToTopLevel(Home)
                                },
                editViewModel = editViewModel
            )
        }
    }
}