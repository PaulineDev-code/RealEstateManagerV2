package com.openclassrooms.realestatemanagerv2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.Property
import com.openclassrooms.realestatemanagerv2.domain.model.PropertyStatus
import java.util.Date

@Preview
@Composable
fun PropertyItem(@PreviewParameter(PropertyPreviewParameterProvider::class) property: Property) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.surface
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current)
                    .data("https://www.istockphoto.com/fr/photo/belle-maison-avec-jardin-gm590279802-101488565")
                    .crossfade(true)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .build()),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit,
            )

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
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = property.type,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                )
                Text(
                    text = property.description,
                    style = MaterialTheme.typography.body2,
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

class PropertyPreviewParameterProvider : PreviewParameterProvider<Property> {
    private val photoList: List<Photo> = listOf(Photo("https://www.istockphoto.com/fr/photo/belle-maison-avec-jardin-gm590279802-101488565", "belle maison"))
    private val nearByPointsList: List<String> = listOf("Ecole","Boulangerie")
    override val values = sequenceOf(
        Property(1, "maison", 100000.00, 50.00, 2,
            "Une belle petite maison", photoList, null, "2 DownStreet NY",
            nearByPointsList, PropertyStatus.Available, "2023, 10, 14", null,
            Agent(1, "Léo l'agent", "0678910111", "toto@gmail.com")
        )
    )
}

