package com.alejandro.pokesearch.feature.favorites.adapter.`in`.ui

import androidx.compose.runtime.Immutable
import com.alejandro.pokesearch.core.designsystem.component.ResultLeading
import com.alejandro.pokesearch.core.designsystem.component.displayNameOf
import com.alejandro.pokesearch.core.designsystem.component.entryLeadingOf
import com.alejandro.pokesearch.core.designsystem.component.formatEntryNumber
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.feature.favorites.domain.Favorite

@Immutable
data class FavoriteItemUi(
    val key: EntryKey,
    val rawName: String,
    val name: String,
    val number: String,
    val imageUrl: String?,
    val leading: ResultLeading,
) {
    val listKey: String get() = "${key.category.name}:${key.id.value}"
}

fun Favorite.toItemUi(): FavoriteItemUi = FavoriteItemUi(
    key = key,
    rawName = snapshot.name,
    name = displayNameOf(key.category, snapshot.name),
    number = formatEntryNumber(key.id),
    imageUrl = snapshot.imageUrl,
    leading = entryLeadingOf(key.category, snapshot.name, snapshot.imageUrl),
)

@Immutable
data class FavoritesFilters(
    val query: String = "",
    val category: PokeCategory? = null,
)

@Immutable
sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState

    data object Empty : FavoritesUiState

    data class NoMatches(val query: String, val category: PokeCategory?) : FavoritesUiState

    data class Content(val items: List<FavoriteItemUi>) : FavoritesUiState
}

sealed interface FavoritesEffect {
    data class ShowUndo(val favorite: Favorite) : FavoritesEffect
}
