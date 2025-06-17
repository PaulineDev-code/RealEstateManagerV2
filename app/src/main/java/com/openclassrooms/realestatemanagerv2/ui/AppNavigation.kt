package com.openclassrooms.realestatemanagerv2.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home_screen"){
        composable(route = "home_screen") { backStackEntry ->
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(backStackEntry)
            HomeScreen(navController = navController, sharedViewModel)
        }
        composable(route = "search_screen") {
            SearchScreen(navController)
        }
        composable(route = "map_screen") { backStackEntry ->
            val sharedViewModel = hiltViewModel<PropertySharedViewModel>(backStackEntry)
            MapScreen(navController = navController, sharedViewModel)
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