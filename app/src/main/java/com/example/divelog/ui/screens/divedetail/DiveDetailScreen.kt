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
import com.example.divelog.domain.model.Dive
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
import androidx.compose.animation.animateContentSize
import com.example.divelog.domain.model.GalleryItem
import com.example.divelog.domain.model.GalleryItemType
import androidx.compose.material3.AssistChip
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.Brush
import com.example.divelog.ui.components.navigation.DiveDetailHeader
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.ui.components.cards.DiveSectionCard
import com.example.divelog.ui.theme.DiveFoam
import androidx.compose.foundation.shape.CircleShape
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.ui.components.buttons.DiveButton
import com.example.divelog.ui.components.buttons.DiveOutlinedButton
import com.example.divelog.ui.components.dialogs.DiveConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiveDetailScreen(
    dive: Dive?,
    onBackClick: () -> Unit,
    onDrawingClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onPhotoClick: (String) -> Unit,
    onDeleteGalleryItem: (GalleryItem) -> Unit,
    onShareClick: (Dive) -> Unit,
    onDeleteClick: (Dive) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var galleryItemToDelete by remember { mutableStateOf<GalleryItem?>(null) }

    Scaffold(
        containerColor = DiveDeepBlue,
        topBar = {
            DiveDetailHeader(
                title = dive?.title ?: "Detalle de inmersión",
                location = dive?.location ?: "",
                onBackClick = onBackClick
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
            val photoItems = dive.photos.map {
                GalleryItem(
                    uri = it,
                    type = GalleryItemType.PHOTO
                )
            }

            val drawingItems = dive.drawings.map {
                GalleryItem(
                    uri = it,
                    type = GalleryItemType.DRAWING
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DiveDeepBlue,
                                DiveOceanBlue,
                                DiveTeal.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    DiveSectionCard(
                        title = "Información de la inmersión"
                    ) {
                        DetailRow("Fecha", dive.date)
                        DetailRow("Tipo de inmersión", dive.diveType)
                        DetailRow("Profundidad máxima", dive.maxDepth)
                        DetailRow("Duración", dive.duration)
                        DetailRow("Temperatura del agua", dive.waterTemperature)
                        DetailRow("Visibilidad", dive.visibility)
                    }

                    DiveSectionCard(
                        title = "Notas"
                    ) {
                        Text(
                            text = dive.notes.ifBlank { "Sin notas añadidas." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = DiveFoam.copy(alpha = 0.85f)
                        )
                    }

                    GallerySection(
                        title = "Fotos",
                        emptyText = "No hay fotos guardadas.",
                        items = photoItems,
                        onPhotoClick = onPhotoClick,
                        onDeleteClick = { item ->
                            galleryItemToDelete = item
                        }
                    )

                    GallerySection(
                        title = "Dibujos",
                        emptyText = "No hay dibujos guardados.",
                        items = drawingItems,
                        onPhotoClick = onPhotoClick,
                        onDeleteClick = { item ->
                            galleryItemToDelete = item
                        }
                    )

                    DiveButton(
                        text = "Abrir dibujos",
                        onClick = onDrawingClick
                    )

                    DiveButton(
                        text = "Editar inmersión",
                        onClick = {
                            onEditClick(dive.id)
                        }
                    )

                    DiveButton(
                        text = "Compartir inmersión",
                        onClick = {
                            onShareClick(dive)
                        }
                    )

                    DiveOutlinedButton(
                        text = "Eliminar inmersión",
                        onClick = {
                            showDeleteDialog = true
                        }
                    )

                    if (showDeleteDialog) {
                        DiveConfirmDialog(
                            title = "Eliminar inmersión",
                            message = "¿Seguro que quieres eliminar esta inmersión? Esta acción no se puede deshacer.",
                            confirmText = "Eliminar",
                            onConfirm = {
                                onDeleteClick(dive)
                                showDeleteDialog = false
                            },
                            onDismiss = {
                                showDeleteDialog = false
                            }
                        )
                    }
                }
            }



            if (galleryItemToDelete != null) {
                DiveConfirmDialog(
                    title = "Eliminar elemento",
                    message = "¿Seguro que quieres eliminar este elemento de la galería?",
                    confirmText = "Eliminar",
                    onConfirm = {
                        galleryItemToDelete?.let { item ->
                            onDeleteGalleryItem(item)
                        }
                        galleryItemToDelete = null
                    },
                    onDismiss = {
                        galleryItemToDelete = null
                    }
                )
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
            color = DiveFoam.copy(alpha = 0.62f)
        )

        Text(
            text = value.ifBlank { "No indicado" },
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = DiveFoam
        )
    }
}

@Composable
fun GallerySection(
    title: String,
    emptyText: String,
    items: List<GalleryItem>,
    onPhotoClick: (String) -> Unit,
    onDeleteClick: (GalleryItem) -> Unit
) {
    DiveSectionCard(
        title = title
    ) {
        if (items.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { item ->
                    Box(
                        modifier = Modifier.size(112.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(item.uri),
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(112.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    onPhotoClick(item.uri)
                                }
                        )

                        IconButton(
                            onClick = {
                                onDeleteClick(item)
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(
                                    color = DiveDeepBlue.copy(alpha = 0.82f),
                                    shape = CircleShape
                                )
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar elemento",
                                tint = DiveFoam,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodyMedium,
                color = DiveFoam.copy(alpha = 0.75f)
            )
        }
    }
}