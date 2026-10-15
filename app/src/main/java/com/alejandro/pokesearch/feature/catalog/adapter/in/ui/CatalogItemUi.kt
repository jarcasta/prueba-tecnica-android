package com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui

import androidx.compose.runtime.Immutable
import com.alejandro.pokesearch.core.designsystem.component.ResultLeading
import com.alejandro.pokesearch.core.designsystem.component.displayNameOf
import com.alejandro.pokesearch.core.designsystem.component.entryLeadingOf
import com.alejandro.pokesearch.core.designsystem.component.formatEntryNumber
import com.alejandro.pokesearch.core.domain.EntryKey
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
    leading = entryLeadingOf(key.category, name, imageUrl),
    isFavorite = isFavorite,
)
