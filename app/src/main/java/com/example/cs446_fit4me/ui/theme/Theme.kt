package com.example.cs446_fit4me.ui.theme

import android.app.Activity
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
import com.example.cs446_fit4me.ui.theme.AppTypography

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val LightColorScheme2 = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    secondary = OrangeSecondary,
    onSecondary = Color.Black,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimary,
    error = ErrorRed
)

private val DarkColorScheme2 = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = Color.White,
    secondary = OrangeSecondary,
    onSecondary = Color.White,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    error = ErrorRed
)

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    secondary = CoralAccent,
    tertiary = CoralAccent, // Or another accent
    background = LightGreyBackground,
    surface = White, // For cards, sheets
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = DarkBackground
)

@Composable
fun CS446fit4meTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}