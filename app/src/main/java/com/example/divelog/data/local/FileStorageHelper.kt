package com.example.divelog.data.local

import java.io.File

object FileStorageHelper {

    fun deleteFile(path: String): Boolean {
        return try {
            val file = File(path)
            file.exists() && file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}