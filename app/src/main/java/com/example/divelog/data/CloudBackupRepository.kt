package com.example.divelog.data.remote

import com.example.divelog.domain.model.Dive
import io.github.jan.supabase.postgrest.from

class CloudBackupRepository {

    private val client = SupabaseClientProvider.client
    private val authRepository = SupabaseAuthRepository()
    private val storageRepository = SupabaseStorageRepository()

    suspend fun uploadDives(dives: List<Dive>) {
        val userId = authRepository.currentUserId()
            ?: throw IllegalStateException("Usuario no autenticado")

        val remoteDives = dives.map { dive ->

            val uploadedPhotos = dive.photos.map { path ->
                if (isRemoteMedia(path)) {
                    path.substringAfter("/sign/dive-media/")
                        .substringBefore("?")
                } else {
                    storageRepository.uploadMedia(path, "photos")
                }
            }

            val uploadedDrawings = dive.drawings.map { path ->
                if (isRemoteMedia(path)) {
                    path.substringAfter("/sign/dive-media/")
                        .substringBefore("?")
                } else {
                    storageRepository.uploadMedia(path, "drawings")
                }
            }

            dive.copy(
                photos = uploadedPhotos,
                drawings = uploadedDrawings
            ).toSupabaseDto(userId)
        }

        client.from("dives")
            .upsert(remoteDives) {
                onConflict = "user_id,sync_id"
            }
    }

    suspend fun downloadDives(): List<Dive> {
        val userId = authRepository.currentUserId()
            ?: throw IllegalStateException("Usuario no autenticado")

        val remoteDives = client.from("dives")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupabaseDiveDto>()

        return remoteDives.map { remoteDive ->

            val signedPhotos = remoteDive.photos?.map { path ->
                storageRepository.createSignedUrl(path)
            } ?: emptyList()

            val signedDrawings = remoteDive.drawings?.map { path ->
                storageRepository.createSignedUrl(path)
            } ?: emptyList()

            remoteDive.copy(
                photos = signedPhotos,
                drawings = signedDrawings
            ).toDive()
        }
    }

    private fun isRemoteMedia(path: String): Boolean {
        return path.contains("supabase.co") ||
                path.contains("/storage/v1/") ||
                path.startsWith("http") ||
                path.contains("/photos/") ||
                path.contains("/drawings/")
    }
}