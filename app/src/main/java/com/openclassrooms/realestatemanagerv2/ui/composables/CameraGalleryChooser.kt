package com.openclassrooms.realestatemanagerv2.ui.composables

import android.app.Activity
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
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.utils.ImageUtils

@Composable
fun CameraGalleryChooser(onPhotoSelected: (String) -> Unit) {
    val context = LocalContext.current

    val imageUtils = ImageUtils(context)

    var currentPhoto: String?

    val openDialog = remember { mutableStateOf(false) }


    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data?.data
                currentPhoto = // Gallery Pick Intent
                    data?.toString() ?: // Camera intent
                            imageUtils.currentPhotoUri.toString()
                openDialog.value = true
                currentPhoto?.let(onPhotoSelected)

            }

        }

    /*if (currentPhoto != null && openDialog.value) {
    showDescriptionDialog(photoUri = currentPhoto!!, onPhotoAdded = onPhotoAdded)
    }*/


    Button(
        modifier = Modifier.padding(8.dp),
        onClick = {
            launcher.launch(imageUtils.getIntentChooser())
        }
    ) {
        Text(text = stringResource(id = R.string.import_a_photo))
    }

}


/*DisposableEffect(currentPhoto) {
    // Code à exécuter lorsque le DisposableEffect est créé
    onDispose {
        currentPhoto?.let { uri ->
            // Code à exécuter lorsque le DisposableEffect est supprimé
            showDescriptionDialog(
                photoUri = uri,
                onPhotoAdded = { photo ->
                    if (photo.description.isNotEmpty()) {
                        photoList.add(photo)
                    }
                }
            )
        }
    }
}*/

/*currentPhoto?.let {
    showDescriptionDialog(photoUri = it) { photo ->
        // Ajouter la photo avec la description à la liste
        if (photo.description.isNotEmpty()) {
            photoList.add(photo)
        }
    }
}*/

/*if (currentPhoto != null) {
        showDescriptionDialog(
            photoUri = currentPhoto!!,
            onPhotoAdded = onPhotoAdded
        )
    }*/


// You can also use a normal Image by decoding the currentPhoto to a Bitmap using the BitmapFactory
/*AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(currentPhoto)
        .placeholder(R.drawable.ic_add_photo)
        .error(R.drawable.ic_add_photo)
        .build(),
    contentDescription = null,
    *//*placeholder = painterResource(id = R.drawable.ic_add_photo),*//*
            modifier = Modifier
                .size(128.dp)
                .padding(8.dp)
                .clickable { launcher.launch(imageUtils.getIntent()) },
            contentScale = ContentScale.Fit,
            onError = { error ->
                Log.e("CoilError", error.result.throwable.localizedMessage ?: "Unknown error")
            }


        )*/

@Composable
@Preview(showBackground = true, backgroundColor = -1)
fun CameraGalleryChooserPreview() {
    CameraGalleryChooser(onPhotoSelected = {})
}


