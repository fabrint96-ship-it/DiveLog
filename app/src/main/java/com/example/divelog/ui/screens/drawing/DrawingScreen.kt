package com.example.divelog.ui.screens.drawing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

data class DrawingPath(
    val path: Path,
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    onBackClick: () -> Unit,
    onSaveDrawing: (Bitmap) -> Unit
) {
    val paths = remember { mutableStateListOf<DrawingPath>() }

    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var currentPath by remember { mutableStateOf(Path()) }
    var selectedColor by remember { mutableStateOf(Color.Blue) }
    var strokeWidth by remember { mutableFloatStateOf(6f) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val colors = listOf(
        Color.Blue,
        Color.Black,
        Color.Red,
        Color.Green,
        Color.Magenta
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dibujos de la inmersión",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Croquis submarino",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Dibuja rutas, zonas interesantes, fauna vista o detalles de la inmersión.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (selectedColor == color) 3.dp else 1.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = color
                            }
                            .padding(16.dp)
                    )
                }
            }

            Column {
                Text(
                    text = "Grosor del pincel: ${strokeWidth.toInt()}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    value = strokeWidth,
                    onValueChange = { strokeWidth = it },
                    valueRange = 2f..18f
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .pointerInput(selectedColor, strokeWidth) {
                        canvasSize = size

                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPoints = listOf(offset)
                                currentPath = Path().apply {
                                    moveTo(offset.x, offset.y)
                                }
                            },
                            onDrag = { change, _ ->
                                currentPoints = currentPoints + change.position
                                currentPath.lineTo(
                                    change.position.x,
                                    change.position.y
                                )

                                paths.add(
                                    DrawingPath(
                                        path = Path().apply {
                                            addPath(currentPath)
                                        },
                                        points = currentPoints,
                                        color = selectedColor,
                                        strokeWidth = strokeWidth
                                    )
                                )
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    canvasSize = IntSize(
                        width = size.width.toInt(),
                        height = size.height.toInt()
                    )

                    paths.forEach { drawingPath ->
                        drawPath(
                            path = drawingPath.path,
                            color = drawingPath.color,
                            style = Stroke(width = drawingPath.strokeWidth)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        paths.clear()
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Limpiar"
                    )
                    Text(" Limpiar")
                }

                Button(
                    onClick = {
                        if (paths.isNotEmpty() && canvasSize.width > 0 && canvasSize.height > 0) {
                            val bitmap = createBitmapFromPaths(
                                width = canvasSize.width,
                                height = canvasSize.height,
                                paths = paths
                            )

                            onSaveDrawing(bitmap)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar"
                    )
                    Text(" Guardar")
                }
            }
        }
    }
}

fun createBitmapFromPaths(
    width: Int,
    height: Int,
    paths: List<DrawingPath>
): Bitmap {
    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)

    paths.forEach { drawingPath ->
        val androidPath = AndroidPath()

        drawingPath.points.forEachIndexed { index, offset ->
            if (index == 0) {
                androidPath.moveTo(offset.x, offset.y)
            } else {
                androidPath.lineTo(offset.x, offset.y)
            }
        }

        val paint = Paint().apply {
            color = drawingPath.color.toArgb()
            strokeWidth = drawingPath.strokeWidth
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }

        canvas.drawPath(androidPath, paint)
    }

    return bitmap
}