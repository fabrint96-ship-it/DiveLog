package com.example.divelog.ui.components.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.divelog.ui.theme.DiveFoam
import com.example.divelog.ui.theme.DiveTeal

@Composable
fun DiveFAB(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = text
            )
        },
        text = {
            Text(
                text = text,
                fontWeight = FontWeight.Bold
            )
        },
        containerColor = DiveTeal,
        contentColor = DiveFoam,
        shape = RoundedCornerShape(20.dp)
    )
}