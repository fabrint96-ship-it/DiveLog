package com.example.divelog.ui.components.maps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.divelog.ui.components.cards.DiveSectionCard
import com.example.divelog.ui.theme.Dimens
import com.example.divelog.ui.theme.DiveFoam

@Composable
fun DiveMiniMapCard(
    latitude: Double?,
    longitude: Double?,
    locationName: String,
    interactive: Boolean = true
) {
    if (latitude == null || longitude == null) return

    DiveSectionCard(
        title = "Ubicación"
    ) {
        Column {
            Text(
                text = locationName.ifBlank { "Ubicación seleccionada" },
                style = MaterialTheme.typography.bodyMedium,
                color = DiveFoam.copy(alpha = 0.85f)
            )

            MapLibreView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.CardImageHeight),
                latitude = latitude,
                longitude = longitude,
                zoom = 11.0,
                interactive = interactive
            )
        }
    }
}