package com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.CategorySelector
import com.alejandro.pokesearch.core.designsystem.component.EmptyState
import com.alejandro.pokesearch.core.designsystem.component.ErrorState
import com.alejandro.pokesearch.core.designsystem.component.LoadingState
import com.alejandro.pokesearch.core.designsystem.component.LoadingVariant
import com.alejandro.pokesearch.core.designsystem.component.OfflineBanner
import com.alejandro.pokesearch.core.designsystem.component.PagingFooter
import com.alejandro.pokesearch.core.designsystem.component.ResultListItem
import com.alejandro.pokesearch.core.designsystem.component.ResultListItemPlaceholder
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.catalog.domain.CatalogEntry
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val CONTENT_TYPE_ENTRY = "entry"
private const val CONTENT_TYPE_HEADER = "header"
private const val CONTENT_TYPE_FOOTER = "footer"


@Composable
fun ExploreScreen(
    category: PokeCategory,
    items: LazyPagingItems<CatalogItemUi>,
    listState: ExploreListState,
    onCategorySelect: (PokeCategory) -> Unit,
    onItemClick: (CatalogItemUi) -> Unit,
    onToggleFavorite: (CatalogItemUi) -> Unit,
    onRetry: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    isOffline: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val scope = rememberCoroutineScope()
    val bottomPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())
    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
            CategorySelector(
                selected = category,
                onSelect = { selected ->
                    if (selected != null && selected != category) {
                        onCategorySelect(selected)
                        scope.launch { lazyListState.scrollToItem(0) }
                    }
                },
            )
            when (listState) {
                ExploreListState.Loading -> LoadingState(variant = LoadingVariant.List, modifier = Modifier.fillMaxSize())
                is ExploreListState.Error -> ErrorState(
                    title = errorTitle(category),
                    message = errorMessage(listState.error),
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = bottomPadding,
                )
                ExploreListState.Empty -> EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = stringResource(R.string.explore_empty_title),
                    message = stringResource(R.string.explore_empty_message),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = bottomPadding,
                )
                is ExploreListState.Content -> Column {
                    // Stored entries are still useful offline; the banner says they may be old.
                    OfflineBanner(visible = isOffline)
                    CatalogList(
                        category = category,
                        items = items,
                        content = listState,
                        onItemClick = onItemClick,
                        onToggleFavorite = onToggleFavorite,
                        onRetry = onRetry,
                        lazyListState = lazyListState,
                        contentPadding = bottomPadding,
                    )
                }
            }
        }
        BackToTopButton(
            lazyListState = lazyListState,
            onClick = { scope.launch { lazyListState.animateScrollToItem(0) } },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottomPadding)
                .padding(Spacing.md),
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottomPadding),
        )
    }
}

@Composable
private fun CatalogList(
    category: PokeCategory,
    items: LazyPagingItems<CatalogItemUi>,
    content: ExploreListState.Content,
    onItemClick: (CatalogItemUi) -> Unit,
    onToggleFavorite: (CatalogItemUi) -> Unit,
    onRetry: () -> Unit,
    lazyListState: LazyListState,
    contentPadding: PaddingValues,
) {
    LazyColumn(state = lazyListState, contentPadding = contentPadding, modifier = Modifier.fillMaxSize()) {
        content.totalCount?.let { count ->
            item(key = CONTENT_TYPE_HEADER, contentType = CONTENT_TYPE_HEADER) {
                ResultCount(text = countLabel(category, count))
            }
        }
        items(
            count = items.itemCount,
            key = items.itemKey { it.listKey },
            contentType = items.itemContentType { CONTENT_TYPE_ENTRY },
        ) { index ->
            val item = items[index]
            if (item == null) {
                ResultListItemPlaceholder()
            } else {
                ResultListItem(
                    name = item.name,
                    number = item.number,
                    numberDescription = stringResource(R.string.entry_number_description, item.key.id.value),
                    leading = item.leading,
                    isFavorite = item.isFavorite,
                    onToggleFavorite = { onToggleFavorite(item) },
                    onClick = { onItemClick(item) },
                )
            }
        }
        if (content.footer != PagingFooterState.None) {
            item(key = CONTENT_TYPE_FOOTER, contentType = CONTENT_TYPE_FOOTER) {
                PagingFooter(isError = content.footer == PagingFooterState.Error, onRetry = onRetry)
            }
        }
    }
}

@Composable
private fun ResultCount(text: String) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.fillMaxWidth().height(36.dp).padding(horizontal = Spacing.md),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BackToTopButton(lazyListState: LazyListState, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val visible by remember { derivedStateOf { lazyListState.firstVisibleItemIndex > 0 } }
    AnimatedVisibility(visible = visible, enter = scaleIn(), exit = scaleOut(), modifier = modifier) {
        SmallFloatingActionButton(onClick = onClick) {
            Icon(imageVector = Icons.Rounded.ArrowUpward, contentDescription = stringResource(R.string.back_to_top))
        }
    }
}

