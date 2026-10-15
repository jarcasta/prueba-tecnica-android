package com.alejandro.pokesearch.feature.catalog.adapter.out.room

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.alejandro.pokesearch.core.domain.Clock
import com.alejandro.pokesearch.core.domain.DomainException
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.core.network.NamedResourceListDto
import com.alejandro.pokesearch.core.network.toApiPath
import com.alejandro.pokesearch.core.network.toDomainError
import com.alejandro.pokesearch.feature.catalog.adapter.out.remote.PokeApi
import java.time.Duration
import java.time.Instant
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalPagingApi::class)
class CatalogRemoteMediator(
    private val category: PokeCategory,
    private val api: PokeApi,
    private val dao: CatalogDao,
    private val clock: Clock,
) : RemoteMediator<Int, CatalogEntryEntity>() {

    override suspend fun initialize(): InitializeAction {
        val syncState = dao.syncState(category) ?: return InitializeAction.LAUNCH_INITIAL_REFRESH
        val age = Duration.between(Instant.ofEpochMilli(syncState.lastSyncedAt), clock.now())
        return if (age < CACHE_TTL) InitializeAction.SKIP_INITIAL_REFRESH else InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, CatalogEntryEntity>): MediatorResult {
        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val syncState = dao.syncState(category)
                if (syncState?.endReached == true) return MediatorResult.Success(endOfPaginationReached = true)
                syncState?.nextOffset ?: 0
            }
        }
        return try {
            val page = api.listResources(category.toApiPath(), offset = offset, limit = PAGE_SIZE)
            val endReached = page.next == null
            dao.savePage(
                category = category,
                entries = page.toEntities(offset),
                syncState = SyncStateEntity(
                    category = category,
                    nextOffset = offset + page.results.size,
                    endReached = endReached,
                    lastSyncedAt = clock.now().toEpochMilli(),
                ),
                replaceExisting = loadType == LoadType.REFRESH,
            )
            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            MediatorResult.Error(DomainException(error.toDomainError(), error))
        }
    }

    private fun NamedResourceListDto.toEntities(offset: Int): List<CatalogEntryEntity> =
        results.mapIndexedNotNull { index, resource ->
            EntryId.fromResourceUrl(resource.url).getOrNull()?.let { id ->
                CatalogEntryEntity(category = category, entryId = id.value, name = resource.name, position = offset + index)
            }
        }

    companion object {
        const val PAGE_SIZE = 50
        val CACHE_TTL: Duration = Duration.ofHours(24)
    }
}
