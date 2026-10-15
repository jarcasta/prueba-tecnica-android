package com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui

import androidx.compose.runtime.Immutable
import com.alejandro.pokesearch.core.designsystem.component.ResultLeading
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.feature.catalog.domain.CatalogEntry

@Immutable
data class CatalogItemUi(
    val key: EntryKey,
    val rawName: String,
    val name: String,
    val number: String,
    val imageUrl: String?,
    val leading: ResultLeading,
    val isFavorite: Boolean,
) {
    val listKey: String get() = "${key.category.name}:${key.id.value}"
}

fun CatalogEntry.toItemUi(isFavorite: Boolean): CatalogItemUi = CatalogItemUi(
    key = key,
    rawName = name,
    name = displayNameOf(key.category, name),
    number = formatEntryNumber(key.id),
    imageUrl = imageUrl,
    leading = leadingOf(key.category, name, imageUrl),
    isFavorite = isFavorite,
)

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

private fun leadingOf(category: PokeCategory, rawName: String, imageUrl: String?): ResultLeading = when (category) {
    PokeCategory.POKEMON -> ResultLeading.Sprite(imageUrl)
    PokeCategory.GENERATION -> ResultLeading.Monogram(
        rawName.substringAfterLast('-').takeIf { romanNumeral.matches(it) }?.uppercase() ?: initialOf(rawName),
    )
    PokeCategory.ITEM_CATEGORY, PokeCategory.REGION -> ResultLeading.Monogram(initialOf(rawName))
}

private fun initialOf(rawName: String): String = rawName.take(1).uppercase()
