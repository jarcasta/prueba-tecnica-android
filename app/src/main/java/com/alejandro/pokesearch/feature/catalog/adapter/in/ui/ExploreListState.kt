package com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui

import androidx.compose.runtime.Immutable
import androidx.paging.LoadState
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.DomainException

@Immutable
sealed interface ExploreListState {
    data object Loading : ExploreListState
    data class Error(val error: DomainError) : ExploreListState

    data object Empty : ExploreListState

    data class Content(
        val totalCount: Int?,
        val footer: PagingFooterState,
        val staleDataError: DomainError?,
    ) : ExploreListState
}

enum class PagingFooterState { None, Loading, Error }

fun exploreListState(refresh: LoadState, append: LoadState, itemCount: Int): ExploreListState = when {
    itemCount == 0 && refresh is LoadState.Error -> ExploreListState.Error(refresh.error.toDomainError())
    itemCount == 0 && refresh is LoadState.NotLoading && append.endOfPaginationReached -> ExploreListState.Empty
    itemCount == 0 -> ExploreListState.Loading
    else -> ExploreListState.Content(
        totalCount = itemCount.takeIf { append.endOfPaginationReached },
        footer = when (append) {
            is LoadState.Loading -> PagingFooterState.Loading
            is LoadState.Error -> PagingFooterState.Error
            is LoadState.NotLoading -> PagingFooterState.None
        },
        staleDataError = (refresh as? LoadState.Error)?.error?.toDomainError(),
    )
}

private fun Throwable.toDomainError(): DomainError = (this as? DomainException)?.error ?: DomainError.Unexpected
