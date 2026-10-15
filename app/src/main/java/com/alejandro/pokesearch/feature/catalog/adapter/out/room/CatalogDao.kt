package com.alejandro.pokesearch.feature.catalog.adapter.out.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.alejandro.pokesearch.core.domain.PokeCategory

@Dao
abstract class CatalogDao {
    @Query("SELECT * FROM catalog_entries WHERE category = :category ORDER BY position ASC")
    abstract fun pagingSource(category: PokeCategory): PagingSource<Int, CatalogEntryEntity>

    @Query("SELECT * FROM sync_state WHERE category = :category")
    abstract suspend fun syncState(category: PokeCategory): SyncStateEntity?

    @Upsert
    abstract suspend fun upsertEntries(entries: List<CatalogEntryEntity>)

    @Upsert
    abstract suspend fun upsertSyncState(syncState: SyncStateEntity)

    @Query("DELETE FROM catalog_entries WHERE category = :category")
    abstract suspend fun deleteEntries(category: PokeCategory)

    @Transaction
    open suspend fun savePage(
        category: PokeCategory,
        entries: List<CatalogEntryEntity>,
        syncState: SyncStateEntity,
        replaceExisting: Boolean,
    ) {
        if (replaceExisting) deleteEntries(category)
        upsertEntries(entries)
        upsertSyncState(syncState)
    }
}
