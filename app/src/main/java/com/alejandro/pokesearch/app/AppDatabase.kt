package com.alejandro.pokesearch.app

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.alejandro.pokesearch.feature.auth.adapter.out.room.SessionDao
import com.alejandro.pokesearch.feature.auth.adapter.out.room.SessionEntity
import com.alejandro.pokesearch.feature.auth.adapter.out.room.UserDao
import com.alejandro.pokesearch.feature.auth.adapter.out.room.UserEntity
import com.alejandro.pokesearch.feature.catalog.adapter.out.room.CatalogDao
import com.alejandro.pokesearch.feature.catalog.adapter.out.room.CatalogEntryEntity
import com.alejandro.pokesearch.feature.catalog.adapter.out.room.SyncStateEntity

@Database(
    entities = [
        UserEntity::class,
        SessionEntity::class,
        CatalogEntryEntity::class,
        SyncStateEntity::class,
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        //adds catalog_entries and sync_state.
        AutoMigration(from = 1, to = 2),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun sessionDao(): SessionDao

    abstract fun catalogDao(): CatalogDao

    companion object {
        const val NAME = "pokesearch.db"
    }
}
