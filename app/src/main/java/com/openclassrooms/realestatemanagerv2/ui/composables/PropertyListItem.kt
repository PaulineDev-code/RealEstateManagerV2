package com.openclassrooms.realestatemanagerv2.ui.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
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
            /*.clickable (onClick = { onItemClick(property.id) })*/
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

/*SubcomposeAsyncImage(
                model = "https://www.istockphoto.com/fr/photo/belle-maison-avec-jardin-gm590279802-101488565",
                contentDescription = "maison",
                modifier = Modifier.size(128.dp)
            )  {
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                    CircularProgressIndicator()
                } else {
                    SubcomposeAsyncImageContent()
                }
            }*/
/*@Preview
@Composable
fun PreviewPropertyItem(@PreviewParameter(PropertyPreviewParameterProvider::class) property: Property) {
    PropertyItem(property = property)
}*/

/*class PropertyPreviewParameterProvider : PreviewParameterProvider<Property> {
    private val mediaLists: List<Media> = listOf(Photo("https://www.istockphoto.com/fr/photo/belle-maison-avec-jardin-gm590279802-101488565", "belle maison"))
    private val nearByPointsList: List<String> = listOf("Ecole","Boulangerie")
    override val values = sequenceOf(
        Property("1", "maison", 100000.00, 50.00, 2,
            "Une belle petite maison", mediaLists, "2 DownStreet NY",
            nearByPointsList, PropertyStatus.Available, "2023, 10, 14", null,
            Agent("1", "Léo l'agent", "0678910111", "toto@gmail.com")
        )
    )
}*/

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

