package com.openclassrooms.realestatemanagerv2.ui

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel




@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavigation(windowAdaptiveInfo: WindowAdaptiveInfo) {

    // Define your primary navigation destinations
    val primaryDestinations = listOf(BottomNavItem.List, BottomNavItem.Search, BottomNavItem.Map)


    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // This determines if we should show the full NavSuite (BottomNav, Rail, Drawer)
    // or if we are in a different flow (e.g., AddScreen, EditScreen, DetailsScreen on compact)
    val showNavSuite =
        primaryDestinations.any { it.route == currentRoute } || currentRoute == null // Show on start

    // Determine the type of navigation suite based on window size
    val navigationSuiteType =
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)

    if (showNavSuite) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
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
            },
            // layoutType = navigationSuiteType, // This is applied by default
            // You can customize colors and other properties of the navigation suite here
        ) {
            // Main content area for the NavigationSuiteScaffold
            AppNavHost(
                navController = navController,
                windowAdaptiveInfo = windowAdaptiveInfo,
                modifier = Modifier // This will get padding from NavigationSuiteScaffold
            )
        }
    } else {
        // If not showing NavSuite, we are likely in a deeper screen like Add/Edit/Details (on compact)
        // These screens might have their own TopAppBar and manage their own layout.
        AppNavHost(
            navController = navController,
            windowAdaptiveInfo = windowAdaptiveInfo,
            modifier = Modifier
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home_screen",
        modifier = modifier
    ) {

        composable("home_screen") { backStackEntry ->

            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(backStackEntry)
            HomeScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                viewModel = sharedViewModel,
                onBackClicked = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate("add_screen") },
                onNavigateToEdit = { propertyId ->
                    navController.navigate("edit_estate/$propertyId")
                }
            )
        }
        composable("map_screen") {

            val parentEntry = navController.getBackStackEntry("home_screen")
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(parentEntry)
            MapScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                viewModel = sharedViewModel,
                onNavigateToAdd = { navController.navigate("add_estate") }
            )
        }
        composable("search_screen") {
            SearchScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController, // If SearchScreen needs to navigate
                onNavigateToAdd = { navController.navigate("add_estate") }
            )
        }
        composable("details_screen/{propertyId}") { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")!!
            // DetailsScreen might not be part of the NavSuite if it takes the full screen
            DetailsScreen(
                propertyId = propertyId,
                windowAdaptiveInfo = windowAdaptiveInfo,
                onNavigateToAdd = { navController.navigate("add_screen") },
                onNavigateToEdit = { navController.navigate("edit_estate/$propertyId") }
            )
        }
        composable("add_screen") {
            AddScreen(
                navController = navController,
                windowAdaptiveInfo = windowAdaptiveInfo,
                onBackClicked = { navController.popBackStack() }
            )
        }
        /*composable("edit_estate/{propertyId}") { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
            EditScreen(
                propertyId = propertyId,
                navController = navController,
                windowAdaptiveInfo = windowAdaptiveInfo,
                onBack = { navController.popBackStack() }
            )
        }*/
    }
}


/*NavHost(navController = navController, startDestination = "shared_properties_graph"){

    navigation(
        startDestination = "home_screen",
        route = "shared_properties_graph"){

        composable(route = "home_screen") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("shared_properties_graph")
            }
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(parentEntry)
            HomeScreen(navController = navController, sharedViewModel)
        }

        composable(route = "map_screen") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("shared_properties_graph")
            }
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(parentEntry)
            MapScreen(navController = navController, sharedViewModel)
        }
    }
    composable(route = "search_screen") {
        SearchScreen(navController)
    }
    composable(route = "details_screen/{propertyId}",
    arguments = listOf(
        navArgument("propertyId") {
            type = NavType.StringType
        }
    )) {
        DetailsScreen(
            navController = navController,
            propertyId = it.arguments?.getString("propertyId")!!
        )
        Log.d("DetailsScreen", "Property ID: ${it.arguments?.getString("propertyId")}")
    }
    composable(route = "add_screen") {
        AddScreen(navController)
    }

}

}*/