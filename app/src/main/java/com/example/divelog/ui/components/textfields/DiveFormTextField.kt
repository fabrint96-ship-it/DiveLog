package com.example.divelog.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveTeal

@Composable
fun DiveFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    isError: Boolean = false,
    suffix: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(placeholder)
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        readOnly = readOnly,
        isError = isError,
        suffix = suffix,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = DiveFoam,
            unfocusedTextColor = DiveFoam,
            focusedBorderColor = DiveTeal,
            unfocusedBorderColor = DiveFoam.copy(alpha = 0.45f),
            focusedLabelColor = DiveTeal,
            unfocusedLabelColor = DiveFoam.copy(alpha = 0.75f),
            focusedPlaceholderColor = DiveFoam.copy(alpha = 0.55f),
            unfocusedPlaceholderColor = DiveFoam.copy(alpha = 0.45f),
            cursorColor = DiveTeal,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error
        )
    )
}