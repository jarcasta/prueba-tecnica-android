package com.alejandro.pokesearch.feature.auth.di

import com.alejandro.pokesearch.feature.auth.adapter.out.room.RoomSessionRepository
import com.alejandro.pokesearch.feature.auth.adapter.out.room.RoomUserRepository
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
}
