package com.alejandro.pokesearch.feature.favorites.domain

import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.feature.auth.domain.UserId
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeKeys(userId: UserId): Flow<Set<EntryKey>>

    fun search(userId: UserId, query: String, category: PokeCategory?): Flow<List<Favorite>>

    suspend fun find(userId: UserId, key: EntryKey): Favorite?

    suspend fun add(favorite: Favorite)

    suspend fun remove(userId: UserId, key: EntryKey)

    suspend fun removeAll(userId: UserId): Int
}
