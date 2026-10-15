package com.alejandro.pokesearch.feature.favorites.adapter.`in`.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.CategorySelector
import com.alejandro.pokesearch.core.designsystem.component.EmptyState
import com.alejandro.pokesearch.core.designsystem.component.LoadingState
import com.alejandro.pokesearch.core.designsystem.component.LoadingVariant
import com.alejandro.pokesearch.core.designsystem.component.LocalSearchField
import com.alejandro.pokesearch.core.designsystem.component.ResultListItem
import com.alejandro.pokesearch.core.designsystem.component.categoryLabel
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.auth.domain.UserId
import com.alejandro.pokesearch.feature.favorites.domain.Favorite
import com.alejandro.pokesearch.feature.favorites.domain.FavoriteSnapshot
import java.time.Instant

private const val CONTENT_TYPE_FAVORITE = "favorite"

@Composable
fun FavoritesScreen(
    filters: FavoritesFilters,
    state: FavoritesUiState,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (PokeCategory?) -> Unit,
    onClearFilters: () -> Unit,
    onItemClick: (FavoriteItemUi) -> Unit,
    onRemoveFavorite: (FavoriteItemUi) -> Unit,
    onExploreClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val bottomPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())
    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
            if (state != FavoritesUiState.Empty) {
                CategorySelector(selected = filters.category, onSelect = onCategorySelect, includeAll = true)
                LocalSearchField(
                    query = filters.query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                )
            }
            when (state) {
                FavoritesUiState.Loading -> LoadingState(variant = LoadingVariant.List, modifier = Modifier.fillMaxSize())
                FavoritesUiState.Empty -> EmptyState(
                    icon = Icons.Rounded.FavoriteBorder,
                    title = stringResource(R.string.favorites_empty_title),
                    message = stringResource(R.string.favorites_empty_message),
                    actionLabel = stringResource(R.string.nav_explore),
                    onAction = onExploreClick,
                    actionIcon = Icons.Rounded.Explore,
                    highlighted = true,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = bottomPadding,
                )
                is FavoritesUiState.NoMatches -> EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = stringResource(R.string.favorites_no_matches_title),
                    message = noMatchesMessage(state),
                    actionLabel = stringResource(R.string.favorites_clear_filters),
                    onAction = onClearFilters,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = bottomPadding,
                )
                is FavoritesUiState.Content -> LazyColumn(
                    state = lazyListState,
                    contentPadding = bottomPadding,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(items = state.items, key = { it.listKey }, contentType = { CONTENT_TYPE_FAVORITE }) { item ->
                        ResultListItem(
                            name = item.name,
                            number = item.number,
                            numberDescription = stringResource(R.string.entry_number_description, item.key.id.value),
                            leading = item.leading,
                            isFavorite = true,
                            onToggleFavorite = { onRemoveFavorite(item) },
                            onClick = { onItemClick(item) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottomPadding),
        )
    }
}

@Composable
private fun noMatchesMessage(state: FavoritesUiState.NoMatches): String = when {
    state.query.isNotEmpty() -> stringResource(R.string.favorites_no_matches_query, state.query)
    state.category != null -> stringResource(R.string.favorites_no_matches_category, categoryLabel(state.category))
    else -> stringResource(R.string.favorites_no_matches_generic)
}

private fun previewItem(category: PokeCategory, id: Int, name: String): FavoriteItemUi = Favorite(
    userId = UserId(1),
    key = EntryKey(category, requireNotNull(EntryId.of(id).getOrNull())),
    snapshot = FavoriteSnapshot(name = name, imageUrl = null),
    addedAt = Instant.EPOCH,
).toItemUi()

@Composable
private fun FavoritesPreview(state: FavoritesUiState, filters: FavoritesFilters = FavoritesFilters()) {
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            FavoritesScreen(
                filters = filters,
                state = state,
                onQueryChange = {},
                onCategorySelect = {},
                onClearFilters = {},
                onItemClick = {},
                onRemoveFavorite = {},
                onExploreClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun FavoritesLoadingPreview() = FavoritesPreview(FavoritesUiState.Loading)

@PreviewLightDark
@Composable
private fun FavoritesEmptyPreview() = FavoritesPreview(FavoritesUiState.Empty)

@PreviewLightDark
@Composable
private fun FavoritesNoMatchesPreview() = FavoritesPreview(
    state = FavoritesUiState.NoMatches(query = "mewtwo", category = null),
    filters = FavoritesFilters(query = "mewtwo"),
)

@PreviewLightDark
@Composable
private fun FavoritesNoMatchesCategoryPreview() = FavoritesPreview(
    state = FavoritesUiState.NoMatches(query = "", category = PokeCategory.REGION),
    filters = FavoritesFilters(category = PokeCategory.REGION),
)

@PreviewLightDark
@Composable
private fun FavoritesContentPreview() = FavoritesPreview(
    FavoritesUiState.Content(
        listOf(
            previewItem(PokeCategory.POKEMON, 25, "pikachu"),
            previewItem(PokeCategory.POKEMON, 122, "mr-mime"),
            previewItem(PokeCategory.REGION, 1, "kanto"),
            previewItem(PokeCategory.GENERATION, 8, "generation-viii"),
        ),
    ),
)
