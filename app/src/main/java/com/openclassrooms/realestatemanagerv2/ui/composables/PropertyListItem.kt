package com.openclassrooms.realestatemanagerv2.ui.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus


@Composable
fun PropertyListItem(property: Property, onItemClick: (String) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Row(
            modifier = Modifier
                .clickable { onItemClick(property.id) }
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
                    text = property.price.toInt().toString() + "$",
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

