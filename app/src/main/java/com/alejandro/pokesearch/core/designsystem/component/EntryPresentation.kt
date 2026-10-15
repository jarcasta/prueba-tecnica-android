package com.alejandro.pokesearch.core.designsystem.component

import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.PokeCategory

/** Formateo par el texto o imagenes a mostrar. */

private val romanNumeral = Regex("^[ivxlcdm]+$")
private const val NUMBER_DIGITS = 3

fun displayNameOf(category: PokeCategory, rawName: String): String =
    rawName.split('-')
        .filter { it.isNotEmpty() }
        .joinToString(" ") { word ->
            if (category == PokeCategory.GENERATION && romanNumeral.matches(word)) {
                word.uppercase()
            } else {
                word.replaceFirstChar { it.titlecase() }
            }
        }

fun formatEntryNumber(id: EntryId): String = "#" + id.value.toString().padStart(NUMBER_DIGITS, '0')


fun entryLeadingOf(category: PokeCategory, rawName: String, imageUrl: String?): ResultLeading = when (category) {
    PokeCategory.POKEMON -> ResultLeading.Sprite(imageUrl)
    PokeCategory.GENERATION -> ResultLeading.Monogram(
        rawName.substringAfterLast('-').takeIf { romanNumeral.matches(it) }?.uppercase() ?: initialOf(rawName),
    )
    PokeCategory.ITEM_CATEGORY, PokeCategory.REGION -> ResultLeading.Monogram(initialOf(rawName))
}

private fun initialOf(rawName: String): String = rawName.take(1).uppercase()
