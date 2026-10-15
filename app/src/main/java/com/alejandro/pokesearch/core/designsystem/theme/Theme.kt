package com.alejandro.pokesearch.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

val LocalPokeTypeColors = staticCompositionLocalOf { PokeTypeColorScheme.light(PokeLightColorScheme.surface) }

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

@Composable
fun PokeSearchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> PokeDarkColorScheme
        else -> PokeLightColorScheme
    }
    val typeColors = remember(darkTheme, colorScheme.surface) {
        if (darkTheme) {
            PokeTypeColorScheme.dark(colorScheme.surface)
        } else {
            PokeTypeColorScheme.light(colorScheme.surface)
        }
    }
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalPokeTypeColors provides typeColors,
        LocalExtendedColors provides extendedColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(),
            shapes = PokeShapes,
            content = content,
        )
    }
}

object PokeTheme {
    val typeColors: PokeTypeColorScheme
        @Composable @ReadOnlyComposable get() = LocalPokeTypeColors.current

    val extendedColors: ExtendedColors
        @Composable @ReadOnlyComposable get() = LocalExtendedColors.current
}
