package com.example.divelog.data.local

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object DrawingStorageHelper {

    fun saveDrawingToInternalStorage(
        context: Context,
        bitmap: Bitmap
    ): String? {
        return try {
            val drawingsDir = File(context.filesDir, "dive_drawings")

            if (!drawingsDir.exists()) {
                drawingsDir.mkdirs()
            }

            val drawingFile = File(
                drawingsDir,
                "${UUID.randomUUID()}.png"
            )

            FileOutputStream(drawingFile).use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream
                )
            }

            drawingFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}