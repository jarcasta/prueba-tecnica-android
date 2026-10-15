package com.alejandro.pokesearch.feature.favorites.adapter.`in`.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.displayNameOf
import com.alejandro.pokesearch.core.domain.EntryKey

@Composable
fun FavoritesRoute(
    onOpenDetail: (EntryKey) -> Unit,
    onExploreClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FavoritesEffect.ShowUndo -> {
                    val favorite = effect.favorite
                    val name = displayNameOf(favorite.key.category, favorite.snapshot.name)
                    val result = snackbarHostState.showSnackbar(
                        message = resources.getString(R.string.favorite_removed, name),
                        actionLabel = resources.getString(R.string.action_undo),
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) viewModel.onUndoRemove(favorite)
                }
            }
        }
    }

    FavoritesScreen(
        filters = filters,
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onCategorySelect = viewModel::onCategorySelect,
        onClearFilters = viewModel::onClearFilters,
        onItemClick = { item -> onOpenDetail(item.key) },
        onRemoveFavorite = viewModel::onRemoveFavorite,
        onExploreClick = onExploreClick,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,
        modifier = modifier,
    )
}
