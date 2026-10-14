package com.alejandro.pokesearch.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Spacing scale: multiples of 4 dp. */
object Spacing {
    /** Title/subtitle gap in list items. */
    val xxs = 4.dp

    /** Gap between chips and vertical padding of chip rows. */
    val xs = 8.dp

    /** Icon-text gap, snackbar margin, card vertical padding. */
    val sm = 12.dp

    /** Horizontal screen and list item padding. */
    val md = 16.dp

    /** Gap between Detail sections. */
    val lg = 20.dp

    /** Padding of Login, Detail and dialogs. */
    val xl = 24.dp

    /** Gap between Login blocks. */
    val xxl = 32.dp

    /** Minimum touch target. */
    val touchTarget = 48.dp
}

internal val PokeShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
