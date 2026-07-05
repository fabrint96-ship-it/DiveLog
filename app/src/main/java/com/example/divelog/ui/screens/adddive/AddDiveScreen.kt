package com.example.divelog.ui.screens.adddive

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.divelog.domain.model.Dive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.divelog.ui.components.navigation.DiveEditorHeader
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Brush
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.components.cards.DiveSectionCard
import com.example.divelog.ui.components.textfields.DiveFormTextField
import com.example.divelog.ui.components.textfields.DiveDropdownField
import com.example.divelog.ui.components.buttons.DiveButton
import com.example.divelog.ui.theme.DiveFoam
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.example.divelog.ui.theme.DiveDeepBlue
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.divelog.ui.components.common.DiveValidationErrorCard
import com.example.divelog.ui.theme.DiveAnimations
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LaunchedEffect
import com.example.divelog.ui.components.maps.DiveMiniMapCard
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDiveScreen(
    diveToEdit: Dive? = null,
    pickedLocation: String? = null,
    pickedLatitude: Double? = null,
    pickedLongitude: Double? = null,
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
        photos: List<String>,
        latitude: Double?,
        longitude: Double?
    ) -> Unit,
    onBackClick: () -> Unit,
    onPickLocationClick: () -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(diveToEdit?.title ?: "") }
    var location by rememberSaveable { mutableStateOf(diveToEdit?.location ?: "") }
    var latitude by rememberSaveable { mutableStateOf(diveToEdit?.latitude) }
    var longitude by rememberSaveable { mutableStateOf(diveToEdit?.longitude) }
    LaunchedEffect(pickedLocation, pickedLatitude, pickedLongitude) {
        if (!pickedLocation.isNullOrBlank()) {
            location = pickedLocation
            latitude = pickedLatitude
            longitude = pickedLongitude
        }
    }
    var diveType by rememberSaveable { mutableStateOf(diveToEdit?.diveType ?: "") }
    var date by rememberSaveable { mutableStateOf(diveToEdit?.date ?: "") }
    var depth by rememberSaveable { mutableStateOf(diveToEdit?.maxDepth?.replace(" m", "") ?: "") }
    var duration by rememberSaveable { mutableStateOf(diveToEdit?.duration?.replace(" min", "") ?: "") }
    var temperature by rememberSaveable { mutableStateOf(diveToEdit?.waterTemperature?.replace(" ºC", "") ?: "") }
    var visibility by rememberSaveable { mutableStateOf(diveToEdit?.visibility ?: "") }
    var notes by rememberSaveable { mutableStateOf(diveToEdit?.notes ?: "") }

    var selectedPhotos by remember {
        mutableStateOf<List<Uri>>(
            diveToEdit?.photos?.map { Uri.parse(it) } ?: emptyList()
        )
    }

    var showError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var diveTypeExpanded by remember { mutableStateOf(false) }
    var visibilityExpanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

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

    val visibilityOptions = listOf(
        "Excelente / +20 m",
        "Buena / 10-20 m",
        "Media / 5-10 m",
        "Baja / 2-5 m",
        "Muy baja / <2 m"
    )

    val depthNumber = depth.toDoubleOrNull()
    val durationNumber = duration.toIntOrNull()

    val isDepthValid = depthNumber != null && depthNumber in 0.0..100.0
    val isDurationValid = durationNumber != null && durationNumber > 0
    val isDateValid = date.isNotBlank()

    val validationErrors = buildList {
        if (title.isBlank()) add("El título es obligatorio.")
        if (location.isBlank()) add("El lugar es obligatorio.")
        if (!isDateValid) add("Selecciona una fecha válida.")
        if (!isDepthValid) add("La profundidad debe estar entre 0 y 100 metros.")
        if (!isDurationValid) add("La duración debe ser mayor que 0 minutos.")
    }

    val isFormValid = title.isNotBlank() &&
            location.isNotBlank() &&
            isDateValid &&
            isDepthValid &&
            isDurationValid

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris ->
            selectedPhotos = (selectedPhotos + uris).distinctBy { it.toString() }
        }
    )

    Scaffold(
        containerColor = DiveDeepBlue,
        topBar = {
            DiveEditorHeader(
                isEditMode = diveToEdit != null,
                onBackClick = onBackClick
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
                DatePicker(state = datePickerState)
            }
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
                    Text(
                        text = "Datos de la inmersión",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DiveFoam
                    )

                    Text(
                        text = "Completa la información principal de tu salida bajo el agua.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DiveFoam.copy(alpha = 0.82f)
                    )

                    AnimatedVisibility(
                        visible = true,
                        enter = DiveAnimations.cardEnter()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            DiveSectionCard(
                                title = "Información de la inmersión"
                            ) {
                                DiveFormTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = "Título *",
                                    placeholder = "Ej: Inmersión en Cabo de Palos",
                                    isError = showError && title.isBlank()
                                )

                                DiveFormTextField(
                                    value = location,
                                    onValueChange = { location = it },
                                    label = "Lugar *",
                                    placeholder = "Selecciona o escribe un lugar",
                                    readOnly = false,
                                    isError = showError && location.isBlank(),
                                    trailingIcon = {
                                        IconButton(
                                            onClick = onPickLocationClick
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Place,
                                                contentDescription = "Seleccionar ubicación en mapa",
                                                tint = DiveFoam
                                            )
                                        }
                                    }
                                )

                                if (latitude != null && longitude != null) {
                                    DiveMiniMapCard(
                                        latitude = latitude,
                                        longitude = longitude,
                                        locationName = location,
                                        interactive = false
                                    )
                                }

                                DiveDropdownField(
                                    value = diveType,
                                    label = "Tipo de inmersión",
                                    placeholder = "Selecciona un tipo",
                                    expanded = diveTypeExpanded,
                                    options = diveTypes,
                                    onExpandedChange = {
                                        diveTypeExpanded = !diveTypeExpanded
                                    },
                                    onDismissRequest = {
                                        diveTypeExpanded = false
                                    },
                                    onOptionSelected = { type ->
                                        diveType = type
                                        diveTypeExpanded = false
                                    }
                                )

                                DiveFormTextField(
                                    value = date,
                                    onValueChange = {},
                                    label = "Fecha *",
                                    placeholder = "Selecciona una fecha",
                                    readOnly = true,
                                    isError = showError && !isDateValid,
                                    modifier = Modifier.clickable {
                                        showDatePicker = true
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                showDatePicker = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarMonth,
                                                contentDescription = "Seleccionar fecha",
                                                tint = DiveFoam
                                            )
                                        }
                                    }
                                )
                            }

                            DiveSectionCard(
                                title = "Parámetros de inmersión"
                            ) {
                                DiveFormTextField(
                                    value = depth,
                                    onValueChange = { value ->
                                        depth = value.filter { it.isDigit() || it == '.' }
                                    },
                                    label = "Profundidad máxima *",
                                    placeholder = "Ej: 28",
                                    isError = showError && !isDepthValid,
                                    suffix = { Text("m", color = DiveFoam) },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal
                                    )
                                )

                                DiveFormTextField(
                                    value = duration,
                                    onValueChange = { value ->
                                        duration = value.filter { it.isDigit() }
                                    },
                                    label = "Duración *",
                                    placeholder = "Ej: 45",
                                    isError = showError && !isDurationValid,
                                    suffix = { Text("min", color = DiveFoam) },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    )
                                )

                                DiveFormTextField(
                                    value = temperature,
                                    onValueChange = { value ->
                                        temperature = value.filter { it.isDigit() || it == '.' }
                                    },
                                    label = "Temperatura del agua",
                                    placeholder = "Ej: 21",
                                    suffix = { Text("ºC", color = DiveFoam) },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal
                                    )
                                )

                                DiveDropdownField(
                                    value = visibility,
                                    label = "Visibilidad",
                                    placeholder = "Selecciona la visibilidad",
                                    expanded = visibilityExpanded,
                                    options = visibilityOptions,
                                    onExpandedChange = {
                                        visibilityExpanded = !visibilityExpanded
                                    },
                                    onDismissRequest = {
                                        visibilityExpanded = false
                                    },
                                    onOptionSelected = { option ->
                                        visibility = option
                                        visibilityExpanded = false
                                    }
                                )
                            }

                            DiveSectionCard(
                                title = "Notas"
                            ) {
                                DiveFormTextField(
                                    value = notes,
                                    onValueChange = { notes = it },
                                    label = "Notas",
                                    placeholder = "Escribe recuerdos, fauna vista o sensaciones...",
                                    singleLine = false,
                                    modifier = Modifier.height(140.dp)
                                )
                            }
                        }
                    }

                DiveSectionCard(
                    title = "Fotos de la inmersión"
                ) {
                    DiveButton(
                        text = "Añadir fotos",
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    if (selectedPhotos.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = selectedPhotos,
                                key = { it.toString() }
                            ) { uri ->
                                Box(
                                    modifier = Modifier.size(104.dp)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = "Foto seleccionada",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(104.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                    )

                                    IconButton(
                                        onClick = {
                                            selectedPhotos = selectedPhotos.filterNot { it == uri }
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
                                            contentDescription = "Eliminar foto",
                                            tint = DiveFoam,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Aún no has añadido fotos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DiveFoam.copy(alpha = 0.75f)
                        )
                    }
                }

                    Spacer(modifier = Modifier.height(8.dp))

                if (showError && !isFormValid) {
                    DiveValidationErrorCard(
                        errors = validationErrors
                    )
                }

                DiveButton(
                    text = if (diveToEdit == null) {
                        "Guardar inmersión"
                    } else {
                        "Actualizar inmersión"
                    },
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
                                selectedPhotos.map { it.toString() },
                                latitude,
                                longitude
                            )
                        } else {
                            showError = true
                        }
                    }
                )
                }
            }
    }
}