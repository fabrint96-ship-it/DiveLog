package com.example.divelog.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

@Composable
fun DiveHomeHeader(
    isSyncing: Boolean,
    isSearchMode: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCloseSearchClick: () -> Unit,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onLogoutClick: () -> Unit
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
                start = 20.dp,
                end = 20.dp,
                top = 28.dp,
                bottom = 20.dp
            )
    ) {
        if (isSearchMode) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    Text("Buscar inmersión...")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = DiveFoam
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onCloseSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar búsqueda",
                            tint = DiveFoam
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DiveFoam,
                    unfocusedTextColor = DiveFoam,
                    focusedBorderColor = DiveFoam,
                    unfocusedBorderColor = DiveFoam.copy(alpha = 0.55f),
                    cursorColor = DiveFoam,
                    focusedPlaceholderColor = DiveFoam.copy(alpha = 0.75f),
                    unfocusedPlaceholderColor = DiveFoam.copy(alpha = 0.75f),
                    focusedLeadingIconColor = DiveFoam,
                    unfocusedLeadingIconColor = DiveFoam,
                    focusedTrailingIconColor = DiveFoam,
                    unfocusedTrailingIconColor = DiveFoam
                )
            )

            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DiveLog",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DiveFoam
                )

                Text(
                    text = if (isSyncing) {
                        "Sincronizando tus inmersiones..."
                    } else {
                        "Tu bitácora submarina"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = DiveFoam.copy(alpha = 0.82f)
                )
            }

            Row {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar inmersiones",
                        tint = DiveFoam
                    )
                }

                IconButton(
                    onClick = onBackupClick,
                    enabled = !isSyncing
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Subir backup a la nube",
                        tint = DiveFoam
                    )
                }

                IconButton(
                    onClick = onRestoreClick,
                    enabled = !isSyncing
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = "Restaurar backup desde la nube",
                        tint = DiveFoam
                    )
                }

                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = DiveFoam
                    )
                }
            }
        }
    }
}