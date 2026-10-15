package com.alejandro.pokesearch.feature.detail.adapter.out.room

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Query
import androidx.room.Upsert
import com.alejandro.pokesearch.core.domain.PokeCategory
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "detail_cache",
    primaryKeys = ["category", "entryId"],
)
data class DetailCacheEntity(
    val category: PokeCategory,
    val entryId: Int,
    val json: String,
    val fetchedAt: Long,
)

@Dao
interface DetailCacheDao {
    @Query("SELECT * FROM detail_cache WHERE category = :category AND entryId = :entryId")
    fun observe(category: PokeCategory, entryId: Int): Flow<DetailCacheEntity?>

    @Query("SELECT * FROM detail_cache WHERE category = :category AND entryId = :entryId")
    suspend fun find(category: PokeCategory, entryId: Int): DetailCacheEntity?

    @Upsert
    suspend fun upsert(entry: DetailCacheEntity)
}
