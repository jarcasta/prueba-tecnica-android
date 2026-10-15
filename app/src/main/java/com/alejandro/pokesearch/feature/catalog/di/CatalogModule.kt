package com.alejandro.pokesearch.feature.catalog.di

import com.alejandro.pokesearch.feature.catalog.adapter.out.OfflineFirstCatalogRepository
import com.alejandro.pokesearch.feature.catalog.adapter.out.remote.PokeApi
import com.alejandro.pokesearch.feature.catalog.domain.CatalogRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface CatalogModule {
    @Binds
    fun bindCatalogRepository(impl: OfflineFirstCatalogRepository): CatalogRepository

    companion object {
        @Provides
        @Singleton
        fun providePokeApi(retrofit: Retrofit): PokeApi = retrofit.create(PokeApi::class.java)
    }
}
