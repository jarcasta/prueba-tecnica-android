package com.alejandro.pokesearch.feature.catalog.domain

import androidx.paging.PagingData
import com.alejandro.pokesearch.core.domain.PokeCategory
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun observeCatalog(category: PokeCategory): Flow<PagingData<CatalogEntry>>
}
