package com.alejandro.pokesearch.feature.favorites.di

import com.alejandro.pokesearch.feature.auth.domain.FavoriteCountReader
import com.alejandro.pokesearch.feature.favorites.adapter.out.room.RoomFavoriteCountReader
import com.alejandro.pokesearch.feature.favorites.adapter.out.room.RoomFavoritesRepository
import com.alejandro.pokesearch.feature.favorites.domain.FavoritesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FavoritesModule {
    @Binds
    fun bindFavoritesRepository(impl: RoomFavoritesRepository): FavoritesRepository

    @Binds
    fun bindFavoriteCountReader(impl: RoomFavoriteCountReader): FavoriteCountReader
}
