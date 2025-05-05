package com.openclassrooms.realestatemanagerv2.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.Video
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsDescriptionContent
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsInformationsContent
import com.openclassrooms.realestatemanagerv2.ui.composables.DetailsMediaContent
import com.openclassrooms.realestatemanagerv2.ui.composables.VideoPlayer
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertyDetailsViewModel


@Composable
fun DetailsScreen(navController: NavController, propertyId: String,
                  viewModel: PropertyDetailsViewModel = hiltViewModel()
) {

    AppTopBar(
        navController = navController,
        onNavigationClick = { /*TODO*/ },
        onSearchClick = { /*TODO*/ },
        onModifyClick = { /*TODO*/ },
        showModifyButton = false,
        content = {
            LaunchedEffect(propertyId) {
                viewModel.getPropertyById(propertyId)
            }

            Log.d("PropertyDetails", "Property ID: $propertyId")


            val uiState by viewModel.uiState.collectAsState()

            when (uiState) {
                is PropertyDetailsViewModel.PropertyDetailsUiState.Success -> {
                    var isVideoDisplayed = false
                    val context = LocalContext.current
                    var videoUrl: String = ""
                    val property =
                        (uiState as PropertyDetailsViewModel.PropertyDetailsUiState.Success).property
                    if (property != null) {
                        // Display property details
                        Column (modifier = Modifier
                            .padding(it)
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())){
                            DetailsMediaContent(photoList = property.media.filterIsInstance<Photo>(),
                                videoList = property.media.filterIsInstance<Video>(),
                                context = context,
                                onPhotoDeleted = {},
                                onVideoDeleted = {},
                                onVideoClicked = {video ->
                                    videoUrl = video
                                    isVideoDisplayed = true
                                }
                                )
                            DetailsDescriptionContent(description = property.description)
                            DetailsInformationsContent(property)
                        }
                    } else {
                        // Affiche un message ou une indication qu'aucune propriété n'est disponible
                        Text(text = "No property available to display")
                    }
                    if(isVideoDisplayed && videoUrl.isNotBlank()) {

                        VideoPlayer(videoUri = videoUrl, context = context,
                            onClose = { isVideoDisplayed = false } )
                    }
                }

                is PropertyDetailsViewModel.PropertyDetailsUiState.Error -> Log.e(
                    "UI ERROR",
                    "PropertyDetailsUiState.Error"
                )
            }
        }
    )
}


@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun DetailsScreenPreview() {
    DetailsScreen(navController = rememberNavController(), propertyId = "1")
}










/*@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PropertyDetailsPreview() {
    PropertyDetails(
        property = Property(
            "1",
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
            null,
            "833 Ocean Ave, Brooklyn, NY 11226, États-Unis",
            listOf("Pharmacy", "Restaurant"),
            PropertyStatus.Available,
            "20/11/2023",
            null,
            Agent("1", "Will", "911", "willagent@brooklyn.com")
        )
    )
}*/

