package com.example.divelog.ui.screens.photoviewer

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PhotoViewerScreen(
    photoUri: String,
    initialNote: String,
    onSavePostcardNote: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var showZoomIndicator by remember { mutableStateOf(false) }
    var showPostcardBack by remember { mutableStateOf(false) }
    var postcardText by remember(initialNote) {
        mutableStateOf(initialNote)
    }

    val latestPostcardText by rememberUpdatedState(postcardText)

    DisposableEffect(Unit) {
        onDispose {
            if (latestPostcardText.isNotBlank()) {
                onSavePostcardNote(latestPostcardText)
            }
        }
    }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        DiveDeepBlue,
                        DiveOceanBlue,
                        DiveTeal.copy(alpha = 0.85f)
                    )
                )
            )
    ) {
        AnimatedContent(
            targetState = showPostcardBack,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.96f))
                    .togetherWith(
                        fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 1.04f)
                    )
            },
            label = "PhotoPostcardTransition"
        ) { isBack ->

            if (!isBack) {
                Image(
                    painter = rememberAsyncImagePainter(Uri.decode(photoUri)),
                    contentDescription = "Foto ampliada",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (scale == 1f) {
                                        scale = 2.5f
                                        showZoomIndicator = true
                                    } else {
                                        scale = 1f
                                        offset = Offset.Zero
                                        showZoomIndicator = true
                                    }

                                    scope.launch {
                                        delay(900)
                                        showZoomIndicator = false
                                    }
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)

                                offset = if (scale > 1f) {
                                    offset + pan
                                } else {
                                    Offset.Zero
                                }

                                showZoomIndicator = true

                                scope.launch {
                                    delay(900)
                                    showZoomIndicator = false
                                }
                            }
                        }
                )
            } else {
                PostcardBack(
                    text = postcardText,
                    onTextChange = {
                        postcardText = it
                    }
                )
            }
        }

        IconButton(
            onClick = {
                if (showPostcardBack) {
                    if (postcardText.isNotBlank()) {
                        onSavePostcardNote(postcardText)
                    }
                }
                showPostcardBack = !showPostcardBack
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    color = DiveDeepBlue.copy(alpha = 0.82f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = if (showPostcardBack) {
                    Icons.Default.Image
                } else {
                    Icons.Default.Article
                },
                contentDescription = if (showPostcardBack) {
                    "Ver imagen"
                } else {
                    "Ver reverso postal"
                },
                tint = DiveFoam
            )
        }

        IconButton(
            onClick = {
                if (postcardText.isNotBlank()) {
                    onSavePostcardNote(postcardText)
                }
                onBackClick()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    color = DiveDeepBlue.copy(alpha = 0.82f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar visor",
                tint = DiveFoam
            )
        }

        AnimatedVisibility(
            visible = showZoomIndicator,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            AssistChip(
                onClick = {},
                label = {
                    Text("Zoom ${String.format("%.1f", scale)}x")
                },
                shape = RoundedCornerShape(50),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = DiveDeepBlue.copy(alpha = 0.88f),
                    labelColor = DiveFoam
                )
            )
        }
    }
}

@Composable
private fun PostcardBack(
    text: String,
    onTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = DiveDeepBlue.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Reverso de postal",
                color = DiveFoam,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Escribe un recuerdo asociado a esta foto o dibujo.",
                color = DiveFoam.copy(alpha = 0.75f)
            )

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                label = {
                    Text("Recuerdo")
                },
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DiveFoam,
                    unfocusedTextColor = DiveFoam,
                    focusedBorderColor = DiveTeal,
                    unfocusedBorderColor = DiveFoam.copy(alpha = 0.45f),
                    focusedLabelColor = DiveTeal,
                    unfocusedLabelColor = DiveFoam.copy(alpha = 0.75f),
                    cursorColor = DiveTeal
                )
            )
        }
    }
}