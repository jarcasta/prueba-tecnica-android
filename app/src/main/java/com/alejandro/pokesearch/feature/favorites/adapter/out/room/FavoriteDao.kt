package com.alejandro.pokesearch.feature.favorites.adapter.out.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.alejandro.pokesearch.core.domain.PokeCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT category, entryId FROM favorites WHERE userId = :userId")
    fun observeKeys(userId: Long): Flow<List<FavoriteKeyRow>>

    @Query(
        """
        SELECT * FROM favorites
        WHERE userId = :userId
          AND (:category IS NULL OR category = :category)
          AND name LIKE :namePattern ESCAPE '\'
        ORDER BY addedAt DESC
        """,
    )
    fun search(userId: Long, category: PokeCategory?, namePattern: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND category = :category AND entryId = :entryId)")
    suspend fun exists(userId: Long, category: PokeCategory, entryId: Int): Boolean

    @Upsert
    suspend fun upsert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND category = :category AND entryId = :entryId")
    suspend fun delete(userId: Long, category: PokeCategory, entryId: Int)

    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun deleteAllOf(userId: Long): Int

    @Query("SELECT userId, COUNT(*) AS favoriteCount FROM favorites GROUP BY userId")
    fun observeCountsByUser(): Flow<List<UserFavoriteCountRow>>
}
