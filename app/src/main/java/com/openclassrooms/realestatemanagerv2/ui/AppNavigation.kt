package com.openclassrooms.realestatemanagerv2.ui

import android.net.Uri
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.openclassrooms.realestatemanagerv2.ui.states.PropertyUiState
import com.openclassrooms.realestatemanagerv2.viewmodels.EditPropertyViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel




@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavigation(windowAdaptiveInfo: WindowAdaptiveInfo) {

    // Define your primary navigation destinations
    val primaryDestinations = listOf(BottomNavItem.List, BottomNavItem.Search, BottomNavItem.Map)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?")

    // This determines if we should show the full NavSuite (BottomNav, Rail, Drawer)
    // or if we are in a different flow (e.g., AddScreen, EditScreen, DetailsScreen on compact)
    val showNavSuite =
        primaryDestinations.any { it.route == currentRoute } || currentRoute == null // Show on start

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
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = { Text(screen.title) }
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                } else {
                    NavigationSuite {
                        primaryDestinations.forEach { screen ->
                            item(
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = { Text(screen.title) }
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
        startDestination = BottomNavItem.List.fullRoute,
        modifier = modifier
    ) {

        composable(
            route = BottomNavItem.List.fullRoute,
            arguments = listOf(
                navArgument(BottomNavItem.List.ARG_NEW_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(backStackEntry)
            val detailsViewModel = hiltViewModel<PropertyDetailsViewModel>()
            val newId: String? =
                backStackEntry.arguments?.getString(BottomNavItem.List.ARG_NEW_ID)?.let(Uri::decode)
            val uiState by sharedViewModel.uiState.collectAsState()
            LaunchedEffect(newId, uiState) {
                if (newId != null && uiState is PropertyUiState.Success) {
                    sharedViewModel.updateAddedProperty(newId)
                    backStackEntry.arguments?.remove(BottomNavItem.List.ARG_NEW_ID)
                }
            }

            HomeScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                listViewModel = sharedViewModel,
                detailsViewModel = detailsViewModel,
                onNavigateToAdd = { navController.navigate("add_screen") },
                onNavigateToEdit = { propertyId ->
                    navController.navigate("edit_estate/$propertyId")
                }
            )
        }
        composable(BottomNavItem.Map.route) { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(BottomNavItem.List.fullRoute)
            }
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(parentEntry)
            val detailsViewModel = hiltViewModel<PropertyDetailsViewModel>()
            MapScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                onNavigateToAdd = { navController.navigate("add_screen") },
                onNavigateToEdit = { propertyId ->
                    navController.navigate("edit_estate/$propertyId")
                },
                propertiesViewModel = sharedViewModel,
                detailsViewModel = detailsViewModel
            )
        }
        composable(BottomNavItem.Search.route) {
            SearchScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                onNavigateToAdd = { navController.navigate("add_screen") }
            )
        }
        composable("add_screen") {
            AddScreen(
                navController = navController,
                windowAdaptiveInfo = windowAdaptiveInfo,
                onUpClicked = { navController.popBackStack() }
            )
        }
        composable("edit_estate/{propertyId}") { backStackEntry ->

            val editViewModel = hiltViewModel<EditPropertyViewModel>(backStackEntry)

            EditScreen(
                navController = navController,
                windowAdaptiveInfo = windowAdaptiveInfo,
                onUpClicked = { navController.popBackStack() },
                editViewModel = editViewModel
            )
        }
    }
}