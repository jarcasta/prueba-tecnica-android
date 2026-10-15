package com.alejandro.pokesearch.feature.detail.domain

import com.alejandro.pokesearch.core.domain.EntryKey

sealed interface EntryDetail {
    val key: EntryKey
    val name: String
    val imageUrl: String?
}

data class PokemonDetail(
    override val key: EntryKey,
    override val name: String,
    val artworkUrl: String?,
    val types: List<String>,
    val heightMeters: Double,
    val weightKg: Double,
    val abilities: List<String>,
    val stats: List<BaseStat>,
) : EntryDetail {
    override val imageUrl: String? get() = artworkUrl
}

data class ItemCategoryDetail(
    override val key: EntryKey,
    override val name: String,
    val pocket: String,
    val items: List<CategoryItem>,
) : EntryDetail {
    override val imageUrl: String? get() = null
}

data class CategoryItem(
    val name: String,
    val spriteUrl: String,
)

data class RegionDetail(
    override val key: EntryKey,
    override val name: String,
    val mainGeneration: String?,
    val locationCount: Int,
    val pokedexes: List<String>,
    val versionGroups: List<String>,
) : EntryDetail {
    override val imageUrl: String? get() = null
}

data class GenerationDetail(
    override val key: EntryKey,
    override val name: String,
    val mainRegion: String,
    val newSpeciesCount: Int,
    val versionGroups: List<String>,
    val speciesSample: List<SpeciesSummary>,
) : EntryDetail {
    override val imageUrl: String? get() = null

    companion object {
        const val SPECIES_SAMPLE_SIZE = 12
    }
}

data class SpeciesSummary(
    val key: EntryKey,
    val name: String,
    val artworkUrl: String,
)

data class BaseStat(
    val name: String,
    val value: Int,
)
