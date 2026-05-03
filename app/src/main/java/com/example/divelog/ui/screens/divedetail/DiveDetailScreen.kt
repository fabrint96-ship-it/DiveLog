package com.example.divelog.ui.screens.divedetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.divelog.data.model.Dive
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiveDetailScreen(
    dive: Dive?,
    onBackClick: () -> Unit,
    onDrawingClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onPhotoClick: (String) -> Unit,
    onDeleteClick: (Dive) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de inmersión",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (dive == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("No se ha encontrado la inmersión.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = dive.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = dive.location,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailRow("Fecha", dive.date)
                        DetailRow("Tipo de inmersión", dive.diveType)
                        DetailRow("Profundidad máxima", dive.maxDepth)
                        DetailRow("Duración", dive.duration)
                        DetailRow("Temperatura del agua", dive.waterTemperature)
                        DetailRow("Visibilidad", dive.visibility)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Notas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = dive.notes.ifBlank { "Sin notas añadidas." },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Fotos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (dive.photos.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(dive.photos) { photo ->
                                    Image(
                                        painter = rememberAsyncImagePainter(photo),
                                        contentDescription = "Foto de la inmersión",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(110.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                onPhotoClick(photo)
                                            }
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "No hay fotos añadidas.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Button(
                    onClick = onDrawingClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = "Dibujos"
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text("Abrir dibujos")
                }

                Button(
                    onClick = {
                        onEditClick(dive.id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar"
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text("Editar inmersión")
                }

                OutlinedButton(
                    onClick = {
                        showDeleteDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar"
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text("Eliminar inmersión")
                }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDeleteDialog = false
                        },
                        title = {
                            Text("Eliminar inmersión")
                        },
                        text = {
                            Text("¿Seguro que quieres eliminar esta inmersión? Esta acción no se puede deshacer.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onDeleteClick(dive)
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )

        Text(
            text = value.ifBlank { "No indicado" },
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}