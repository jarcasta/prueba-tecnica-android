package com.alejandro.pokesearch.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** Brand scheme generated*/
internal val PokeLightColorScheme = lightColorScheme(
    primary = Color(0xFFB22B0B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDAD2),
    onPrimaryContainer = Color(0xFF3E0500),
    secondary = Color(0xFF6F5B56),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF5DED8),
    onSecondaryContainer = Color(0xFF281814),
    tertiary = Color(0xFF6D5E00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF9E287),
    onTertiaryContainer = Color(0xFF211B00),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFF8F6),
    onBackground = Color(0xFF231917),
    surface = Color(0xFFFFF8F6),
    onSurface = Color(0xFF231917),
    surfaceVariant = Color(0xFFF5DDD8),
    onSurfaceVariant = Color(0xFF534340),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF0ED),
    surfaceContainer = Color(0xFFFCEAE6),
    surfaceContainerHigh = Color(0xFFF6E4E0),
    surfaceContainerHighest = Color(0xFFF1DFDA),
    surfaceDim = Color(0xFFE8D6D2),
    surfaceBright = Color(0xFFFFF8F6),
    outline = Color(0xFF857370),
    outlineVariant = Color(0xFFD8C2BD),
    inverseSurface = Color(0xFF392E2B),
    inverseOnSurface = Color(0xFFFFEDE9),
    inversePrimary = Color(0xFFFFB4A2),
    scrim = Color(0xFF000000),
)

internal val PokeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4A2),
    onPrimary = Color(0xFF631100),
    primaryContainer = Color(0xFF8C1D00),
    onPrimaryContainer = Color(0xFFFFDAD2),
    secondary = Color(0xFFDCC1BB),
    onSecondary = Color(0xFF3E2D29),
    secondaryContainer = Color(0xFF56433F),
    onSecondaryContainer = Color(0xFFF9DDD7),
    tertiary = Color(0xFFDCC66E),
    onTertiary = Color(0xFF3A3000),
    tertiaryContainer = Color(0xFF534600),
    onTertiaryContainer = Color(0xFFF9E287),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A110F),
    onBackground = Color(0xFFF1DFDA),
    surface = Color(0xFF1A110F),
    onSurface = Color(0xFFF1DFDA),
    surfaceVariant = Color(0xFF534340),
    onSurfaceVariant = Color(0xFFD8C2BD),
    surfaceContainerLowest = Color(0xFF140C0A),
    surfaceContainerLow = Color(0xFF231917),
    surfaceContainer = Color(0xFF271D1B),
    surfaceContainerHigh = Color(0xFF322825),
    surfaceContainerHighest = Color(0xFF3D3230),
    surfaceDim = Color(0xFF1A110F),
    surfaceBright = Color(0xFF423734),
    outline = Color(0xFFA08C88),
    outlineVariant = Color(0xFF534340),
    inverseSurface = Color(0xFFF1DFDA),
    inverseOnSurface = Color(0xFF392E2B),
    inversePrimary = Color(0xFFB22B0B),
    scrim = Color(0xFF000000),
)

data class ExtendedColors(
    val success: Color,
)

internal val LightExtendedColors = ExtendedColors(success = Color(0xFF386A20))
internal val DarkExtendedColors = ExtendedColors(success = Color(0xFF9CD67D))
