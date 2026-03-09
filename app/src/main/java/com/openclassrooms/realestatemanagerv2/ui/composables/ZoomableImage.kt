package com.openclassrooms.realestatemanagerv2.ui.composables

import android.content.Context
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Photo

@Composable
fun ZoomableImage (
    modifier: Modifier = Modifier,
    photo: Photo,
    context: Context,
    onScaleChanged: (Float) -> Unit = {},
    onSingleTap: () -> Unit = {}

) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
    var previousDistance by remember { mutableFloatStateOf(0f) }

    // Reset zoom when photo changes
    LaunchedEffect(photo.mediaUrl) {
        scale = 1f
        offsetX = 0f
        offsetY = 0f
    }

    LaunchedEffect(scale) {
        onScaleChanged(scale)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        if (scale > 1f) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        } else {
                            scale = 2f
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            offsetX = (centerX - tapOffset.x) * (scale - 1f)
                            offsetY = (centerY - tapOffset.y) * (scale - 1f)

                            val maxOffsetX = (size.width * (scale - 1f)) / 2f
                            val maxOffsetY = (size.height * (scale - 1f)) / 2f
                            offsetX = offsetX.coerceIn(-maxOffsetX, maxOffsetX)
                            offsetY = offsetY.coerceIn(-maxOffsetY, maxOffsetY)
                        }
                    },
                    onTap = { onSingleTap() }
                )
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    // Wait for first pointer
                    awaitFirstDown(requireUnconsumed = false)
                    previousDistance = 0f

                    do {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.filter { it.pressed }
                        val pointerCount = pressed.size

                        when {
                            // Multitouch : manage zoom
                            pointerCount >= 2 -> {
                                val (centroid, distance) = calculateCentroidAndDistance(pressed)

                                if (previousDistance > 0f) {
                                    val zoomFactor = distance / previousDistance
                                    val newScale = (scale * zoomFactor).coerceIn(1f, 4f)

                                    if (newScale > 1f) {
                                        val scaleDiff = newScale - scale
                                        val centerX = size.width / 2f
                                        val centerY = size.height / 2f

                                        offsetX += (centerX - centroid.x) * scaleDiff
                                        offsetY += (centerY - centroid.y) * scaleDiff

                                        val maxOffsetX = (size.width * (newScale - 1f)) / 2f
                                        val maxOffsetY = (size.height * (newScale - 1f)) / 2f
                                        offsetX = offsetX.coerceIn(-maxOffsetX, maxOffsetX)
                                        offsetY = offsetY.coerceIn(-maxOffsetY, maxOffsetY)
                                    }

                                    scale = newScale

                                    if (scale <= 1f) {
                                        scale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                    }
                                }

                                previousDistance = distance

                                event.changes.forEach { it.consume() }
                            }

                            // Single touch + zoom
                            pointerCount == 1 && scale > 1f -> {
                                val change = pressed.first()
                                if (change.positionChanged()) {
                                    val pan = change.position - change.previousPosition

                                    // Apply pan
                                    val maxOffsetX = (size.width * (scale - 1f)) / 2f
                                    val maxOffsetY = (size.height * (scale - 1f)) / 2f

                                    offsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                    offsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)

                                    event.changes.forEach { it.consume() }
                                }
                            }

                            pointerCount == 1 && scale <= 1f -> {
                                previousDistance = 0f
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(photo.mediaUrl)
                .crossfade(true)
                .build(),
            contentDescription = photo.description,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.Fit,
            onState = { state ->
                imageState = state
            }
        )

        // Loading state
        if (imageState is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Error state
        if (imageState is AsyncImagePainter.State.Error) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = stringResource(R.string.image_load_error),
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = stringResource(R.string.image_load_error),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

private fun calculateCentroidAndDistance(changes: List<PointerInputChange>): Pair<Offset, Float> {
    if (changes.size < 2) {
        return Pair(changes.firstOrNull()?.position ?: Offset.Zero, 0f)
    }

    // Centroid
    val centroid = Offset(
        x = changes.map { it.position.x }.average().toFloat(),
        y = changes.map { it.position.y }.average().toFloat()
    )

    // Distance
    val p1 = changes[0].position
    val p2 = changes[1].position
    val distance = kotlin.math.sqrt(
        (p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y)
    )

    return Pair(centroid, distance)
}
