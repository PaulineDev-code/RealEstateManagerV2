package com.openclassrooms.realestatemanagerv2.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object List : BottomNavItem("home_screen", "Liste", Icons.AutoMirrored.Filled.List)
    object Search : BottomNavItem("search_screen", "Recherche", Icons.Filled.Search)
    object Map : BottomNavItem("map_screen", "Carte", Icons.Filled.LocationOn)
}