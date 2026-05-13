package com.example.divelog.data.local

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.divelog.data.model.Dive
import java.io.File

object ShareHelper {

    fun shareDive(
        context: Context,
        dive: Dive
    ) {
        val shareText = buildString {
            appendLine("🌊 DiveLog - Inmersión")
            appendLine()
            appendLine("Título: ${dive.title}")
            appendLine("Lugar: ${dive.location}")
            appendLine("Fecha: ${dive.date}")
            appendLine("Tipo: ${dive.diveType.ifBlank { "No indicado" }}")
            appendLine("Profundidad: ${dive.maxDepth}")
            appendLine("Duración: ${dive.duration}")
            appendLine("Temperatura: ${dive.waterTemperature.ifBlank { "No indicada" }}")
            appendLine("Visibilidad: ${dive.visibility.ifBlank { "No indicada" }}")
            appendLine()
            appendLine("Notas:")
            appendLine(dive.notes.ifBlank { "Sin notas." })
        }

        val mediaPaths = dive.photos + dive.drawings

        if (mediaPaths.isEmpty()) {
            val textIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            context.startActivity(
                Intent.createChooser(textIntent, "Compartir inmersión")
            )
        } else {
            val uris = mediaPaths.mapNotNull { path ->
                val file = File(path)

                if (file.exists()) {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                } else {
                    null
                }
            }

            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(shareIntent, "Compartir inmersión")
            )
        }
    }
}