package com.openclassrooms.realestatemanagerv2.ui.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus


@Composable
fun PropertyListItem(property: Property, onItemClick: (String)->Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.surface
    ) {

        //
        Row(modifier = Modifier.clickable { onItemClick(property.id) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(property.media.filterIsInstance<Photo>()[0].mediaUrl)
/*
                    .placeholder(R.drawable.ic_launcher_foreground)
*/
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit,
                onError = { error ->
                    Log.e("CoilError", error.result.throwable.localizedMessage ?: "Unknown error")
                }

            )

            /*R.drawable.ic_launcher_foreground*/



            Column(Modifier.padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = property.type,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                )
                Text(
                    modifier = Modifier.height(96.dp),
                    text = property.description,
                    style = MaterialTheme.typography.body2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = property.price.toString() + "$",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.secondaryVariant
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

