package com.example.divelog.ui.components.textfields

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveTeal

@Composable
fun DivePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Contraseña"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    imageVector = if (passwordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = if (passwordVisible) {
                        "Ocultar contraseña"
                    } else {
                        "Mostrar contraseña"
                    }
                )
            }
        },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = DiveTeal,
            unfocusedBorderColor = DiveFoam.copy(alpha = 0.45f),
            focusedLabelColor = DiveTeal,
            unfocusedLabelColor = DiveFoam.copy(alpha = 0.75f),
            cursorColor = DiveTeal,
            focusedLeadingIconColor = DiveTeal,
            unfocusedLeadingIconColor = DiveFoam.copy(alpha = 0.75f),
            focusedTrailingIconColor = DiveTeal,
            unfocusedTrailingIconColor = DiveFoam.copy(alpha = 0.75f)
        )
    )
}