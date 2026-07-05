package com.example.divelog.ui.screens.locationpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.divelog.ui.components.buttons.DiveButton
import com.example.divelog.ui.components.maps.MapLibreView
import com.example.divelog.ui.components.navigation.DiveEditorHeader
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal

@Composable
fun LocationPickerScreen(
    onBackClick: () -> Unit,
    onLocationSelected: (Double, Double) -> Unit
) {
    var selectedLat by remember { mutableDoubleStateOf(40.4168) }
    var selectedLng by remember { mutableDoubleStateOf(-3.7038) }

    Scaffold(
        containerColor = DiveDeepBlue,
        topBar = {
            DiveEditorHeader(
                isEditMode = true,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

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
                .padding(paddingValues)
        ) {
            MapLibreView(
                modifier = Modifier.fillMaxSize(),
                latitude = selectedLat,
                longitude = selectedLng,
                zoom = 5.0,
                onCameraIdle = { lat, lng ->
                    selectedLat = lat
                    selectedLng = lng
                }
            )

            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Punto seleccionado",
                tint = DiveFoam,
                modifier = Modifier.align(Alignment.Center)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            ) {
                DiveButton(
                    text = "Usar esta ubicación",
                    onClick = {
                        onLocationSelected(selectedLat, selectedLng)
                    }
                )
            }
        }
    }
}