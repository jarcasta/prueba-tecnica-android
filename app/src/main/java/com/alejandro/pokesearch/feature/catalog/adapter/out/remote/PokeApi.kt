package com.alejandro.pokesearch.feature.catalog.adapter.out.remote

import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.network.NamedResourceListDto
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

private const val POKEMON_ARTWORK_URL =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png"

fun catalogImageUrl(category: PokeCategory, entryId: Int): String? = when (category) {
    PokeCategory.POKEMON -> POKEMON_ARTWORK_URL.format(entryId)
    PokeCategory.ITEM_CATEGORY, PokeCategory.REGION, PokeCategory.GENERATION -> null
}
