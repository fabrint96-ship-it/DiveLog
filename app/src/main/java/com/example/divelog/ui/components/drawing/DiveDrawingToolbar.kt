package com.example.divelog.ui.components.drawing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.divelog.ui.components.cards.DiveSectionCard
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveTeal

@Composable
fun DiveDrawingToolbar(
    colors: List<Color>,
    selectedColor: Color,
    strokeWidth: Float,
    onColorSelected: (Color) -> Unit,
    onStrokeWidthChange: (Float) -> Unit
) {
    DiveSectionCard(
        title = "Herramientas de dibujo"
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Color",
                style = MaterialTheme.typography.bodyMedium,
                color = DiveFoam.copy(alpha = 0.85f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .background(color, CircleShape)
                            .border(
                                width = if (selectedColor == color) 3.dp else 1.dp,
                                color = if (selectedColor == color) DiveFoam else DiveFoam.copy(alpha = 0.45f),
                                shape = CircleShape
                            )
                            .clickable {
                                onColorSelected(color)
                            }
                            .padding(18.dp)
                    )
                }
            }

            Text(
                text = "Grosor del pincel: ${strokeWidth.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = DiveFoam.copy(alpha = 0.85f)
            )

            Slider(
                value = strokeWidth,
                onValueChange = onStrokeWidthChange,
                valueRange = 2f..18f,
                colors = SliderDefaults.colors(
                    thumbColor = DiveFoam,
                    activeTrackColor = DiveTeal,
                    inactiveTrackColor = DiveFoam.copy(alpha = 0.35f)
                )
            )
        }
    }
}