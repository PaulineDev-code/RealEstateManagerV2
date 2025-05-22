package com.openclassrooms.realestatemanagerv2.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.openclassrooms.realestatemanagerv2.R

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object List : BottomNavItem("home_screen", "List", Icons.AutoMirrored.Filled.List)
    object Search : BottomNavItem("search_screen", "Search", Icons.Filled.Search)
    object Map : BottomNavItem("map_screen", "Map", Icons.Filled.LocationOn)
}