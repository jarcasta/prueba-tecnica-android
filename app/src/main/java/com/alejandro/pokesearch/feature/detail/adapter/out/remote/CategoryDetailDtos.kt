package com.alejandro.pokesearch.feature.detail.adapter.out.remote

import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.core.network.NamedResourceDto
import com.alejandro.pokesearch.core.network.itemSpriteUrl
import com.alejandro.pokesearch.core.network.pokemonArtworkUrl
import com.alejandro.pokesearch.feature.detail.domain.CategoryItem
import com.alejandro.pokesearch.feature.detail.domain.GenerationDetail
import com.alejandro.pokesearch.feature.detail.domain.ItemCategoryDetail
import com.alejandro.pokesearch.feature.detail.domain.RegionDetail
import com.alejandro.pokesearch.feature.detail.domain.SpeciesSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemCategoryDto(
    val name: String,
    val pocket: NamedResourceDto,
    val items: List<NamedResourceDto> = emptyList(),
)

@Serializable
data class RegionDto(
    val name: String,
    @SerialName("main_generation") val mainGeneration: NamedResourceDto? = null,
    val locations: List<NamedResourceDto> = emptyList(),
    val pokedexes: List<NamedResourceDto> = emptyList(),
    @SerialName("version_groups") val versionGroups: List<NamedResourceDto> = emptyList(),
)

@Serializable
data class GenerationDto(
    val name: String,
    @SerialName("main_region") val mainRegion: NamedResourceDto,
    @SerialName("pokemon_species") val pokemonSpecies: List<NamedResourceDto> = emptyList(),
    @SerialName("version_groups") val versionGroups: List<NamedResourceDto> = emptyList(),
)

fun ItemCategoryDto.toDomain(key: EntryKey): ItemCategoryDetail = ItemCategoryDetail(
    key = key,
    name = name,
    pocket = pocket.name,
    items = items.map { CategoryItem(name = it.name, spriteUrl = itemSpriteUrl(it.name)) },
)

fun RegionDto.toDomain(key: EntryKey): RegionDetail = RegionDetail(
    key = key,
    name = name,
    mainGeneration = mainGeneration?.name,
    locationCount = locations.size,
    pokedexes = pokedexes.map { it.name },
    versionGroups = versionGroups.map { it.name },
)

fun GenerationDto.toDomain(key: EntryKey): GenerationDetail = GenerationDetail(
    key = key,
    name = name,
    mainRegion = mainRegion.name,
    newSpeciesCount = pokemonSpecies.size,
    versionGroups = versionGroups.map { it.name },
    speciesSample = pokemonSpecies
        .mapNotNull { species -> EntryId.fromResourceUrl(species.url).getOrNull()?.let { id -> id to species.name } }
        .sortedBy { (id, _) -> id.value }
        .take(GenerationDetail.SPECIES_SAMPLE_SIZE)
        .map { (id, speciesName) ->
            SpeciesSummary(
                key = EntryKey(PokeCategory.POKEMON, id),
                name = speciesName,
                artworkUrl = pokemonArtworkUrl(id.value),
            )
        },
)
