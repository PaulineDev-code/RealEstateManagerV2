package com.openclassrooms.realestatemanagerv2.ui.composables

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayer(videoUri: String, modifier: Modifier = Modifier,context: Context, onClose: () -> Unit ) {
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier
        .background(Color.Black)) {

        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }
            },
            modifier = Modifier.matchParentSize()
        )

        // Button to close the video player
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopEnd)
        ) {
            Column {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close video player",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.background(Color.White, CircleShape).align(CenterHorizontally)

                )

                Text(text = "Close", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VideoPLayerPreview() {
    VideoPlayer(videoUri = "", context = LocalContext.current) {

    }
}
