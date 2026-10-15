package com.alejandro.pokesearch.feature.detail.di

import com.alejandro.pokesearch.feature.detail.adapter.out.OfflineFirstDetailRepository
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.DetailApi
import com.alejandro.pokesearch.feature.detail.domain.DetailRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
interface DetailModule {
    @Binds
    fun bindDetailRepository(impl: OfflineFirstDetailRepository): DetailRepository

    companion object {
        @Provides
        @Singleton
        fun provideDetailApi(retrofit: Retrofit): DetailApi = retrofit.create(DetailApi::class.java)
    }
}
