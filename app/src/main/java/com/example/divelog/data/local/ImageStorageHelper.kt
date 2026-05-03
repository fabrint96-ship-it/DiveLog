package com.example.divelog.data.local

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object ImageStorageHelper {

    fun saveImagesToInternalStorage(
        context: Context,
        uris: List<Uri>
    ): List<String> {
        return uris.mapNotNull { uri ->
            saveImageToInternalStorage(context, uri)
        }
    }

    private fun saveImageToInternalStorage(
        context: Context,
        uri: Uri
    ): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return null

            val imagesDir = File(context.filesDir, "dive_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            val imageFile = File(
                imagesDir,
                "${UUID.randomUUID()}.jpg"
            )

            imageFile.outputStream().use { outputStream ->
                inputStream.use { input ->
                    input.copyTo(outputStream)
                }
            }

            imageFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}