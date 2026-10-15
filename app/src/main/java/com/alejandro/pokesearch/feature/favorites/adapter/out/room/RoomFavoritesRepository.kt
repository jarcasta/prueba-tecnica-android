package com.alejandro.pokesearch.feature.favorites.adapter.out.room

import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.auth.domain.UserId
import com.alejandro.pokesearch.feature.favorites.domain.Favorite
import com.alejandro.pokesearch.feature.favorites.domain.FavoriteSnapshot
import com.alejandro.pokesearch.feature.favorites.domain.FavoritesRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFavoritesRepository @Inject constructor(
    private val dao: FavoriteDao,
) : FavoritesRepository {
    override fun observeKeys(userId: UserId): Flow<Set<EntryKey>> =
        dao.observeKeys(userId.value).map { rows ->
            rows.mapNotNullTo(mutableSetOf()) { row -> entryKeyOf(row.category, row.entryId) }
        }

    override fun search(userId: UserId, query: String, category: PokeCategory?): Flow<List<Favorite>> =
        dao.search(userId.value, category, containsPattern(query)).map { rows -> rows.mapNotNull { it.toDomain() } }

    override suspend fun find(userId: UserId, key: EntryKey): Favorite? =
        dao.find(userId.value, key.category, key.id.value)?.toDomain()

    override suspend fun add(favorite: Favorite) = dao.upsert(favorite.toEntity())

    override suspend fun remove(userId: UserId, key: EntryKey) = dao.delete(userId.value, key.category, key.id.value)

    override suspend fun removeAll(userId: UserId): Int = dao.deleteAllOf(userId.value)
}

internal fun containsPattern(query: String): String {
    val escaped = query
        .replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")
    return "%$escaped%"
}

private fun entryKeyOf(category: PokeCategory, entryId: Int): EntryKey? =
    EntryId.of(entryId).getOrNull()?.let { EntryKey(category, it) }

private fun FavoriteEntity.toDomain(): Favorite? = entryKeyOf(category, entryId)?.let { key ->
    Favorite(
        userId = UserId(userId),
        key = key,
        snapshot = FavoriteSnapshot(name = name, imageUrl = imageUrl),
        addedAt = Instant.ofEpochMilli(addedAt),
    )
}

private fun Favorite.toEntity() = FavoriteEntity(
    userId = userId.value,
    category = key.category,
    entryId = key.id.value,
    name = snapshot.name,
    imageUrl = snapshot.imageUrl,
    addedAt = addedAt.toEpochMilli(),
)
