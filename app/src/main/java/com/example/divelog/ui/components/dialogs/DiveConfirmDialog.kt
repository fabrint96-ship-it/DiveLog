package com.example.divelog.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.divelog.ui.components.buttons.DiveButton
import com.example.divelog.ui.components.buttons.DiveOutlinedButton
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam

@Composable
fun DiveConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DiveDeepBlue,
        titleContentColor = DiveFoam,
        textContentColor = DiveFoam.copy(alpha = 0.85f),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            DiveButton(
                text = confirmText,
                onClick = onConfirm
            )
        },
        dismissButton = {
            DiveOutlinedButton(
                text = cancelText,
                onClick = onDismiss
            )
        }
    )
}