@Composable
private fun countLabel(category: PokeCategory, count: Int): String = pluralStringResource(
    when (category) {
        PokeCategory.POKEMON -> R.plurals.catalog_count_pokemon
        PokeCategory.ITEM_CATEGORY -> R.plurals.catalog_count_item_category
        PokeCategory.REGION -> R.plurals.catalog_count_region
        PokeCategory.GENERATION -> R.plurals.catalog_count_generation
    },
    count,
    count,
)

@Composable
private fun errorTitle(category: PokeCategory): String = stringResource(
    when (category) {
        PokeCategory.POKEMON -> R.string.explore_error_title_pokemon
        PokeCategory.ITEM_CATEGORY -> R.string.explore_error_title_item_category
        PokeCategory.REGION -> R.string.explore_error_title_region
        PokeCategory.GENERATION -> R.string.explore_error_title_generation
    },
)

@Composable
private fun errorMessage(error: DomainError): String = when (error) {
    DomainError.NoConnection -> stringResource(R.string.explore_error_no_connection)
    DomainError.NotFound -> stringResource(R.string.explore_error_not_found)
    is DomainError.Server -> stringResource(R.string.error_server, error.code)
    DomainError.Unexpected -> stringResource(R.string.error_unexpected)
}

private fun previewEntry(category: PokeCategory, id: Int, name: String, favorite: Boolean = false): CatalogItemUi {
    val key = EntryKey(category, requireNotNull(EntryId.of(id).getOrNull()))
    return CatalogEntry(key = key, name = name, imageUrl = null).toItemUi(isFavorite = favorite)
}

private val previewPokemon = listOf(
    previewEntry(PokeCategory.POKEMON, 1, "bulbasaur"),
    previewEntry(PokeCategory.POKEMON, 6, "charizard", favorite = true),
    previewEntry(PokeCategory.POKEMON, 25, "pikachu", favorite = true),
    previewEntry(PokeCategory.POKEMON, 122, "mr-mime"),
)

private val previewGenerations = listOf(
    previewEntry(PokeCategory.GENERATION, 1, "generation-i"),
    previewEntry(PokeCategory.GENERATION, 8, "generation-viii", favorite = true),
    previewEntry(PokeCategory.GENERATION, 9, "generation-ix"),
)

@Composable
private fun ExplorePreview(
    listState: ExploreListState,
    category: PokeCategory = PokeCategory.POKEMON,
    entries: List<CatalogItemUi> = emptyList(),
    isOffline: Boolean = false,
) {
    val notLoading = LoadState.NotLoading(endOfPaginationReached = false)
    val items = flowOf(
        PagingData.from(entries, sourceLoadStates = LoadStates(notLoading, notLoading, notLoading)),
    ).collectAsLazyPagingItems()
    PokeSearchTheme(dynamicColor = false) {
        Surface {
            ExploreScreen(
                category = category,
                items = items,
                listState = listState,
                onCategorySelect = {},
                onItemClick = {},
                onToggleFavorite = {},
                onRetry = {},
                snackbarHostState = SnackbarHostState(),
                isOffline = isOffline,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ExploreLoadingPreview() = ExplorePreview(listState = ExploreListState.Loading)

@PreviewLightDark
@Composable
private fun ExploreErrorPreview() = ExplorePreview(
    listState = ExploreListState.Error(DomainError.NoConnection),
    category = PokeCategory.REGION,
)

@PreviewLightDark
@Composable
private fun ExploreEmptyPreview() = ExplorePreview(listState = ExploreListState.Empty)

@PreviewLightDark
@Composable
private fun ExploreContentPreview() = ExplorePreview(
    listState = ExploreListState.Content(totalCount = null, footer = PagingFooterState.Loading, staleDataError = null),
    entries = previewPokemon,
)

@PreviewLightDark
@Composable
private fun ExplorePagingErrorPreview() = ExplorePreview(
    listState = ExploreListState.Content(totalCount = null, footer = PagingFooterState.Error, staleDataError = null),
    entries = previewPokemon,
)

@PreviewLightDark
@Composable
private fun ExploreCompleteWithCountPreview() = ExplorePreview(
    listState = ExploreListState.Content(totalCount = 3, footer = PagingFooterState.None, staleDataError = null),
    category = PokeCategory.GENERATION,
    entries = previewGenerations,
)

@PreviewLightDark
@Composable
private fun ExploreOfflinePreview() = ExplorePreview(
    listState = ExploreListState.Content(totalCount = null, footer = PagingFooterState.None, staleDataError = DomainError.NoConnection),
    entries = previewPokemon,
    isOffline = true,
)
