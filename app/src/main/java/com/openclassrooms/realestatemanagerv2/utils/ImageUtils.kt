package com.openclassrooms.realestatemanagerv2.utils

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility class for handling image-related operations.
 *
 * This class provides helper methods to:
 * 1. Orchestrate Camera and Gallery intents.
 * 2. Manage Scoped Storage and legacy file system interactions for image capture.
 * 3. Persist images from external sources (like the Gallery) to the application's internal storage
 *    to ensure data durability and [FileProvider] compatibility.
 *
 * @property context The application context used for content resolution and file system access.
 */
class ImageUtils(private val context: Context) {

    /**
     * Temporary storage for the URI of a photo currently being captured or processed.
     */
    var currentPhotoUri: Uri? = null

    /**
     * Creates a new image file record in the MediaStore.
     *
     * Handles Scoped Storage requirements for Android 10 (API 29) and above by
     * placing files in the [Environment.DIRECTORY_PICTURES] subdirectory.
     *
     * @return The [Uri] of the newly created record, or null if an error occurs.
     */
    private fun createImageFile(): Uri? {
        val timestamp = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "JPEG_${timestamp}_")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/RealEstateManager")
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


    /**
     * Copies an image from a source [Uri] (typically from the Gallery) to the
     * application's private internal storage.
     *
     * This ensures the app maintains its own persistent copy of the media,
     * avoiding permission loss if the original source is deleted or moved.
     *
     * @param sourceUri The external [Uri] of the selected image.
     * @return A persistent, internal [Uri] via [FileProvider], or null on failure.
     */
    fun copyImageToInternalStorage(sourceUri: Uri): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null

            // Create a destination file in internal storage of the application
            val timestamp = System.currentTimeMillis()
            val dir = File(context.filesDir, "images").apply { mkdirs() }
            val destinationFile = File(dir, "IMG_$timestamp.jpg")

            val outputStream = FileOutputStream(destinationFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            // Return URI from local file using FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider", // Authority of the FileProvider
                destinationFile
            )
        } catch (e: IOException) {
            Log.e("ImageUtils", "Failed to copy image to internal storage", e)
            null
        }
    }

    /**
     * Constructs an Intent to trigger the system camera for image capture.
     *
     * Automatically handles [FileProvider] URI generation and permission granting
     * for different Android versions (API < 29 vs API >= 29).
     *
     * @return An [Intent] configured for [MediaStore.ACTION_IMAGE_CAPTURE].
     */
    fun getImageCaptureIntent(): Intent {
        val photoUri = if (Build.VERSION.SDK_INT >= 29) {
            createImageFile()
        } else {
            val dir = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File.createTempFile("photo_", ".jpg", dir)
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                .also { currentPhotoUri = it }
        }

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (photoUri != null) {
                clipData = ClipData.newRawUri("output", photoUri)
                val res = context.packageManager.queryIntentActivities(this, 0)
                for (ri in res) {
                    context.grantUriPermission(
                        ri.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
        }
    }

    /**
     * Constructs a standard picker Intent for the system Gallery.
     */
    private fun getGalleryIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

    /**
     * Creates a system chooser Intent allowing the user to pick between
     * taking a new photo with the camera or selecting an existing one from the gallery.
     *
     * @return A [Intent.ACTION_CHOOSER] configured with both Camera and Gallery options.
     */
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