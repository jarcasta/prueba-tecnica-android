package com.alejandro.pokesearch.feature.detail.adapter.out

import app.cash.turbine.test
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.Outcome
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.network.NamedResourceDto
import com.alejandro.pokesearch.feature.auth.testing.FixedClock
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.PokemonDto
import com.alejandro.pokesearch.feature.detail.adapter.out.remote.RegionDto
import com.alejandro.pokesearch.feature.detail.adapter.out.room.DetailCacheEntity
import com.alejandro.pokesearch.feature.detail.domain.PokemonDetail
import com.alejandro.pokesearch.feature.detail.domain.RegionDetail
import com.alejandro.pokesearch.feature.detail.testing.FakeDetailApi
import com.alejandro.pokesearch.feature.detail.testing.FakeDetailCacheDao
import com.alejandro.pokesearch.feature.detail.testing.pokemonDto
import com.alejandro.pokesearch.feature.favorites.testing.entryKey
import java.io.IOException
import java.time.Duration
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OfflineFirstDetailRepositoryTest {

    private val clock = FixedClock()
    private val api = FakeDetailApi()
    private val dao = FakeDetailCacheDao()
    private val json = Json { ignoreUnknownKeys = true }
    private val repository = OfflineFirstDetailRepository(api, dao, json, clock)
    private val pikachu = entryKey(PokeCategory.POKEMON, 25)

    private suspend fun givenCache(dto: PokemonDto, age: Duration) {
        val fetchedAt = (clock.now - age).toEpochMilli()
        dao.upsert(DetailCacheEntity(PokeCategory.POKEMON, dto.id, json.encodeToString(PokemonDto.serializer(), dto), fetchedAt))
    }

    @Test
    fun `cuando la cache es vigente, entonces no llama a la red`() = runTest {
        givenCache(pokemonDto(25, "pikachu"), age = Duration.ofHours(23))

        val result = repository.refresh(pikachu)

        assertEquals(Outcome.Success(Unit), result)
        assertEquals(0, api.calls)
        repository.observe(pikachu).test { assertEquals("pikachu", awaitItem()?.name) }
    }

    @Test
    fun `cuando la cache esta vencida, entonces se refresca desde la red`() = runTest {
        givenCache(pokemonDto(25, "pikachu-old"), age = Duration.ofHours(25))
        api.response = pokemonDto(25, "pikachu")

        val result = repository.refresh(pikachu)

        assertEquals(Outcome.Success(Unit), result)
        assertEquals(1, api.calls)
        assertEquals(clock.now.toEpochMilli(), dao.stored(PokeCategory.POKEMON, 25)?.fetchedAt)
        repository.observe(pikachu).test {
            val detail = awaitItem() as PokemonDetail
            assertEquals("pikachu", detail.name)
            assertEquals(0.4, detail.heightMeters, 0.0001)
        }
    }

    @Test
    fun `cuando el refresco falla con cache, entonces devuelve el error y conserva la cache`() = runTest {
        givenCache(pokemonDto(25, "pikachu"), age = Duration.ofDays(3))
        api.failure = IOException("offline")

        val result = repository.refresh(pikachu)

        assertEquals(Outcome.Failure(DomainError.NoConnection), result)
        repository.observe(pikachu).test { assertEquals("pikachu", awaitItem()?.name) }
    }

    @Test
    fun `cuando no hay cache y el refresco falla, entonces devuelve el error sin detalle`() = runTest {
        api.failure = IOException("offline")

        val result = repository.refresh(pikachu)

        assertEquals(Outcome.Failure(DomainError.NoConnection), result)
        repository.observe(pikachu).test { assertNull(awaitItem()) }
    }

    @Test
    fun `cuando una region se refresca, entonces se cachea y despues se lee sin red`() = runTest {
        val kanto = entryKey(PokeCategory.REGION, 1)
        api.regionResponse = RegionDto(
            name = "kanto",
            mainGeneration = NamedResourceDto("generation-i", ""),
            locations = listOf(NamedResourceDto("route-1", "")),
        )

        assertEquals(Outcome.Success(Unit), repository.refresh(kanto))
        api.failure = IOException("offline")
        clock.now += Duration.ofHours(1)

        assertEquals("the cache is still fresh", Outcome.Success(Unit), repository.refresh(kanto))
        assertEquals(1, api.calls)
        repository.observe(kanto).test {
            val detail = awaitItem() as RegionDetail
            assertEquals("generation-i", detail.mainGeneration)
            assertEquals(1, detail.locationCount)
        }
    }

    @Test
    fun `cuando la cache no se puede decodificar, entonces se trata como ausente y se refresca`() = runTest {
        dao.upsert(DetailCacheEntity(PokeCategory.POKEMON, 25, "{\"broken\": true}", clock.now.toEpochMilli()))
        api.response = pokemonDto(25, "pikachu")

        repository.refresh(pikachu)

        assertEquals(1, api.calls)
        repository.observe(pikachu).test { assertEquals("pikachu", awaitItem()?.name) }
    }
}
