package com.alejandro.pokesearch.app

import com.alejandro.pokesearch.core.domain.PokeCategory
import kotlinx.serialization.Serializable

@Serializable
data object Login

@Serializable
data object Dashboard

@Serializable
data class Detail(val category: PokeCategory, val id: Int)
