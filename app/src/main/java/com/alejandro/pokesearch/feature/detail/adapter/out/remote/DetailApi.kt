package com.alejandro.pokesearch.feature.detail.adapter.out.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface DetailApi {
    @GET("pokemon/{id}")
    suspend fun pokemon(@Path("id") id: Int): PokemonDto
}
