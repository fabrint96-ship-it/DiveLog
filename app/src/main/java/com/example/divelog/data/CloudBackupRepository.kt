package com.example.divelog.data.remote

import android.util.Log
import com.example.divelog.domain.model.Dive
import io.github.jan.supabase.postgrest.from

class CloudBackupRepository {

    private val client = SupabaseClientProvider.client
    private val authRepository = SupabaseAuthRepository()
    private val storageRepository = SupabaseStorageRepository()

    suspend fun uploadDives(dives: List<Dive>) {
        val userId = authRepository.currentUserId()
            ?: throw IllegalStateException("Usuario no autenticado")

        val existingRemoteDives = client.from("dives")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupabaseDiveDto>()

        val remoteDives = dives.map { dive ->

            val existingRemoteDive = existingRemoteDives.firstOrNull {
                it.syncId == dive.syncId
            }

            val uploadedPhotos = dive.photos.map { path ->
                if (isCloudMedia(path)) {
                    storageRepository.cleanRemotePath(path)
                } else {
                    storageRepository.uploadMedia(path, "photos")
                }
            }

            val uploadedDrawings = dive.drawings.map { path ->
                if (isCloudMedia(path)) {
                    storageRepository.cleanRemotePath(path)
                } else {
                    storageRepository.uploadMedia(path, "drawings")
                }
            }

            val oldPhotos = existingRemoteDive?.photos?.map {
                storageRepository.cleanRemotePath(it)
            } ?: emptyList()

            val oldDrawings = existingRemoteDive?.drawings?.map {
                storageRepository.cleanRemotePath(it)
            } ?: emptyList()

            val deletedPhotos = oldPhotos.filter { oldPath ->
                oldPath !in uploadedPhotos
            }

            val deletedDrawings = oldDrawings.filter { oldPath ->
                oldPath !in uploadedDrawings
            }

            deletedPhotos.forEach { path ->
                storageRepository.deleteMedia(path)
            }

            deletedDrawings.forEach { path ->
                storageRepository.deleteMedia(path)
            }

            dive.copy(
                photos = uploadedPhotos,
                drawings = uploadedDrawings
            ).toSupabaseDto(userId)
        }

        if (remoteDives.isNotEmpty()) {
            client.from("dives")
                .upsert(remoteDives) {
                    onConflict = "user_id,sync_id"
                }
        }

        Log.d("SUPABASE_BACKUP", "Backup completado: ${remoteDives.size} inmersiones")
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

        return remoteDives
            .filter { it.syncId.isNotBlank() }
            .distinctBy { it.syncId }
            .map { remoteDive ->

                val signedPhotos = remoteDive.photos
                    ?.map { storageRepository.cleanRemotePath(it) }
                    ?.map { storageRepository.createSignedUrl(it) }
                    ?: emptyList()

                val signedDrawings = remoteDive.drawings
                    ?.map { storageRepository.cleanRemotePath(it) }
                    ?.map { storageRepository.createSignedUrl(it) }
                    ?: emptyList()

                remoteDive.copy(
                    photos = signedPhotos,
                    drawings = signedDrawings
                ).toDive()
            }
    }

    private fun isCloudMedia(path: String): Boolean {
        return path.contains("supabase.co") ||
                path.contains("/storage/v1/") ||
                path.contains("/sign/dive-media/") ||
                path.contains("/object/dive-media/") ||
                path.contains("/photos/") ||
                path.contains("/drawings/")
    }
}