package com.openclassrooms.realestatemanagerv2.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home_screen"){
        composable(route = "home_screen") {
            HomeScreen(navController = navController)
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
        composable(route = "search_screen") {
            SearchScreen(navController)
        }
    }

}