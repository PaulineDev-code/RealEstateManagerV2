package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus

@Composable
fun HomeContent(
    properties: List<Property>,
    isFiltered: Boolean,
    innerPadding: PaddingValues,
    itemIdSelected: String,
    onPropertyItemClick: (propertyId: String) -> Unit
) {

    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {


        if (properties.isEmpty() && isFiltered == true) {
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                text = stringResource(id = R.string.no_property_found),
                fontStyle = FontStyle.Italic
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(items = properties) { _, item ->
                    PropertyListItem(
                        property = item,
                        isItemSelected = itemIdSelected == item.id,
                        onItemClick = { onPropertyItemClick(item.id) }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun HomeContentPreview() {
    //Preview of the HomeContent without filters
    val sampleProperties = listOf(
        Property(
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
        ), Property(
            "2",
            "Apartment",
            400000.0,
            120.0,
            4,
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

            "900 Ocean Ave, Brooklyn, NY 11226, États-Unis",
            latitude = 42.652,
            longitude = -71.961,
            listOf(PointOfInterest.PHARMACY, PointOfInterest.RESTAURANT),
            PropertyStatus.Available,
            4477894000,
            null,
            Agent("1", "Will", "911", "willagent@brooklyn.com")
        )
    )
    HomeContent(
        properties = sampleProperties,
        isFiltered = false,
        innerPadding = PaddingValues(all = 8.dp),
        itemIdSelected = "",
        onPropertyItemClick = {}
    )
}