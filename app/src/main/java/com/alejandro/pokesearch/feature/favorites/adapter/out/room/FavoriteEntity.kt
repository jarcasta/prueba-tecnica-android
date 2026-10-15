package com.alejandro.pokesearch.feature.favorites.adapter.out.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.feature.auth.adapter.out.room.UserEntity

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "category", "entryId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["userId"])],
)
data class FavoriteEntity(
    val userId: Long,
    val category: PokeCategory,
    val entryId: Int,
    val name: String,
    val imageUrl: String?,
    /** Epoch millis. */
    val addedAt: Long,
)

data class FavoriteKeyRow(
    val category: PokeCategory,
    val entryId: Int,
)

data class UserFavoriteCountRow(
    val userId: Long,
    val favoriteCount: Int,
)
