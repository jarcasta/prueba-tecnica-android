package com.alejandro.pokesearch.core.network

import kotlinx.serialization.Serializable

@Serializable
data class NamedResourceListDto(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<NamedResourceDto>,
)

@Serializable
data class NamedResourceDto(
    val name: String,
    val url: String,
)
