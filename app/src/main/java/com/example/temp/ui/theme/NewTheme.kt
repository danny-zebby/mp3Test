package com.example.temp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.compose.*

private val lightScheme = lightColorScheme(
    primary = primaryBCLight,
    onPrimary = tertiaryBGLight, // White
    secondary = secondaryBCLight,
    onSecondary = tertiaryBGLight, // White
    tertiary = tertiaryBCLight,
    onTertiary = secondaryBGLight,
    background = primaryBGLight,
    onBackground = secondaryBGLight,
    surface = primaryBGLight,
    onSurface = secondaryBGLight,
    // Using the same 6 colors to fill other required slots
    surfaceVariant = tertiaryBCLight,
    onSurfaceVariant = secondaryBGLight,
    outline = secondaryBCLight,
    primaryContainer =  Color(0xFF196D8A),   // This controls the SELECTED background
    onPrimaryContainer = Color.White,
)

private val darkScheme = darkColorScheme(
    primary = primaryBCDark,
    onPrimary = tertiaryBGDark, // Black
    secondary = secondaryBCDark,
    onSecondary = tertiaryBGDark, // Black
    tertiary = tertiaryBCDark,
    onTertiary = secondaryBGDark,
    background = primaryBGDark,
    onBackground = secondaryBGDark,
    surface = primaryBGDark,
    onSurface = secondaryBGDark,
    // Using the same 6 colors to fill other required slots
    surfaceVariant = tertiaryBCDark,
    onSurfaceVariant = secondaryBGDark,
    outline = secondaryBCDark
)

@Composable
fun NewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to prioritize your 6 colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
