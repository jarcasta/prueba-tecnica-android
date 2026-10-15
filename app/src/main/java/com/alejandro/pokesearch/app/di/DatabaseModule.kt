package com.alejandro.pokesearch.app.di

import android.content.Context
import androidx.room.Room
import com.alejandro.pokesearch.app.AppDatabase
import com.alejandro.pokesearch.feature.auth.adapter.out.room.SessionDao
import com.alejandro.pokesearch.feature.auth.adapter.out.room.UserDao
import com.alejandro.pokesearch.feature.catalog.adapter.out.room.CatalogDao
import com.alejandro.pokesearch.feature.favorites.adapter.out.room.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME).build()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideSessionDao(database: AppDatabase): SessionDao = database.sessionDao()

    @Provides
    fun provideCatalogDao(database: AppDatabase): CatalogDao = database.catalogDao()

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()
}
