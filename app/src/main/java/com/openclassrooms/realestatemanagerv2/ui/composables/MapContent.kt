package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.utils.convertToLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.formatToLocalCurrency

@Composable
fun MapContent(
    properties: List<Property>,
    locationPermissionGranted: Boolean,
    cameraPositionState: CameraPositionState,
    onInfoWindowClick: (propertyId: String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = locationPermissionGranted),
            uiSettings = MapUiSettings(myLocationButtonEnabled = locationPermissionGranted)
        ) {
            properties.forEach { property ->
                if (property.latitude != null && property.longitude != null) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(property.latitude, property.longitude)
                        ),
                        title = property.address,
                        snippet = property.type + " - " + "${
                            property.price.convertToLocalCurrency().toString()
                                .formatToLocalCurrency()
                        }",
                        onInfoWindowClick = { onInfoWindowClick(property.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = -1)
@Composable
fun MapContentPreview() {

    MapContent(
        properties = emptyList(),
        locationPermissionGranted = false,
        cameraPositionState = rememberCameraPositionState(),
        onInfoWindowClick = {})
}