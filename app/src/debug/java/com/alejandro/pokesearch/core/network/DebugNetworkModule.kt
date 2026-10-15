package com.alejandro.pokesearch.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

/** Network logging, only compiled into debug builds. */
@Module
@InstallIn(SingletonComponent::class)
object DebugNetworkModule {
    @Provides
    @IntoSet
    fun provideLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
}
