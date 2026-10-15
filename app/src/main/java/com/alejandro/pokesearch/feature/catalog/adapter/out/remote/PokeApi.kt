package com.alejandro.pokesearch.feature.catalog.adapter.out.remote

import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.network.NamedResourceListDto
import com.alejandro.pokesearch.core.network.pokemonArtworkUrl
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {
    @GET("{categoryPath}")
    suspend fun listResources(
        @Path("categoryPath") categoryPath: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): NamedResourceListDto
}

fun catalogImageUrl(category: PokeCategory, entryId: Int): String? = when (category) {
    PokeCategory.POKEMON -> pokemonArtworkUrl(entryId)
    PokeCategory.ITEM_CATEGORY, PokeCategory.REGION, PokeCategory.GENERATION -> null
}
