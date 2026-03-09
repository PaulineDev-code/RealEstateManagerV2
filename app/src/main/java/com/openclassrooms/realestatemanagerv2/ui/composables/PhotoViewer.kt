package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Photo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PhotoViewer(
    modifier: Modifier = Modifier,
    photos: List<Photo>,
    selectedPhotoIndex: Int = 0,
    onPhotoIndexChanged: (Int) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Handle empty list
    if (photos.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_photos_available),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    val pagerState = rememberPagerState(
        initialPage = selectedPhotoIndex.coerceIn(0, photos.size - 1),
        pageCount = { photos.size }
    )
    var showControls by remember { mutableStateOf(true) }
    var currentScale by remember { mutableFloatStateOf(1f) }


    // Auto-hide controls after 3 seconds
    LaunchedEffect(showControls, pagerState.currentPage) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }

    LaunchedEffect(selectedPhotoIndex) {
        if (pagerState.currentPage != selectedPhotoIndex) {
            pagerState.scrollToPage(selectedPhotoIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onPhotoIndexChanged(pagerState.currentPage)
    }

    // Intercept back press
    BackHandler {
        onClose()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Photo pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = currentScale <= 1f,
            key = { photos[it].mediaUrl },
        ) { page ->
            ZoomableImage(
                photo = photos[page],
                context = context,
                onScaleChanged = { newScale ->
                    currentScale = newScale
                },
                onSingleTap = {
                    showControls = !showControls
                }
            )
        }

        // UI Controls
        AnimatedVisibility(
            visible = showControls,
            modifier = Modifier.align(Alignment.TopCenter),
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            // Page counter
            Surface(
                modifier = Modifier.padding(top = 16.dp),
                color = Color(0, 0, 0, 180),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = stringResource(
                        R.string.photo_count,
                        pagerState.currentPage + 1, photos.size),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Close button
        AnimatedVisibility(
            visible = showControls,
            modifier = Modifier.align(Alignment.TopEnd),
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(64.dp)
                    .padding(16.dp)
            ) {
                Icon(
                    modifier = Modifier.background(Color.White, CircleShape),
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.close_photo_viewer),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Description overlay
        AnimatedVisibility(
            visible = showControls,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Surface(
                color = Color(0, 0, 0, 180),
                modifier = Modifier
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = photos[pagerState.currentPage].description,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Previous button
        if (pagerState.currentPage > 0) {
            AnimatedVisibility(
                visible = showControls,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
                enter = slideInHorizontally(initialOffsetX = { -it }),
                exit = fadeOut()

            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0, 0, 0, 180), CircleShape)
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.previous_photo),
                        tint = Color.White
                    )
                }
            }
        }

        // Next button
        if (pagerState.currentPage < photos.size - 1) {
            AnimatedVisibility(
                visible = showControls,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0, 0, 0, 180), CircleShape)
                ) {
                    Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        modifier = Modifier.size(32.dp),
                        contentDescription = stringResource(R.string.next_photo),
                        tint = Color.White
                    )
                }
            }
        }

    }
}