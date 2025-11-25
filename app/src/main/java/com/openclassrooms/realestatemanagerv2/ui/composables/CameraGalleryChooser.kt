package com.openclassrooms.realestatemanagerv2.ui.composables

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.utils.ImageUtils

@Composable
fun CameraGalleryChooser(onPhotoSelected: (String) -> Unit) {

    val context = LocalContext.current

    val permission = if (Build.VERSION.SDK_INT >= 33)
        Manifest.permission.READ_MEDIA_IMAGES
    else if (Build.VERSION.SDK_INT <= 28) {
        Manifest.permission.READ_EXTERNAL_STORAGE
        Manifest.permission.WRITE_EXTERNAL_STORAGE }
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

    val imageUtils = ImageUtils(context)

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data?.data
                if (data != null) {
                    imageUtils.currentPhotoUri = null
                    imageUtils.copyImageToInternalStorage(data)?.toString()?.let(onPhotoSelected)
                } else {
                    val captured = imageUtils.currentPhotoUri
                    if (captured != null) {
                        imageUtils.copyImageToInternalStorage(captured)?.toString()?.let(onPhotoSelected)
                    }
                }
            } else {
                imageUtils.currentPhotoUri?.let { uri ->
                    runCatching { context.contentResolver.delete(uri, null, null) }
                }
                imageUtils.currentPhotoUri = null
            }
        }

    val askPerm = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launcher.launch(imageUtils.getIntentChooser())
        else Toast.makeText(
            context,
            "Image permission denied",
            Toast.LENGTH_LONG
        ).show()
    }

    Button(
        modifier = Modifier.padding(8.dp),
        onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                askPerm.launch(permission)
            } else {
                launcher.launch(imageUtils.getIntentChooser())
            }
        }
    ) {
        Text(text = stringResource(id = R.string.import_a_photo))
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = -1)
fun CameraGalleryChooserPreview() {
    CameraGalleryChooser(onPhotoSelected = {})
}


