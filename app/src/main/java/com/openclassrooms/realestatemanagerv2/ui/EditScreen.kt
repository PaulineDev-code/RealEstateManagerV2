package com.openclassrooms.realestatemanagerv2.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.openclassrooms.realestatemanagerv2.viewmodels.AddPropertyViewModel
import com.openclassrooms.realestatemanagerv2.viewmodels.EditPropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    editViewModel: EditPropertyViewModel = hiltViewModel()
) {

    /*AddContent()*/
}