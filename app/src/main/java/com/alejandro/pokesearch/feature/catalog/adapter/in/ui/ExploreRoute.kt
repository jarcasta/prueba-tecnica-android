package com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.EntryKey

@Composable
fun ExploreRoute(
    onOpenDetail: (EntryKey) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val items = viewModel.catalog.collectAsLazyPagingItems()
    val refresh = items.loadState.refresh
    val append = items.loadState.append
    val listState = exploreListState(refresh, append, items.itemCount)
    val snackbarHostState = remember { SnackbarHostState() }
    val staleDataMessage = stringResource(R.string.stale_data)
    val showStaleData = (listState as? ExploreListState.Content)?.staleDataError
        ?.let { it != DomainError.NoConnection } == true
    val hasLoadError = refresh is LoadState.Error || append is LoadState.Error
    val currentItems by rememberUpdatedState(items)

    LaunchedEffect(refresh, showStaleData) {
        if (showStaleData) snackbarHostState.showSnackbar(staleDataMessage)
    }
    LaunchedEffect(hasLoadError) { viewModel.onLoadErrorChange(hasLoadError) }
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ExploreEffect.RetryFailedLoads -> currentItems.retry()
            }
        }
    }

    ExploreScreen(
        category = state.category,
        items = items,
        listState = listState,
        isOffline = state.isOffline,
        onCategorySelect = viewModel::onCategorySelect,
        onItemClick = { item -> onOpenDetail(item.key) },
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = items::retry,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,
        modifier = modifier,
    )
}
