package com.openclassrooms.realestatemanagerv2.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "shared_properties_graph"){

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
            DetailsScreen(navController = navController, propertyId = it.arguments?.getString("propertyId")!!)
            Log.d("DetailsScreen", "Property ID: ${it.arguments?.getString("propertyId")}")
        }
        composable(route = "add_screen") {
            AddScreen(navController)
        }

    }

}