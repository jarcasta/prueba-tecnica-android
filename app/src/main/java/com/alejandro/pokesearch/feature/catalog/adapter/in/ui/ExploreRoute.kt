package com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val listState = exploreListState(refresh, items.loadState.append, items.itemCount)
    val snackbarHostState = remember { SnackbarHostState() }
    val staleDataMessage = (listState as? ExploreListState.Content)?.staleDataError?.let { staleDataMessage(it) }

    LaunchedEffect(refresh, staleDataMessage != null) {
        if (staleDataMessage != null) snackbarHostState.showSnackbar(staleDataMessage)
    }

    ExploreScreen(
        category = state.category,
        items = items,
        listState = listState,
        onCategorySelect = viewModel::onCategorySelect,
        onItemClick = { item -> onOpenDetail(item.key) },
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = items::retry,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,
        modifier = modifier,
    )
}

@Composable
private fun staleDataMessage(error: DomainError): String = stringResource(
    if (error == DomainError.NoConnection) R.string.offline_banner else R.string.stale_data,
)
