package com.alejandro.pokesearch.feature.detail.adapter.`in`.ui

import com.alejandro.pokesearch.core.domain.PokeCategory
import kotlinx.serialization.Serializable

@Serializable
data class Detail(val category: PokeCategory, val id: Int)
