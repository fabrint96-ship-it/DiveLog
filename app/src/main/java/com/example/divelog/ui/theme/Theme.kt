package com.example.divelog.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DiveLogColorScheme = darkColorScheme(
    primary = DiveTeal,
    onPrimary = DiveFoam,

    secondary = DiveSand,
    onSecondary = DiveDeepBlue,

    background = DiveBackground,
    onBackground = DiveFoam,

    surface = DiveSurface,
    onSurface = DiveFoam,

    surfaceVariant = DiveSurfaceVariant,
    onSurfaceVariant = DiveFoam.copy(alpha = 0.82f),

    error = DiveError,
    onError = DiveFoam,

    errorContainer = DiveError.copy(alpha = 0.22f),
    onErrorContainer = DiveFoam
)

@Composable
fun DiveLogTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DiveLogColorScheme,
        typography = Typography,
        content = content
    )
}