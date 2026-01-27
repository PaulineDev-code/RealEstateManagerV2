package com.openclassrooms.realestatemanagerv2.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.BuildConfig
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Video
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel

@Composable
fun DetailsContent(
    uiState: PropertyDetailsViewModel.PropertyDetailsUiState,
    innerPadding: PaddingValues, // For content padding from AppTopBar
    onPhotoClicked: (photoIndex: Int) -> Unit,
    onPhotoViewerClosed: () -> Unit,
    isPhotoViewerDisplayed: Boolean,
    selectedPhotoIndex: Int,
    onPhotoIndexChanged: (Int) -> Unit,
    onVideoClicked: (videoUrl: String) -> Unit,
    onVideoPlayerClosed: () -> Unit,
    isVideoDisplayed: Boolean,
    currentVideoUrl: String
) {
    val context = LocalContext.current

    when (uiState) {
        is PropertyDetailsViewModel.PropertyDetailsUiState.Loading -> {
            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is PropertyDetailsViewModel.PropertyDetailsUiState.Success -> {
            val property = uiState.property
            if (property != null) {
                // Display property details
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    DetailsMediaContent(
                        photoList = property.media.filterIsInstance<Photo>(),
                        videoList = property.media.filterIsInstance<Video>(),
                        context = context,
                        onPhotoDeleted = {},
                        onPhotoClicked = onPhotoClicked,
                        onVideoDeleted = {},
                        onVideoClicked = onVideoClicked
                    )
                    DetailsDescriptionContent(description = property.description)
                    DetailsInformationsContent(property)
                    StaticMapView(
                        latitude = property.latitude,
                        longitude = property.longitude,
                        apiKey = BuildConfig.MAPS_API_KEY,
                    )
                }
            } else {
                // Display Text saying there is no property available to display
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No property available to display")
                }
            }

            if (isVideoDisplayed && currentVideoUrl.isNotBlank()) {
                VideoPlayer(
                    videoUri = currentVideoUrl,
                    context = context.applicationContext,
                    onClose = onVideoPlayerClosed
                )
            }
            if (isPhotoViewerDisplayed && property != null) {
                PhotoViewer(
                    photos = property.media.filterIsInstance<Photo>(),
                    selectedPhotoIndex = selectedPhotoIndex,
                    onPhotoIndexChanged = onPhotoIndexChanged,
                    onClose = onPhotoViewerClosed
                )
            }
        }

        is PropertyDetailsViewModel.PropertyDetailsUiState.Error -> {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Display error message for the user
                Text(text = "Error loading property details.")
                // Log the error for debugging
                Log.e("DetailsScreenContent", "PropertyDetailsUiState.Error occurred")
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun DetailsScreenPreview() {
    //Preview of the DetailsScreen in success state:
    val sampleProperty = Property(
        "1",
        "Apartment",
        300000.0,
        90.0,
        3,
        "A spacious flat in the middle of Brooklyn " + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed mollis, massa euismod tempor rhoncus, nulla neque luctus sapien, at mollis purus ligula in libero. Sed libero augue, consequat eu mauris a, pulvinar venenatis nunc. Donec faucibus ligula ac mattis luctus. Morbi purus urna, ullamcorper ac volutpat ac, sodales id nulla. Nunc ultrices nisi ex, eget lacinia purus suscipit congue. In quis facilisis nisl, vel pharetra leo. Vivamus mollis massa at ligula consequat lacinia eget a neque. Maecenas volutpat blandit purus luctus egestas. Donec et iaculis libero. Donec quis mi sed magna sollicitudin tempus. Etiam efficitur suscipit consequat. Integer ante nisi, placerat id orci ut, eleifend sollicitudin ipsum. Integer posuere, risus ac ultrices porta, nibh quam ultricies quam, eget lobortis magna lorem id erat. Maecenas lorem purus, varius finibus odio in, accumsan imperdiet leo.",
        listOf(
            Photo(
                "https://unsplash.com/fr/photos/edificio-in-cemento-bianco-e-blu-sotto-il-cielo-blu-durante-il-giorno-jfRrtH1hDTo",
                "façade"
            ),
            Photo(
                "https://unsplash.com/fr/photos/divano-componibile-grigio-A4U4dEuN-hw",
                "LivingRoom"
            ),
            Photo(
                "https://unsplash.com/fr/photos/une-salle-de-bain-avec-baignoire-lavabo-et-miroir--4muZDx4-dM",
                "Bathroom"
            )
        ),

        "833 Ocean Ave, Brooklyn, NY 11226, États-Unis",
        latitude = 40.652,
        longitude = -73.961,
        listOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
        PropertyStatus.Available,
        4477894645,
        null,
        Agent("1", "Will", "911", "willagent@brooklyn.com")
    )

    DetailsContent(
        uiState = PropertyDetailsViewModel.PropertyDetailsUiState.Success(sampleProperty),
        innerPadding = PaddingValues(0.dp),
        onPhotoClicked = {},
        onPhotoViewerClosed = {},
        isPhotoViewerDisplayed = false,
        selectedPhotoIndex = 0,
        onPhotoIndexChanged = { },
        onVideoClicked = {},
        onVideoPlayerClosed = {},
        isVideoDisplayed = false,
        currentVideoUrl = ""
    )
}