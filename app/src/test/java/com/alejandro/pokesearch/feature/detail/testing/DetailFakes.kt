package com.alejandro.pokesearch.feature.detail.testing

import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.DetailApi
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.PokemonDto
import com.alejandro.pokesearch.feature.detail.adapter.out.room.DetailCacheDao
import com.alejandro.pokesearch.feature.detail.adapter.out.room.DetailCacheEntity
import com.alejandro.pokesearch.feature.detail.domain.BaseStat
import com.alejandro.pokesearch.feature.detail.domain.PokemonDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

fun pokemonDto(id: Int, name: String) = PokemonDto(id = id, name = name, height = 4, weight = 60)

fun pokemonDetail(key: EntryKey, name: String) = PokemonDetail(
    key = key,
    name = name,
    artworkUrl = "https://img/${key.id.value}.png",
    types = listOf("electric"),
    heightMeters = 0.4,
    weightKg = 6.0,
    abilities = listOf("static"),
    stats = listOf(BaseStat("hp", 35)),
)

class FakeDetailApi : DetailApi {
    var response: PokemonDto? = null
    var failure: Exception? = null
    var calls = 0
        private set

    override suspend fun pokemon(id: Int): PokemonDto {
        calls++
        failure?.let { throw it }
        return requireNotNull(response) { "No response configured" }
    }
}

class FakeDetailCacheDao : DetailCacheDao {
    private val rows = MutableStateFlow<Map<Pair<PokeCategory, Int>, DetailCacheEntity>>(emptyMap())

    fun stored(category: PokeCategory, entryId: Int): DetailCacheEntity? = rows.value[category to entryId]

    override fun observe(category: PokeCategory, entryId: Int): Flow<DetailCacheEntity?> =
        rows.map { it[category to entryId] }

    override suspend fun find(category: PokeCategory, entryId: Int): DetailCacheEntity? = stored(category, entryId)

    override suspend fun upsert(entry: DetailCacheEntity) {
        rows.update { it + ((entry.category to entry.entryId) to entry) }
    }
}
