package com.openclassrooms.realestatemanagerv2.ui.composables

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.openclassrooms.realestatemanagerv2.R

@Composable
fun VideoPicker(onVideoAdded: (String) -> Unit) {

    val context = LocalContext.current

    val permission = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_VIDEO
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    val singleVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { videoPickedUri ->
            if (videoPickedUri != null) {
                Log.d("VideoPicker", "Video picked: $videoPickedUri")
                onVideoAdded(videoPickedUri.toString())
            } else Toast.makeText(context, "Video selection failed", Toast.LENGTH_LONG).show()
        })

    val askPerm = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) singleVideoPicker.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
        ) else Toast.makeText(
            context,
            "Video permission denied",
            Toast.LENGTH_LONG
        ).show()
    }

    Button(
        modifier = Modifier.padding(8.dp),
        onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                askPerm.launch(permission)
            } else {
                singleVideoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
            }
        }
    ) {
        Text(text = stringResource(id = R.string.import_a_video))
    }

}