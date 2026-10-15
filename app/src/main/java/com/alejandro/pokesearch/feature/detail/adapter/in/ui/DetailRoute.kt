package com.alejandro.pokesearch.feature.detail.adapter.`in`.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.domain.EntryKey

@Composable
fun DetailRoute(
    onBack: () -> Unit,
    onOpenEntry: (EntryKey) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val staleDataMessage = stringResource(R.string.stale_data)
    val showStaleData = (state as? DetailUiState.Content)?.let { it.isStale && !it.isOffline } == true

    LaunchedEffect(showStaleData) {
        if (showStaleData) snackbarHostState.showSnackbar(staleDataMessage)
    }

    DetailScreen(
        state = state,
        onBack = onBack,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = viewModel::onRetry,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onOpenEntry = onOpenEntry,
    )
}
