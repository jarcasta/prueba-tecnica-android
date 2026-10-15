package com.alejandro.pokesearch.feature.catalog.application

import androidx.paging.PagingData
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.feature.catalog.domain.CatalogEntry
import com.alejandro.pokesearch.feature.catalog.domain.CatalogRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCatalogUseCase @Inject constructor(
    private val catalogRepository: CatalogRepository,
) {
    operator fun invoke(category: PokeCategory): Flow<PagingData<CatalogEntry>> =
        catalogRepository.observeCatalog(category)
}
