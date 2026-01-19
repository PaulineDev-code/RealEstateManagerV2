package com.openclassrooms.realestatemanagerv2.ui

import android.app.Activity
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.ui.composables.DoubleBackToExitHandler
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
    //TODO: Compose back pressed handler plutôt que primary destinations

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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?")

    val isPrimaryDestination = currentRoute in listOf(
        BottomNavItem.List.route,   // "home_screen"
        BottomNavItem.Map.route,    // "map_screen"
        BottomNavItem.Search.route  // "search_screen"
    )

    val activity = LocalContext.current as? Activity

    DoubleBackToExitHandler(
        enabled = isPrimaryDestination,
        message = stringResource(R.string.press_again_to_exit),
        exit = { activity?.finish() }
    )

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
                backStackEntry.arguments?.getString(BottomNavItem.List.ARG_NEW_ID)
            val uiState by sharedViewModel.uiState.collectAsState()
            LaunchedEffect(newId, uiState) {
                if (newId != null && uiState is PropertySharedViewModel.PropertyUiState.Success) {
                    sharedViewModel.updateAddedProperty(newId)
                    backStackEntry.savedStateHandle.remove<String>(BottomNavItem.List.ARG_NEW_ID)
                }
            }

            HomeScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                listViewModel = sharedViewModel,
                detailsViewModel = detailsViewModel,
                onBackClicked = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate("add_screen") },
                onNavigateToEdit = { propertyId ->
                    navController.navigate("edit_estate/$propertyId")
                }
            )
        }
        composable(BottomNavItem.Map.route) {
            val parentEntry = navController.getBackStackEntry(BottomNavItem.List.fullRoute)
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(parentEntry)
            val detailsViewModel = hiltViewModel<PropertyDetailsViewModel>()
            MapScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController,
                onNavigateToAdd = { navController.navigate("add_screen") },
                propertiesViewModel = sharedViewModel,
                detailsViewModel = detailsViewModel
            )
        }
        composable(BottomNavItem.Search.route) {
            SearchScreen(
                windowAdaptiveInfo = windowAdaptiveInfo,
                navController = navController, // If SearchScreen needs to navigate
                onNavigateToAdd = { navController.navigate("add_screen") }
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