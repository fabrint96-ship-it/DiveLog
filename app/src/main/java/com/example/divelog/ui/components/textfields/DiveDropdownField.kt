package com.example.divelog.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.divelog.ui.theme.DiveDeepBlue
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveOceanBlue
import com.example.divelog.ui.theme.DiveTeal
import com.example.divelog.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiveDropdownField(
    value: String,
    label: String,
    placeholder: String,
    expanded: Boolean,
    options: List<String>,
    onExpandedChange: () -> Unit,
    onDismissRequest: () -> Unit,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange()
        },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            shape = RoundedCornerShape(Dimens.RadiusM),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DiveFoam,
                unfocusedTextColor = DiveFoam,
                focusedBorderColor = DiveTeal,
                unfocusedBorderColor = DiveFoam.copy(alpha = 0.45f),
                focusedLabelColor = DiveTeal,
                unfocusedLabelColor = DiveFoam.copy(alpha = 0.75f),
                focusedPlaceholderColor = DiveFoam.copy(alpha = 0.55f),
                unfocusedPlaceholderColor = DiveFoam.copy(alpha = 0.45f),
                focusedTrailingIconColor = DiveTeal,
                unfocusedTrailingIconColor = DiveFoam.copy(alpha = 0.75f)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            containerColor = DiveDeepBlue
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = DiveFoam
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}