package com.openclassrooms.realestatemanagerv2.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ext.SdkExtensions
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File

class ImageUtils(private val context: Context) {
    var currentPhotoUri: Uri? = null

    private fun createImageFile(): Uri? {
        val timestamp = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "JPEG_${timestamp}_")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        return try {
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.also {
                currentPhotoUri = it
            }
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error creating image file", e)
            null
        }
    }

    private fun getImageCaptureIntent(): Intent {
        val photoUri = createImageFile()
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
    }

    private fun getGalleryIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    fun getIntentChooser(): Intent {
        val captureIntent = getImageCaptureIntent()
        val galleryIntent = getGalleryIntent()

        return Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, galleryIntent)
            putExtra(Intent.EXTRA_TITLE, "Select from:")
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureIntent))
        }
    }
}


    /*private fun createImageFile(): File {
        val timestamp = System.currentTimeMillis()
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }


    private fun getImageCaptureIntent() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getImageCaptureIntentR()
    } else {
        getImageCaptureIntentLegacy()
    }



    @RequiresApi(Build.VERSION_CODES.R)
    private fun getImageCaptureIntentR(): Intent {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "JPEG_${System.currentTimeMillis()}_")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val photoUri = context.contentResolver.insert(contentUri, values)

        currentPhotoPath = photoUri?.toString()

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
    }

    private fun getImageCaptureIntentLegacy() = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        takePictureIntent.resolveActivity(context.packageManager)?.also {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: Exception) {
                null
            }

            photoFile?.also {
                val photoURI = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    it
                )

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
        }
    }

    private fun getGalleryIntent() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2) {
        Intent(MediaStore.ACTION_PICK_IMAGES)
    } else {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    fun getPathFromGalleryUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        cursor?.moveToFirst()

        val columnIndex = cursor?.getColumnIndex(projection[0])

        val path = columnIndex?.let { cursor.getString(it) }

        cursor?.close()

        return path
    }

    fun getIntentChooser(): Intent {

        val captureIntent = getImageCaptureIntent()
        val galleryIntent = getGalleryIntent()

        val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, galleryIntent)
            putExtra(Intent.EXTRA_TITLE, "Select from:")
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureIntent))
        }

        return chooserIntent
    }*/

///////////////////////

/*@RequiresApi(Build.VERSION_CODES.Q)
   private fun getImageCaptureIntentQ(): Intent {
       val values = ContentValues().apply {
           put(MediaStore.Images.Media.DISPLAY_NAME, "JPEG_${System.currentTimeMillis()}_")
           put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
           put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
       }

       val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
       val photoUri = context.contentResolver.insert(contentUri, values)

       currentPhotoPath = photoUri?.toString()

       return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
           putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
       }
   }*/

/*val writePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (writePermission == PackageManager.PERMISSION_GRANTED) {
            // Vous avez déjà la permission, continuez avec votre logique d'écriture
        } else {
            // Demandez la permission à l'utilisateur
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_STORAGE_PERMISSION
            )
        }*/