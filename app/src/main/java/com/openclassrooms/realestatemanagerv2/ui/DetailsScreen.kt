package com.openclassrooms.realestatemanagerv2.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.domain.model.Video
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsDescriptionContent
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsInformationsContent
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsMediaContent
import com.openclassrooms.realestatemanagerv2.ui.composables.VideoPlayer
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel


@Composable
fun DetailsScreen(navController: NavController,
                  propertyId: String,
                  viewModel: PropertyDetailsViewModel = hiltViewModel()
) {

    // State for video player visibility and URL, managed within this stateful composable
    var isVideoDisplayed by remember { mutableStateOf(false) }
    var currentVideoUrl by remember { mutableStateOf("") }

    // Fetch property details when propertyId changes
    LaunchedEffect(propertyId) {
        viewModel.getPropertyById(propertyId)
    }

    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    AppTopBar(
        navController = navController,
        onNavigationClick = { /*TODO*/ },
        onSearchClick = { /*TODO*/ },
        onModifyClick = { /*TODO*/ },
        showModifyButton = false,
        content = { innerPadding ->
            DetailsContent(
                uiState = uiState,
                innerPadding = innerPadding, // For content padding from AppTopBar  {
                onVideoClicked = { videoUrl ->
                    currentVideoUrl = videoUrl
                    isVideoDisplayed = true},
                onVideoPlayerClosed = {
                    currentVideoUrl = ""
                    isVideoDisplayed = false
                },
                isVideoDisplayed = isVideoDisplayed,
                currentVideoUrl = currentVideoUrl
            )
        }
    )
}

@Composable
fun DetailsContent(uiState: PropertyDetailsViewModel.PropertyDetailsUiState,
                   innerPadding: PaddingValues, // For content padding from AppTopBar
                   onVideoClicked: (videoUrl: String) -> Unit,
                   onVideoPlayerClosed: () -> Unit,
                   isVideoDisplayed: Boolean,
                   currentVideoUrl: String) {
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
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    DetailsMediaContent(
                        photoList = property.media.filterIsInstance<Photo>(),
                        videoList = property.media.filterIsInstance<Video>(),
                        context = context,
                        onPhotoDeleted = {},
                        onVideoDeleted = {},
                        onVideoClicked = onVideoClicked
                    )
                    DetailsDescriptionContent(description = property.description)
                    DetailsInformationsContent(property)
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
                    context = context,
                    onClose = onVideoPlayerClosed
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
    val sampleProperty = Property("1",
        "Apartment",
        300000.0,
        90.0,
        3,
        "A spacious flat in the middle of Brooklyn "+"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed mollis, massa euismod tempor rhoncus, nulla neque luctus sapien, at mollis purus ligula in libero. Sed libero augue, consequat eu mauris a, pulvinar venenatis nunc. Donec faucibus ligula ac mattis luctus. Morbi purus urna, ullamcorper ac volutpat ac, sodales id nulla. Nunc ultrices nisi ex, eget lacinia purus suscipit congue. In quis facilisis nisl, vel pharetra leo. Vivamus mollis massa at ligula consequat lacinia eget a neque. Maecenas volutpat blandit purus luctus egestas. Donec et iaculis libero. Donec quis mi sed magna sollicitudin tempus. Etiam efficitur suscipit consequat. Integer ante nisi, placerat id orci ut, eleifend sollicitudin ipsum. Integer posuere, risus ac ultrices porta, nibh quam ultricies quam, eget lobortis magna lorem id erat. Maecenas lorem purus, varius finibus odio in, accumsan imperdiet leo.",
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
        onVideoClicked = {},
        onVideoPlayerClosed = {},
        isVideoDisplayed = false,
        currentVideoUrl = "")
}