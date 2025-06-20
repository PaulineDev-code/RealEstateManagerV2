package com.openclassrooms.realestatemanagerv2.ui.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.openclassrooms.realestatemanagerv2.R

@Composable
fun StaticMapView(
    latitude: Double?,
    longitude: Double?,
    apiKey: String,
    modifier: Modifier = Modifier,
    zoom: Int = 18,
    width: Int = 600, // Image width in pixels
    height: Int = 600, // Image height in pixels
    mapType: String = "roadmap",
    markerLabel: String = "",
    markerColor: String = "red",
    imageModifier: Modifier = Modifier,
    mapHeight: Dp = 200.dp
) {
    if (latitude == null || longitude == null) {
        // Handle cases where lat/lng might be missing
        Text(
            text = stringResource(R.string.location_not_available),
            modifier = modifier.height(mapHeight)
        )
        return
    }

    if (apiKey.isBlank() || apiKey == "YOUR_API_KEY_PLACEHOLDER" || apiKey.startsWith("\${")) {
        Text(
            text = stringResource(R.string.maps_api_key_missing_static_map),
            color = MaterialTheme.colorScheme.error,
            modifier = modifier.height(mapHeight)
        )
        return
    }

    val staticMapUrl = remember(latitude, longitude, zoom, width, height, mapType, markerColor, markerLabel, apiKey) {
        // URL for Static Map API request with dynamic parameters
        "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=$latitude,$longitude" +
                "&zoom=$zoom" +
                "&size=${width}x$height" +
                "&maptype=$mapType" +
                "&markers=color:$markerColor%7Clabel:$markerLabel%7C$latitude,$longitude" +
                "&key=$apiKey"
    }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(staticMapUrl)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(R.string.property_location_map),
        modifier = modifier
            .fillMaxWidth()
            .height(mapHeight)
            .then(imageModifier), // Allow further customization of the Image
        contentScale = ContentScale.Crop, // Or ContentScale.Fit for a smaller image with full coverage
        loading = {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        },
        error = {
            Text(
                text = stringResource(R.string.error_loading_map_image),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
            Log.e("StaticMapView", "Error loading map image: ${it.result.throwable}")
        }
    )
}
