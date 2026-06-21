package com.deymervilla.ds.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = WalmartBlue,
    secondary = WalmartYellow,
    background = Background,
    surface = Surface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = WalmartBlue,
    secondary = WalmartYellow,
    background = OnSurface,
    surface = OnSurface,
    onSurface = Surface,
    onSurfaceVariant = Divider,
    error = ErrorRed
)

@Composable
fun GAPSIStoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}