package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo


@Composable
fun PhotoItem(/*@PreviewParameter(PhotoPreviewParameterProvider::class)*/ media: Media) {
    Box(Modifier.padding(8.dp)) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
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
        Text(
            text = media.description,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomCenter).background(Color.Gray, RectangleShape)

        )
    }

}

@Preview
@Composable
fun PhototItemPreview() {
    var mediaPreview : Media = Photo("", "descriptionTest")
    PhotoItem(mediaPreview)

}

/*
class PhotoPreviewParameterProvider : PreviewParameterProvider<Media> {
    private val mediaLists: List<Media> = listOf(Photo("https://www.istockphoto.com/fr/photo/belle-maison-avec-jardin-gm590279802-101488565", "belle maison"))
    private val nearByPointsList: List<String> = listOf("Ecole","Boulangerie")
    override val values = sequenceOf(
        Photo("https://www.istockphoto.com/fr/photo/belle-maison-avec-jardin-gm590279802-101488565", "Facade")
    )
}*/
