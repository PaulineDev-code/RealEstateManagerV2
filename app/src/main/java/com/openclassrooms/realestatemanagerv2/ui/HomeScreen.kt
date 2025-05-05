package com.openclassrooms.realestatemanagerv2.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.PropertyList

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    //TODO: Create a new composable for scaffold+topbar
    AppTopBar(
        navController = navController,
        onNavigationClick = { /*TODO*/ },
        onModifyClick = { /*TODO*/ },
        showModifyButton = false
    ) {
        PropertyList(navController = navController, modifier = Modifier.padding(it))
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}