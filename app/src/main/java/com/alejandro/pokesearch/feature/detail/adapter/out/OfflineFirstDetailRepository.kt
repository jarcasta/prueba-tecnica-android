package com.alejandro.pokesearch.feature.detail.adapter.out

import com.alejandro.pokesearch.core.domain.Clock
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.Outcome
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.core.network.toDomainError
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.DetailApi
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.GenerationDto
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.ItemCategoryDto
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.PokemonDto
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.RegionDto
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.toDomain
import com.alejandro.pokesearch.feature.detail.adapter.out.room.DetailCacheDao
import com.alejandro.pokesearch.feature.detail.adapter.out.room.DetailCacheEntity
import com.alejandro.pokesearch.feature.detail.domain.DetailRepository
import com.alejandro.pokesearch.feature.detail.domain.EntryDetail
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class OfflineFirstDetailRepository @Inject constructor(
    private val api: DetailApi,
    private val dao: DetailCacheDao,
    private val jsonCodec: Json,
    private val clock: Clock,
) : DetailRepository {

    override fun observe(key: EntryKey): Flow<EntryDetail?> =
        dao.observe(key.category, key.id.value).map { it?.decode() }.distinctUntilChanged()

    override suspend fun refresh(key: EntryKey): Outcome<Unit, DomainError> {
        val cached = dao.find(key.category, key.id.value)
        if (cached != null && cached.isFresh() && cached.decode() != null) return Outcome.Success(Unit)
        return try {
            val payload = fetchJson(key)
            dao.upsert(DetailCacheEntity(key.category, key.id.value, payload, clock.now().toEpochMilli()))
            Outcome.Success(Unit)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            Outcome.Failure(error.toDomainError())
        }
    }

    private suspend fun fetchJson(key: EntryKey): String {
        val id = key.id.value
        return when (key.category) {
            PokeCategory.POKEMON -> jsonCodec.encodeToString(PokemonDto.serializer(), api.pokemon(id))
            PokeCategory.ITEM_CATEGORY -> jsonCodec.encodeToString(ItemCategoryDto.serializer(), api.itemCategory(id))
            PokeCategory.REGION -> jsonCodec.encodeToString(RegionDto.serializer(), api.region(id))
            PokeCategory.GENERATION -> jsonCodec.encodeToString(GenerationDto.serializer(), api.generation(id))
        }
    }

    private fun DetailCacheEntity.isFresh(): Boolean =
        Duration.between(Instant.ofEpochMilli(fetchedAt), clock.now()) < CACHE_TTL

    private fun DetailCacheEntity.decode(): EntryDetail? {
        val key = EntryId.of(entryId).getOrNull()?.let { EntryKey(category, it) } ?: return null
        return try {
            when (category) {
                PokeCategory.POKEMON -> jsonCodec.decodeFromString(PokemonDto.serializer(), json).toDomain(key)
                PokeCategory.ITEM_CATEGORY -> jsonCodec.decodeFromString(ItemCategoryDto.serializer(), json).toDomain(key)
                PokeCategory.REGION -> jsonCodec.decodeFromString(RegionDto.serializer(), json).toDomain(key)
                PokeCategory.GENERATION -> jsonCodec.decodeFromString(GenerationDto.serializer(), json).toDomain(key)
            }
        } catch (_: SerializationException) {
            null
        }
    }

    companion object {
        val CACHE_TTL: Duration = Duration.ofHours(24)
    }
}
