package com.example.divelog.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal

@Composable
fun DiveDetailHeader(
    title: String,
    location: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DiveDeepBlue,
                        DiveOceanBlue,
                        DiveTeal
                    )
                ),
                shape = RoundedCornerShape(
                    bottomStart = 28.dp,
                    bottomEnd = 28.dp
                )
            )
            .padding(
                start = 12.dp,
                end = 20.dp,
                top = 28.dp,
                bottom = 22.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = DiveFoam
                )
            }

            Icon(
                imageVector = Icons.Default.Water,
                contentDescription = null,
                tint = DiveFoam
            )

            Column {
                Text(
                    text = title.ifBlank { "Detalle de inmersión" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DiveFoam
                )

                Text(
                    text = location.ifBlank { "Lugar no indicado" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = DiveFoam.copy(alpha = 0.82f)
                )
            }
        }
    }
}