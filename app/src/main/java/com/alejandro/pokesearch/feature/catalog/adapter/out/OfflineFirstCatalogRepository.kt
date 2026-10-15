package com.alejandro.pokesearch.feature.catalog.adapter.out

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.flatMap
import com.alejandro.pokesearch.core.domain.Clock
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.catalog.adapter.out.remote.PokeApi
import com.alejandro.pokesearch.feature.catalog.adapter.out.remote.catalogImageUrl
import com.alejandro.pokesearch.feature.catalog.adapter.out.room.CatalogDao
import com.alejandro.pokesearch.feature.catalog.adapter.out.room.CatalogEntryEntity
import com.alejandro.pokesearch.feature.catalog.adapter.out.room.CatalogRemoteMediator
import com.alejandro.pokesearch.feature.catalog.domain.CatalogEntry
import com.alejandro.pokesearch.feature.catalog.domain.CatalogRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineFirstCatalogRepository @Inject constructor(
    private val api: PokeApi,
    private val dao: CatalogDao,
    private val clock: Clock,
) : CatalogRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun observeCatalog(category: PokeCategory): Flow<PagingData<CatalogEntry>> =
        Pager(
            config = PagingConfig(
                pageSize = CatalogRemoteMediator.PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            remoteMediator = CatalogRemoteMediator(category, api, dao, clock),
            pagingSourceFactory = { dao.pagingSource(category) },
        ).flow.map { page -> page.flatMap { entity -> listOfNotNull(entity.toDomain()) } }

    private companion object {
        const val PREFETCH_DISTANCE = 5
    }
}

private fun CatalogEntryEntity.toDomain(): CatalogEntry? = EntryId.of(entryId).getOrNull()?.let { id ->
    CatalogEntry(
        key = EntryKey(category, id),
        name = name,
        imageUrl = catalogImageUrl(category, entryId),
    )
}
