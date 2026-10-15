package com.alejandro.pokesearch.feature.detail.adapter.out.remote

import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.network.NamedResourceDto
import com.alejandro.pokesearch.feature.detail.domain.BaseStat
import com.alejandro.pokesearch.feature.detail.domain.PokemonDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: PokemonSpritesDto = PokemonSpritesDto(),
    val types: List<PokemonTypeSlotDto> = emptyList(),
    val abilities: List<PokemonAbilitySlotDto> = emptyList(),
    val stats: List<PokemonStatDto> = emptyList(),
)

@Serializable
data class PokemonSpritesDto(
    val other: OtherSpritesDto? = null,
)

@Serializable
data class OtherSpritesDto(
    @SerialName("official-artwork") val officialArtwork: ArtworkDto? = null,
)

@Serializable
data class ArtworkDto(
    @SerialName("front_default") val frontDefault: String? = null,
)

@Serializable
data class PokemonTypeSlotDto(
    val slot: Int,
    val type: NamedResourceDto,
)

@Serializable
data class PokemonAbilitySlotDto(
    val slot: Int,
    val ability: NamedResourceDto,
)

@Serializable
data class PokemonStatDto(
    @SerialName("base_stat") val baseStat: Int,
    val stat: NamedResourceDto,
)

private const val DECIMETRES_PER_METRE = 10.0
private const val HECTOGRAMS_PER_KILOGRAM = 10.0

private val statOrder = listOf("hp", "attack", "defense", "special-attack", "special-defense", "speed")

fun PokemonDto.toDomain(key: EntryKey): PokemonDetail = PokemonDetail(
    key = key,
    name = name,
    artworkUrl = sprites.other?.officialArtwork?.frontDefault,
    types = types.sortedBy { it.slot }.map { it.type.name },
    heightMeters = height / DECIMETRES_PER_METRE,
    weightKg = weight / HECTOGRAMS_PER_KILOGRAM,
    abilities = abilities.sortedBy { it.slot }.map { it.ability.name },
    stats = stats
        .sortedBy { stat -> statOrder.indexOf(stat.stat.name).takeIf { it >= 0 } ?: statOrder.size }
        .map { BaseStat(name = it.stat.name, value = it.baseStat) },
)
