package com.example.divelog.data.remote

import io.github.jan.supabase.storage.storage
import java.io.File
import java.util.UUID
import kotlin.time.Duration.Companion.hours

class SupabaseStorageRepository {

    private val client = SupabaseClientProvider.client
    private val authRepository = SupabaseAuthRepository()

    suspend fun uploadMedia(localPath: String, folder: String): String {
        val userId = authRepository.currentUserId()
            ?: throw IllegalStateException("Usuario no autenticado")

        val file = File(localPath)

        if (!file.exists()) {
            return cleanRemotePath(localPath)
        }

        val extension = file.extension.ifBlank { "jpg" }
        val remotePath = "$userId/$folder/${UUID.randomUUID()}.$extension"

        client.storage
            .from("dive-media")
            .upload(
                path = remotePath,
                data = file.readBytes()
            ) {
                upsert = true
            }

        return remotePath
    }

    suspend fun createSignedUrl(remotePath: String): String {
        return client.storage
            .from("dive-media")
            .createSignedUrl(
                path = cleanRemotePath(remotePath),
                expiresIn = 1.hours
            )
    }

    suspend fun deleteMedia(remotePath: String) {
        val cleanPath = cleanRemotePath(remotePath)

        if (cleanPath.isBlank()) return
        if (cleanPath.startsWith("content://")) return
        if (cleanPath.startsWith("/data/")) return

        client.storage
            .from("dive-media")
            .delete(cleanPath)
    }

    fun cleanRemotePath(path: String): String {
        return when {
            path.contains("/sign/dive-media/") -> {
                path.substringAfter("/sign/dive-media/")
                    .substringBefore("?")
                    .removePrefix("/")
            }

            path.contains("/object/dive-media/") -> {
                path.substringAfter("/object/dive-media/")
                    .substringBefore("?")
                    .removePrefix("/")
            }

            path.contains("/storage/v1/object/sign/dive-media/") -> {
                path.substringAfter("/storage/v1/object/sign/dive-media/")
                    .substringBefore("?")
                    .removePrefix("/")
            }

            path.contains("/storage/v1/object/dive-media/") -> {
                path.substringAfter("/storage/v1/object/dive-media/")
                    .substringBefore("?")
                    .removePrefix("/")
            }

            else -> path.removePrefix("/")
        }
    }
}