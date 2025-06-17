package com.openclassrooms.realestatemanagerv2.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MapScreen(
    navController: NavHostController,
    viewModel: PropertySharedViewModel
) {

    val uiState by viewModel.uiState.collectAsState()

    AppTopBar(
        navController = navController,
        onNavigationClick = { /*TODO*/ },
        onModifyClick = { /*TODO*/ },
        showModifyButton = false,
        showBottomBar = true
    ) {

        when (uiState) {
            is PropertySharedViewModel.PropertyUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is PropertySharedViewModel.PropertyUiState.Error -> {
                val e = (uiState as PropertySharedViewModel.PropertyUiState.Error).exception
                Text("Erreur : ${e.localizedMessage}")
            }

            is PropertySharedViewModel.PropertyUiState.Success -> {
                val properties =
                    (uiState as PropertySharedViewModel.PropertyUiState.Success).properties

                // 1. S'assurer que chaque Property contient latitude/longitude
                //    (tu dois avoir ces champs dans ton model ou les géocoder au préalable)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            properties.firstOrNull()?.latitude ?: 0.0,
                            properties.firstOrNull()?.longitude ?: 0.0
                        ),
                        12f
                    )
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    properties.forEach { property ->
                        if (property.latitude != null && property.longitude != null) {
                            Marker(
                                state = MarkerState(
                                    position = LatLng(property.latitude, property.longitude)
                                ),
                                title = property.address,
                                snippet = "${property.price} €"
                            )
                        }
                    }
                }
            }
        }
    }
}