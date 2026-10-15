package com.alejandro.pokesearch.feature.favorites.testing

import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.auth.domain.UserId
import com.alejandro.pokesearch.feature.favorites.domain.Favorite
import com.alejandro.pokesearch.feature.favorites.domain.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

fun entryKey(category: PokeCategory, id: Int): EntryKey =
    EntryKey(category, requireNotNull(EntryId.of(id).getOrNull()) { "Invalid test id: $id" })

class FakeFavoritesRepository : FavoritesRepository {
    private val favorites = MutableStateFlow<List<Favorite>>(emptyList())

    val all: List<Favorite> get() = favorites.value

    fun favoritesOf(userId: UserId): List<Favorite> = favorites.value.filter { it.userId == userId }

    override fun observeKeys(userId: UserId): Flow<Set<EntryKey>> =
        favorites.map { list -> list.filter { it.userId == userId }.mapTo(mutableSetOf()) { it.key } }

    override fun search(userId: UserId, query: String, category: PokeCategory?): Flow<List<Favorite>> =
        favorites.map { list ->
            list.filter {
                it.userId == userId &&
                    (category == null || it.key.category == category) &&
                    it.snapshot.name.contains(query, ignoreCase = true)
            }.sortedByDescending { it.addedAt }
        }

    override suspend fun isFavorite(userId: UserId, key: EntryKey): Boolean =
        favorites.value.any { it.userId == userId && it.key == key }

    override suspend fun add(favorite: Favorite) {
        favorites.update { list -> list.filterNot { it.userId == favorite.userId && it.key == favorite.key } + favorite }
    }

    override suspend fun remove(userId: UserId, key: EntryKey) {
        favorites.update { list -> list.filterNot { it.userId == userId && it.key == key } }
    }

    override suspend fun removeAll(userId: UserId): Int {
        val removed = favoritesOf(userId).size
        favorites.update { list -> list.filterNot { it.userId == userId } }
        return removed
    }
}
