package com.alejandro.pokesearch.feature.auth.di

import com.alejandro.pokesearch.feature.auth.adapter.out.NoFavoritesCountReader
import com.alejandro.pokesearch.feature.auth.adapter.out.SystemClock
import com.alejandro.pokesearch.feature.auth.adapter.out.room.RoomSessionRepository
import com.alejandro.pokesearch.feature.auth.adapter.out.room.RoomUserRepository
import com.alejandro.pokesearch.feature.auth.domain.Clock
import com.alejandro.pokesearch.feature.auth.domain.FavoriteCountReader
import com.alejandro.pokesearch.feature.auth.domain.SessionRepository
import com.alejandro.pokesearch.feature.auth.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {
    @Binds
    fun bindUserRepository(impl: RoomUserRepository): UserRepository

    @Binds
    fun bindSessionRepository(impl: RoomSessionRepository): SessionRepository

    @Binds
    fun bindClock(impl: SystemClock): Clock

    @Binds
    fun bindFavoriteCountReader(impl: NoFavoritesCountReader): FavoriteCountReader
}
