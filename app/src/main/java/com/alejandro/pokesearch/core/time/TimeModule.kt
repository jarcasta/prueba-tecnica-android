package com.alejandro.pokesearch.core.time

import com.alejandro.pokesearch.core.domain.Clock
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TimeModule {
    @Binds
    fun bindClock(impl: SystemClock): Clock
}
