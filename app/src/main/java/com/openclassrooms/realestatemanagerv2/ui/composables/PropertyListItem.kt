package com.openclassrooms.realestatemanagerv2.ui.composables

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.PointOfInterest
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import com.openclassrooms.realestatemanagerv2.utils.convertToLocalCurrency
import com.openclassrooms.realestatemanagerv2.utils.formatToLocalCurrency


@Composable
fun PropertyListItem(property: Property, isItemSelected: Boolean, onItemClick: (String) -> Unit) {

    ElevatedCard(
        modifier = Modifier
            .selectable(
                selected = isItemSelected,
                onClick = { onItemClick(property.id) }
            )
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        colors = if (isItemSelected) {
            CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onBackground,
        )} else {
            CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onBackground,
        )},
        elevation = if (isItemSelected) {
            CardDefaults.elevatedCardElevation(10.dp)
        } else {
            CardDefaults.elevatedCardElevation(2.dp)
}
    ) {

        Row(
            modifier = Modifier
                .height(128.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(property.media.filterIsInstance<Photo>().firstOrNull()?.mediaUrl ?:
                    R.drawable.ic_refresh)
                    .placeholder(R.drawable.ic_refresh)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop,
                onError = { error ->
                    Log.e("CoilError", error.result.throwable.localizedMessage ?: "Unknown error")
                }

            )

            Column(
                Modifier
                    .padding(end = 8.dp, top = 4.dp, bottom = 4.dp)
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = property.type,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = property.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = property.price.convertToLocalCurrency().toString().formatToLocalCurrency(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.End),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = -1)
@Composable
fun PropertyListItemPreview() {
    PropertyListItem(
        property = Property("1",
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
        ),
        isItemSelected = false,
        onItemClick = { }
    )
}

