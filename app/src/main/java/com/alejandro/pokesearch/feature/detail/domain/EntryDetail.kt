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

data class BaseStat(
    val name: String,
    val value: Int,
)
