package com.example.divelog.ui.screens.adddive

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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import com.example.divelog.domain.model.Dive
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDiveScreen(
    diveToEdit: Dive? = null,
    onSaveDive: (
        id: Int,
        title: String,
        location: String,
        diveType: String,
        date: String,
        depth: String,
        duration: String,
        temperature: String,
        visibility: String,
        notes: String,
        photos: List<String>
    ) -> Unit,
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf(diveToEdit?.title ?: "") }
    var location by remember { mutableStateOf(diveToEdit?.location ?: "") }
    var diveType by remember { mutableStateOf(diveToEdit?.diveType ?: "") }
    var date by remember { mutableStateOf(diveToEdit?.date ?: "") }
    var depth by remember { mutableStateOf(diveToEdit?.maxDepth?.replace(" m", "") ?: "") }
    var duration by remember { mutableStateOf(diveToEdit?.duration?.replace(" min", "") ?: "") }
    var temperature by remember { mutableStateOf(diveToEdit?.waterTemperature?.replace(" ºC", "") ?: "") }
    var visibility by remember { mutableStateOf(diveToEdit?.visibility ?: "") }
    var visibilityExpanded by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf(diveToEdit?.notes ?: "") }
    var selectedPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }


    val visibilityOptions = listOf(
        "Excelente / +20 m",
        "Buena / 10-20 m",
        "Media / 5-10 m",
        "Baja / 2-5 m",
        "Muy baja / <2 m"
    )

    var showError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    val depthNumber = depth.toDoubleOrNull()
    val durationNumber = duration.toIntOrNull()

    val isDepthValid = depthNumber != null && depthNumber in 0.0..100.0
    val isDurationValid = durationNumber != null && durationNumber > 0
    val isDateValid = date.isNotBlank()

    val isFormValid = title.isNotBlank() &&
            location.isNotBlank() &&
            isDateValid &&
            isDepthValid &&
            isDurationValid

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris ->
            selectedPhotos = uris
        }
    )

    var diveTypeExpanded by remember { mutableStateOf(false) }

    val diveTypes = listOf(
        "Costa",
        "Barco",
        "Nocturna",
        "Cueva",
        "Pecio",
        "Profunda",
        "Fotografía",
        "Entrenamiento"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nueva inmersión",
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

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedMillis = datePickerState.selectedDateMillis

                            if (selectedMillis != null) {
                                date = dateFormatter.format(Date(selectedMillis))
                            }

                            showDatePicker = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Datos de la inmersión",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Completa la información principal de tu salida bajo el agua.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { it / 4 }
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título *") },
                            placeholder = { Text("Ej: Inmersión en Cabo de Palos") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = showError && title.isBlank()
                        )

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Lugar *") },
                            placeholder = { Text("Ej: Murcia, España") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = showError && location.isBlank()
                        )

                        ExposedDropdownMenuBox(
                            expanded = diveTypeExpanded,
                            onExpandedChange = {
                                diveTypeExpanded = !diveTypeExpanded
                            }
                        ) {
                            OutlinedTextField(
                                value = diveType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tipo de inmersión") },
                                placeholder = { Text("Selecciona un tipo") },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = diveTypeExpanded
                                    )
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = diveTypeExpanded,
                                onDismissRequest = {
                                    diveTypeExpanded = false
                                }
                            ) {
                                diveTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(type)
                                        },
                                        onClick = {
                                            diveType = type
                                            diveTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            label = { Text("Fecha *") },
                            placeholder = { Text("Selecciona una fecha") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDatePicker = true
                                },
                            singleLine = true,
                            readOnly = true,
                            isError = showError && !isDateValid,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        showDatePicker = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Seleccionar fecha"
                                    )
                                }
                            }
                        )

                        OutlinedTextField(
                            value = depth,
                            onValueChange = { value ->
                                depth = value.filter { it.isDigit() || it == '.' }
                            },
                            label = { Text("Profundidad máxima *") },
                            placeholder = { Text("Ej: 28") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = showError && !isDepthValid,
                            suffix = { Text("m") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )

                        OutlinedTextField(
                            value = duration,
                            onValueChange = { value ->
                                duration = value.filter { it.isDigit() }
                            },
                            label = { Text("Duración *") },
                            placeholder = { Text("Ej: 45") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = showError && !isDurationValid,
                            suffix = { Text("min") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )

                        OutlinedTextField(
                            value = temperature,
                            onValueChange = { temperature = it },
                            label = { Text("Temperatura del agua") },
                            placeholder = { Text("Ej: 21") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            suffix = { Text("ºC") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )

                        ExposedDropdownMenuBox(
                            expanded = visibilityExpanded,
                            onExpandedChange = {
                                visibilityExpanded = !visibilityExpanded
                            }
                        ) {
                            OutlinedTextField(
                                value = visibility,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Visibilidad") },
                                placeholder = { Text("Selecciona la visibilidad") },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = visibilityExpanded
                                    )
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = visibilityExpanded,
                                onDismissRequest = {
                                    visibilityExpanded = false
                                }
                            ) {
                                visibilityOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(option)
                                        },
                                        onClick = {
                                            visibility = option
                                            visibilityExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notas") },
                            placeholder = { Text("Escribe aquí tus recuerdos, fauna vista, sensaciones...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            maxLines = 6
                        )
                    }
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

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Seleccionar fotos")
                    }

                    if (selectedPhotos.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(selectedPhotos) { uri ->
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "Foto seleccionada",
                                    modifier = Modifier.size(90.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Aún no has añadido fotos.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (showError && !isFormValid) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (title.isBlank()) {
                        Text("El título es obligatorio.", color = MaterialTheme.colorScheme.error)
                    }

                    if (location.isBlank()) {
                        Text("El lugar es obligatorio.", color = MaterialTheme.colorScheme.error)
                    }

                    if (!isDateValid) {
                        Text("Selecciona una fecha válida.", color = MaterialTheme.colorScheme.error)
                    }

                    if (!isDepthValid) {
                        Text("La profundidad debe estar entre 0 y 100 metros.", color = MaterialTheme.colorScheme.error)
                    }

                    if (!isDurationValid) {
                        Text("La duración debe ser mayor que 0 minutos.", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Button(
                onClick = {
                    if (isFormValid) {
                        onSaveDive(
                            diveToEdit?.id ?: 0,
                            title,
                            location,
                            diveType,
                            date,
                            "$depth m",
                            "$duration min",
                            if (temperature.isBlank()) "" else "$temperature ºC",
                            visibility,
                            notes,
                            selectedPhotos.map { it.toString() }
                        )
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Guardar"
                )

                Spacer(modifier = Modifier.padding(4.dp))

                Text(text = "Guardar inmersión")
            }
        }
    }
}