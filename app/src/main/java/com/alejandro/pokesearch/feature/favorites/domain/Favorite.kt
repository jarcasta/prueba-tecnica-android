package com.alejandro.pokesearch.feature.favorites.domain

import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.feature.auth.domain.UserId
import java.time.Instant

data class FavoriteSnapshot(
    val name: String,
    val imageUrl: String?,
)

data class Favorite(
    val userId: UserId,
    val key: EntryKey,
    val snapshot: FavoriteSnapshot,
    val addedAt: Instant,
)
