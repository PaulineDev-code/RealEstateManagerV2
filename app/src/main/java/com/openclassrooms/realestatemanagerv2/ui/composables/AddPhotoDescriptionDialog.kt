package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddPhotoDescriptionDialog(photoUri: String,
                              photoDescription: String,
                              onPhotoUriChange: (String) -> Unit,
                              onPhotoDescriptionChange: (String) -> Unit,
                              onAddPhotoDescriptionClick: () -> Unit,
                              ) {


    AlertDialog(
        onDismissRequest = {
            // Clear photo uri and description for next use
            onPhotoDescriptionChange("")
            onPhotoUriChange("")
        },
        title = { Text("Add Description") },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Display selected photo
                Image(
                    painter = rememberAsyncImagePainter(model = photoUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(192.dp)
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "Add a quick description for this photo",
                    modifier = Modifier.padding(8.dp)
                )

                CustomTextField(
                    label = { Text("Description") },
                    placeHolder = { Text(text = "Add a quick description") },
                    modifier = Modifier.padding(8.dp),
                    onTextChange = onPhotoDescriptionChange,
                    text = photoDescription
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (photoDescription.isNotEmpty()) {
                        onAddPhotoDescriptionClick()
                    }
                    // Fermer la boîte de dialogue


                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = {
                // Clear photo uri and description for next use
                onPhotoUriChange("")
                onPhotoDescriptionChange("")
            }) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun AddPhotoDescriptionDialogPreview() {
    AddPhotoDescriptionDialog(photoUri = "",
        photoDescription = "",
        onAddPhotoDescriptionClick = {},
        onPhotoUriChange = {},
        onPhotoDescriptionChange = {}
    )
}