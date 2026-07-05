package com.example.divelog.ui.components.cards

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.divelog.domain.model.Dive
import androidx.compose.ui.graphics.Brush
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.ui.components.maps.MapLibreView
import com.example.divelog.ui.theme.Dimens

@Composable
fun DiveCard(
    dive: Dive,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = DiveDeepBlue.copy(alpha = 0.92f)
        )
    ) {
        Column {
            DiveCardImage(dive = dive)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = dive.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DiveFoam
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = dive.location.ifBlank { "Lugar no indicado" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = DiveFoam.copy(alpha = 0.78f)
                    )
                }

                Text(
                    text = dive.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = DiveFoam.copy(alpha = 0.65f)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DiveInfoChip(
                        icon = Icons.Default.Water,
                        text = dive.maxDepth
                    )

                    DiveInfoChip(
                        icon = Icons.Default.Timer,
                        text = dive.duration
                    )

                    if (dive.diveType.isNotBlank()) {
                        DiveInfoChip(
                            icon = Icons.Default.Water,
                            text = dive.diveType
                        )
                    }

                    if (dive.waterTemperature.isNotBlank()) {
                        DiveInfoChip(
                            icon = Icons.Default.WbSunny,
                            text = dive.waterTemperature
                        )
                    }

                    if (dive.visibility.isNotBlank()) {
                        DiveInfoChip(
                            icon = Icons.Default.Visibility,
                            text = dive.visibility
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiveCardImage(
    dive: Dive
) {
    when {
        dive.photos.isNotEmpty() -> {
            Image(
                painter = rememberAsyncImagePainter(dive.photos.first()),
                contentDescription = "Foto de ${dive.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.CardImageHeight)
            )
        }

        dive.latitude != null && dive.longitude != null -> {
            MapLibreView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.CardImageHeight),
                latitude = dive.latitude,
                longitude = dive.longitude,
                zoom = 11.0,
                interactive = false
            )
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.CardImageHeight)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                DiveOceanBlue,
                                DiveTeal
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = null,
                    tint = DiveFoam,
                    modifier = Modifier.size(52.dp)
                )
            }
        }
    }
}

@Composable
private fun DiveInfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    AssistChip(
        onClick = {},
        label = {
            Text(text = text)
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = DiveTeal.copy(alpha = 0.22f),
            labelColor = DiveFoam,
            leadingIconContentColor = DiveFoam
        )
    )
}