package com.openclassrooms.realestatemanagerv2.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertySearchCriteria
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.ui.composables.AppTopBar
import com.openclassrooms.realestatemanagerv2.ui.composables.PropertyListItem
import com.openclassrooms.realestatemanagerv2.viewmodels.PropertySharedViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: PropertySharedViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    val navBarsColor = if (
        uiState is PropertySharedViewModel.PropertyUiState.Success
        && (uiState as PropertySharedViewModel.PropertyUiState.Success).isFiltered
    ) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val backStackEntry = navController.previousBackStackEntry
    val savedState     = backStackEntry?.savedStateHandle
    val criteria       = remember(savedState) {
        savedState?.get<PropertySearchCriteria>("criterias")
    }

    LaunchedEffect(criteria) {
        criteria?.let {
            viewModel.searchProperties(it)
            savedState?.remove<PropertySearchCriteria>("criterias")
        }
    }

    AppTopBar(
        navController = navController,
        onNavigationClick = { /*TODO*/ },
        onModifyClick = { /*TODO*/ },
        showModifyButton = false,
        navBarsColor = navBarsColor,
        showBottomBar = true
    ) { innerPadding ->

        HomeContent(
            uiState = uiState,
            innerPadding = innerPadding,
            onPropertyItemClick = { propertyId ->
                navController.navigate("details_screen" + "/" + propertyId)
            },
            onResetFiltersClick = {
                viewModel.resetProperties()
            }
        )
    }
}

@Composable
fun HomeContent(
    uiState: PropertySharedViewModel.PropertyUiState,
    innerPadding: PaddingValues, // Padding from AppTopBar
    onPropertyItemClick: (propertyId: String) -> Unit,
    onResetFiltersClick: () -> Unit
) {

    Box(modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()) {
        when (uiState) {
            is PropertySharedViewModel.PropertyUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is PropertySharedViewModel.PropertyUiState.Success ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(items = uiState.properties) { _, item ->
                        PropertyListItem(
                            property = item,
                            onItemClick = { onPropertyItemClick(item.id) }
                        )
                    }
                }

            is PropertySharedViewModel.PropertyUiState.Error -> {
                val e = uiState.exception
                Text(
                    text = stringResource(id = R.string.error_loading_properties,
                        e.localizedMessage ?: "Unknown error"),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
                Log.e("HomeScreenContent", "PropertyUiState.Error: ${e.message}")
            }
        }

        if (uiState is PropertySharedViewModel.PropertyUiState.Success && uiState.isFiltered) {
            TextButton(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                onClick = onResetFiltersClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.erase_filters))
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    //Preview of the HomeScreen in success state and without filters
    val sampleProperties = listOf( Property("1",
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
    ), Property("2",
    "Apartment",
    400000.0,
    120.0,
    4,
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

    "900 Ocean Ave, Brooklyn, NY 11226, États-Unis",
    latitude = 42.652,
    longitude = -71.961,
    listOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
    PropertyStatus.Available,
    4477894000,
    null,
    Agent("1", "Will", "911", "willagent@brooklyn.com")
    ))
    HomeContent(
        uiState = PropertySharedViewModel.PropertyUiState.Success(sampleProperties, false),
        innerPadding = PaddingValues(all = 8.dp),
        onPropertyItemClick = {},
        onResetFiltersClick = {}
        )
}