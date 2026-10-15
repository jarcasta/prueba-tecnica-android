package com.alejandro.pokesearch.feature.detail.adapter.out.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface DetailApi {
    @GET("pokemon/{id}")
    suspend fun pokemon(@Path("id") id: Int): PokemonDto

    @GET("item-category/{id}")
    suspend fun itemCategory(@Path("id") id: Int): ItemCategoryDto

    @GET("region/{id}")
    suspend fun region(@Path("id") id: Int): RegionDto

    @GET("generation/{id}")
    suspend fun generation(@Path("id") id: Int): GenerationDto
}
