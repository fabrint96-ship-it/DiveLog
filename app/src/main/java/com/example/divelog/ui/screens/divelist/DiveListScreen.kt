package com.example.divelog.ui.screens.divelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.divelog.domain.model.Dive
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.divelog.ui.components.navigation.DiveHomeHeader
import com.example.divelog.ui.components.cards.DiveCard
import androidx.compose.material3.ExtendedFloatingActionButton
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.ui.theme.DiveFoam
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.FilterChip
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.FilterChipDefaults
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import androidx.compose.ui.graphics.Brush
import com.example.divelog.ui.components.buttons.DiveButton
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.ui.components.loading.DiveLoading
import com.example.divelog.ui.components.common.DiveErrorState
import com.example.divelog.ui.components.buttons.DiveFAB
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveAnimations

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
    isLoading: Boolean,
    errorMessage: String?,
    isSyncing: Boolean,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: String?,
    onSnackbarShown: () -> Unit,
    onAddDiveClick: () -> Unit,
    onDiveClick: (Int) -> Unit,
    onLogoutClick: () -> Unit,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    LaunchedEffect(snackbarMessage) {
        if (!snackbarMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onSnackbarShown()
        }
    }

    var searchText by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) }
    val sortSheetState = rememberModalBottomSheetState()
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
            it.maxDepth.filter { char -> char.isDigit() || char == '.' }
                .toDoubleOrNull() ?: 0.0
        }

        DiveSortOption.SHALLOWEST -> filteredDives.sortedBy {
            it.maxDepth.filter { char -> char.isDigit() || char == '.' }
                .toDoubleOrNull() ?: 0.0
        }

        DiveSortOption.LONGEST -> filteredDives.sortedByDescending {
            it.duration.filter { char -> char.isDigit() || char == '.' }
                .toDoubleOrNull() ?: 0.0
        }

        DiveSortOption.SHORTEST -> filteredDives.sortedBy {
            it.duration.filter { char -> char.isDigit() || char == '.' }
                .toDoubleOrNull() ?: 0.0
        }
    }

    var isSearchMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DiveHomeHeader(
                isSyncing = isSyncing,
                isSearchMode = isSearchMode,
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onSearchClick = {
                    isSearchMode = true
                },
                onCloseSearchClick = {
                    isSearchMode = false
                    searchText = ""
                },
                onBackupClick = onBackupClick,
                onRestoreClick = onRestoreClick,
                onLogoutClick = onLogoutClick
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            DiveFAB(
                text = "Nueva inmersión",
                onClick = onAddDiveClick
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
            when {
                isLoading -> {
                    DiveLoading(
                        text = "Cargando tus inmersiones..."
                    )
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        DiveErrorState(
                            message = errorMessage,
                            onActionClick = onRestoreClick
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Mis inmersiones",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = DiveFoam
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Registra tus aventuras bajo el agua.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DiveFoam.copy(alpha = 0.82f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FilterChip(
                                    selected = true,
                                    onClick = { showSortSheet = true },
                                    label = {
                                        Text(
                                            text = "Ordenar: ${selectedSortOption.label}",
                                            color = DiveFoam
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Sort,
                                            contentDescription = null,
                                            tint = DiveFoam
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DiveOceanBlue,
                                        selectedLabelColor = DiveFoam,
                                        selectedLeadingIconColor = DiveFoam
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = true,
                                        borderColor = DiveTeal,
                                        selectedBorderColor = DiveTeal
                                    )
                                )
                            }
                        }

                        if (sortedDives.isEmpty()) {
                            item {
                                EmptyDiveList(
                                    onAddDiveClick = onAddDiveClick
                                )
                            }
                        } else {
                            items(
                                items = sortedDives,
                                key = { dive -> dive.id }
                            ) { dive ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = DiveAnimations.cardEnter(),
                                    exit = DiveAnimations.cardExit()
                                ) {
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

            if (showSortSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSortSheet = false },
                    sheetState = sortSheetState,
                    containerColor = DiveDeepBlue
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Ordenar inmersiones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DiveFoam
                        )

                        DiveSortOption.entries.forEach { option ->
                            FilterChip(
                                selected = selectedSortOption == option,
                                onClick = {
                                    selectedSortOption = option
                                    showSortSheet = false
                                },
                                label = {
                                    Text(option.label)
                                },
                                leadingIcon = {
                                    if (selectedSortOption == option) {
                                        Icon(
                                            imageVector = Icons.Default.Sort,
                                            contentDescription = null
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = DiveOceanBlue.copy(alpha = 0.45f),
                                    labelColor = DiveFoam,
                                    iconColor = DiveFoam,
                                    selectedContainerColor = DiveTeal,
                                    selectedLabelColor = DiveFoam,
                                    selectedLeadingIconColor = DiveFoam
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedSortOption == option,
                                    borderColor = DiveFoam.copy(alpha = 0.25f),
                                    selectedBorderColor = DiveFoam
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
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
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = DiveDeepBlue.copy(alpha = 0.92f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DiveDeepBlue,
                            DiveOceanBlue,
                            DiveTeal
                        )
                    )
                )
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Water,
                    contentDescription = null,
                    tint = DiveFoam,
                    modifier = Modifier.size(56.dp)
                )

                Text(
                    text = "Aún no tienes inmersiones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DiveFoam
                )

                Text(
                    text = "Registra tu primera aventura bajo el agua y empieza a construir tu bitácora submarina.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DiveFoam.copy(alpha = 0.82f)
                )

                DiveButton(
                    text = "Añadir primera inmersión",
                    onClick = onAddDiveClick
                )
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