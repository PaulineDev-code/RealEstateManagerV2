package com.openclassrooms.realestatemanagerv2.ui.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Media
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import com.openclassrooms.realestatemanagerv2.domain.model.Video

@Composable
fun DetailsMediaContent(photoList: List<Photo>,
                        videoList: List<Video>,
                        isInEditMode: Boolean = false,
                        onPhotoDeleted: (Media) -> Unit,
                        onVideoDeleted: (Media) -> Unit,
                        onVideoClicked:(String) -> Unit,
                        context: Context
                        ) {


    Column {
        Text(
            text = stringResource(id = R.string.media),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(8.dp)
        )
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(items = photoList) { _, photo ->
                Box(
                    modifier = Modifier
                        .height(128.dp)
                        .width(80.dp)
                        .padding(8.dp)
                ) {
                    // Photo
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(photo.mediaUrl)
                            .build(),
                        contentDescription = photo.description,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray),
                        contentScale = ContentScale.Crop,
                        onError = { error ->
                            Log.e(
                                "PhotoListCoilError",
                                error.result.throwable.localizedMessage ?: "Unknown error"
                            )
                        }
                    )
                    Log.d("Property photo url", photo.mediaUrl)

                    if (isInEditMode) {

                        IconButton(
                            onClick = { onPhotoDeleted(photo) },
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)

                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Delete photo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .align(Alignment.TopEnd)
                                    .background(Color.White, CircleShape)

                            )
                        }
                    }

                    // Surface avec texte
                    Surface(
                        color = Color(0, 0, 0, 150),
                        modifier = Modifier
                            .fillMaxHeight(0.34F)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                    ) {
                        Text(
                            photo.description,
                            fontSize = 10.sp,
                            color = Color.White,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
        //TODO: Add isInEditMode to hide delete button
        if (videoList.isNotEmpty()) {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(items = videoList) { _, video ->
                    Box(
                        modifier = Modifier
                            .height(128.dp)
                            .width(80.dp)
                            .padding(8.dp)
                            .background(Color.Black)
                            .clickable { onVideoClicked(video.mediaUrl) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play video",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .align(Alignment.Center)

                        )

                        IconButton(
                            onClick = { onVideoDeleted(video) },
                            modifier = Modifier
                                .padding(2.dp)
                                .size(16.dp)
                                .align(Alignment.TopEnd)


                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Delete video",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(Color.White, CircleShape)

                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun DetailsMediaContentPreview() {
    val pictureList: List<Photo> = listOf(
        Photo("", "Maison :D"),
        Photo("", "Méson ><"),
        Photo("", "Mehzon :O"),
    )
    val videoList: List<Video> = listOf(
        Video("", "etst1")
    )

    DetailsMediaContent(
        photoList = pictureList,
        videoList = videoList,
        onPhotoDeleted = {},
        onVideoDeleted = { /*TODO*/ },
        onVideoClicked = { /*TODO*/ },
        context = LocalContext.current
    )
}