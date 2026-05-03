package com.example.divelog.ui.screens.divelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.divelog.data.model.Dive
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect

enum class DiveSortOption(
    val label: String
) {
    NEWEST("Más recientes"),
    OLDEST("Más antiguas"),
    DEEPEST("Mayor profundidad"),
    SHALLOWEST("Menor profundidad"),
    LONGEST("Mayor duración"),
    SHORTEST("Menor duración")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiveListScreen(
    dives: List<Dive>,
    snackbarMessage: String?,
    onSnackbarShown: () -> Unit,
    onAddDiveClick: () -> Unit,
    onDiveClick: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        if (!snackbarMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onSnackbarShown()
        }
    }
    var searchText by remember { mutableStateOf("") }
    var expandedSortMenu by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf(DiveSortOption.NEWEST) }

    val filteredDives = dives.filter { dive ->
        dive.title.contains(searchText, ignoreCase = true) ||
                dive.location.contains(searchText, ignoreCase = true) ||
                dive.date.contains(searchText, ignoreCase = true)
    }

    val sortedDives = when (selectedSortOption) {
        DiveSortOption.NEWEST -> filteredDives.sortedByDescending {
            parseDiveDate(it.date) ?: LocalDate.MIN
        }

        DiveSortOption.OLDEST -> filteredDives.sortedBy {
            parseDiveDate(it.date) ?: LocalDate.MAX
        }
        DiveSortOption.DEEPEST -> filteredDives.sortedByDescending {
            it.maxDepth.filter { char -> char.isDigit() || char == '.' }.toDoubleOrNull() ?: 0.0
        }
        DiveSortOption.SHALLOWEST -> filteredDives.sortedBy {
            it.maxDepth.filter { char -> char.isDigit() || char == '.' }.toDoubleOrNull() ?: 0.0
        }
        DiveSortOption.LONGEST -> filteredDives.sortedByDescending {
            it.duration.filter { char -> char.isDigit() || char == '.' }.toDoubleOrNull() ?: 0.0
        }
        DiveSortOption.SHORTEST -> filteredDives.sortedBy {
            it.duration.filter { char -> char.isDigit() || char == '.' }.toDoubleOrNull() ?: 0.0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DiveLog",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bitácora de submarinismo",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDiveClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir inmersión"
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Mis inmersiones",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Registra tus aventuras bajo el agua.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Buscar inmersión") },
                        placeholder = { Text("Título, lugar o fecha") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,

                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar"
                            )
                        },

                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpiar búsqueda"
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                expandedSortMenu = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ordenar: ${selectedSortOption.label}")
                        }

                        DropdownMenu(
                            expanded = expandedSortMenu,
                            onDismissRequest = {
                                expandedSortMenu = false
                            }
                        ) {
                            DiveSortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(option.label)
                                    },
                                    onClick = {
                                        selectedSortOption = option
                                        expandedSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (sortedDives.isEmpty()) {
                    item {
                        EmptyDiveList(
                            onAddDiveClick = onAddDiveClick
                        )
                    }
                } else {
                    items(sortedDives) { dive ->
                        DiveCard(
                            dive = dive,
                            onClick = {
                                onDiveClick(dive.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiveCard(
    dive: Dive,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Water,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dive.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dive.location,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${dive.date} · ${dive.maxDepth} · ${dive.duration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
        }
    }
}

@Composable
fun EmptyDiveList(
    onAddDiveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Water,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Aún no tienes inmersiones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Empieza registrando tu primera aventura submarina.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Button(
                onClick = onAddDiveClick
            ) {
                Text("Añadir primera inmersión")
            }
        }
    }
}

fun parseDiveDate(date: String): LocalDate? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        LocalDate.parse(date, formatter)
    } catch (e: Exception) {
        null
    }
}