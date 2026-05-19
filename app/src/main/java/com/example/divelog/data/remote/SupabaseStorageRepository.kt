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
            return localPath
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
        if (remotePath.startsWith("http")) {
            return remotePath
        }

        return client.storage
            .from("dive-media")
            .createSignedUrl(
                path = remotePath,
                expiresIn = 1.hours
            )
    }
}