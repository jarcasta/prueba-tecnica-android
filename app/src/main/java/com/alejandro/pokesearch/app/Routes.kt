package com.alejandro.pokesearch.app

import com.alejandro.pokesearch.core.domain.PokeCategory
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object DashboardRoute

@Serializable
data class DetailRoute(val category: PokeCategory, val id: Int)
