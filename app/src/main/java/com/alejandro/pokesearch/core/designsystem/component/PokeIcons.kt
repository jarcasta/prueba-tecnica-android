package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.ui.graphics.vector.ImageVector

/** Iconos a mostrar según lo devuelto por el Api de PokeAPI. */
object PokeIcons {
    fun pocket(pocketName: String): ImageVector = when (pocketName.lowercase()) {
        "medicine" -> Icons.Rounded.Medication
        "pokeballs" -> Icons.Rounded.Adjust
        "machines" -> Icons.Rounded.Album
        "berries" -> Icons.Rounded.Eco
        "key" -> Icons.Rounded.Key
        "battle" -> Icons.Rounded.Shield
        "mail" -> Icons.Rounded.Mail
        else -> Icons.Rounded.Category
    }
}
