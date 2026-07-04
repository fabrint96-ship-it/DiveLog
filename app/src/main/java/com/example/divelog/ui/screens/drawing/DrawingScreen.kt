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
import com.example.divelog.ui.components.drawing.DiveDrawingToolbar
import androidx.compose.ui.graphics.Brush
import com.example.divelog.ui.components.navigation.DiveDrawingHeader
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.ui.components.buttons.DiveButton
import com.example.divelog.ui.components.buttons.DiveOutlinedButton
import com.example.divelog.ui.components.dialogs.DiveConfirmDialog
import androidx.compose.material.icons.filled.Undo

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
    var showClearDialog by remember { mutableStateOf(false) }

    val colors = listOf(
        Color.Blue,
        Color.Black,
        Color.Red,
        Color.Green,
        Color.Magenta
    )

    Scaffold(
        containerColor = DiveDeepBlue,
        topBar = {
            DiveDrawingHeader(
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                DiveDrawingToolbar(
                    colors = colors,
                    selectedColor = selectedColor,
                    strokeWidth = strokeWidth,
                    onColorSelected = { color ->
                        selectedColor = color
                    },
                    onStrokeWidthChange = { value ->
                        strokeWidth = value
                    }
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DiveFoam
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = DiveFoam,
                                shape = RoundedCornerShape(28.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = DiveTeal.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(28.dp)
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
                                        val newPoint = change.position

                                        currentPoints = currentPoints + newPoint

                                        currentPath.lineTo(
                                            newPoint.x,
                                            newPoint.y
                                        )
                                    },
                                    onDragEnd = {
                                        if (currentPoints.isNotEmpty()) {
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

                                        currentPoints = emptyList()
                                        currentPath = Path()
                                    },
                                    onDragCancel = {
                                        currentPoints = emptyList()
                                        currentPath = Path()
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

                            if (currentPoints.isNotEmpty()) {
                                drawPath(
                                    path = currentPath,
                                    color = selectedColor,
                                    style = Stroke(width = strokeWidth)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DiveOutlinedButton(
                        text = "Limpiar",
                        onClick = {
                            showClearDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    )

                    DiveOutlinedButton(
                        text = "Deshacer",
                        onClick = {
                            if (paths.isNotEmpty()) {
                                paths.removeAt(paths.lastIndex)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = paths.isNotEmpty()
                    )

                    DiveButton(
                        text = "Guardar",
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
                        enabled = paths.isNotEmpty()
                    )
                }
            }

            if (showClearDialog) {
                DiveConfirmDialog(
                    title = "Limpiar dibujo",
                    message = "¿Seguro que quieres borrar todo el dibujo actual?",
                    confirmText = "Limpiar",
                    onConfirm = {
                        paths.clear()
                        showClearDialog = false
                    },
                    onDismiss = {
                        showClearDialog = false
                    }
                )
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