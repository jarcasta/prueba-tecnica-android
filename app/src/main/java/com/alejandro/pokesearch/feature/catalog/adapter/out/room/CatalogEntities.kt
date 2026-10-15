package com.alejandro.pokesearch.feature.catalog.adapter.out.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.alejandro.pokesearch.core.domain.PokeCategory

@Entity(
    tableName = "catalog_entries",
    primaryKeys = ["category", "entryId"],
    indices = [Index(value = ["category", "position"])],
)
data class CatalogEntryEntity(
    val category: PokeCategory,
    val entryId: Int,
    val name: String,
    val position: Int,
)

@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey val category: PokeCategory,
    val nextOffset: Int,
    val endReached: Boolean,
    val lastSyncedAt: Long,
)
