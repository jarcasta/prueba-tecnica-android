package com.alejandro.pokesearch.app

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alejandro.pokesearch.feature.auth.adapter.out.room.SessionDao
import com.alejandro.pokesearch.feature.auth.adapter.out.room.SessionEntity
import com.alejandro.pokesearch.feature.auth.adapter.out.room.UserDao
import com.alejandro.pokesearch.feature.auth.adapter.out.room.UserEntity

@Database(
    entities = [UserEntity::class, SessionEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun sessionDao(): SessionDao

    companion object {
        const val NAME = "pokesearch.db"
    }
}
