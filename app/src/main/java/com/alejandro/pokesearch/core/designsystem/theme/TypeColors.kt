package com.alejandro.pokesearch.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Extended color token for a Pokémon type.
 *
 * [base] is only meant for dots and tints, never for text.
 */
@Immutable
data class TypeColors(
    val base: Color,
    val container: Color,
    val onContainer: Color,
)

/** Type color palette for one theme.*/
@Immutable
class PokeTypeColorScheme internal constructor(
    private val byName: Map<String, TypeColors>,
    private val fallback: TypeColors,
    private val surface: Color,
    private val tintFraction: Float,
) {
    operator fun get(typeName: String): TypeColors = byName[typeName.lowercase()] ?: fallback

    /** Tinted background used behind the Detail artwork. */
    fun detailTint(typeName: String): Color = mixSrgb(get(typeName).base, surface, tintFraction)

    internal companion object {
        private val baseColors = mapOf(
            "normal" to Color(0xFFA8A77A),
            "fire" to Color(0xFFEE8130),
            "water" to Color(0xFF6390F0),
            "grass" to Color(0xFF7AC74C),
            "electric" to Color(0xFFF7D02C),
            "ice" to Color(0xFF96D9D6),
            "fighting" to Color(0xFFC22E28),
            "poison" to Color(0xFFA33EA1),
            "ground" to Color(0xFFE2BF65),
            "flying" to Color(0xFFA98FF3),
            "psychic" to Color(0xFFF95587),
            "bug" to Color(0xFFA6B91A),
            "rock" to Color(0xFFB6A136),
            "ghost" to Color(0xFF735797),
            "dragon" to Color(0xFF6F35FC),
            "dark" to Color(0xFF705746),
            "steel" to Color(0xFFB7B7CE),
            "fairy" to Color(0xFFD685AD),
        )

        fun light(surface: Color): PokeTypeColorScheme = build(surface, darkTheme = false)

        fun dark(surface: Color): PokeTypeColorScheme = build(surface, darkTheme = true)

        private fun build(surface: Color, darkTheme: Boolean): PokeTypeColorScheme {
            val resolve = { base: Color -> typeColorsFor(base, surface, darkTheme) }
            return PokeTypeColorScheme(
                byName = baseColors.mapValues { (_, base) -> resolve(base) },
                fallback = resolve(baseColors.getValue("normal")),
                surface = surface,
                tintFraction = if (darkTheme) 0.22f else 0.38f,
            )
        }

        private fun typeColorsFor(base: Color, surface: Color, darkTheme: Boolean): TypeColors =
            if (darkTheme) {
                TypeColors(
                    base = base,
                    container = mixSrgb(base, surface, 0.34f),
                    onContainer = mixSrgb(base, Color.White, 0.40f),
                )
            } else {
                TypeColors(
                    base = base,
                    container = mixSrgb(base, Color.White, 0.22f),
                    onContainer = mixSrgb(base, Color.Black, 0.48f),
                )
            }
    }
}

/**
 * Mixes [color] into [other] in sRGB space, like CSS `color-mix(in srgb, color f, other)`.
 * Compose's `lerp` works in Oklab, which would not match the design tokens.
 */
internal fun mixSrgb(color: Color, other: Color, fraction: Float): Color = Color(
    red = color.red * fraction + other.red * (1 - fraction),
    green = color.green * fraction + other.green * (1 - fraction),
    blue = color.blue * fraction + other.blue * (1 - fraction),
    alpha = 1f,
)
