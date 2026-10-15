package com.alejandro.pokesearch.feature.favorites.adapter.`in`.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.feature.favorites.application.ObserveFavoriteKeysUseCase
import com.alejandro.pokesearch.feature.favorites.application.RestoreFavoriteUseCase
import com.alejandro.pokesearch.feature.favorites.application.SearchFavoritesUseCase
import com.alejandro.pokesearch.feature.favorites.application.ToggleFavoriteResult
import com.alejandro.pokesearch.feature.favorites.application.ToggleFavoriteUseCase
import com.alejandro.pokesearch.feature.favorites.domain.Favorite
import com.alejandro.pokesearch.feature.favorites.domain.FavoriteSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    searchFavorites: SearchFavoritesUseCase,
    observeFavoriteKeys: ObserveFavoriteKeysUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val restoreFavorite: RestoreFavoriteUseCase,
) : ViewModel() {

    private val query = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val category = savedStateHandle.getStateFlow<PokeCategory?>(KEY_CATEGORY, null)

    private val effectChannel = Channel<FavoritesEffect>(Channel.BUFFERED)
    val effects: Flow<FavoritesEffect> = effectChannel.receiveAsFlow()

    val filters: StateFlow<FavoritesFilters> = combine(query, category, ::FavoritesFilters)
        .stateIn(viewModelScope, SharingStarted.Eagerly, FavoritesFilters(query.value, category.value))

    private val hasAnyFavorite: Flow<Boolean> = observeFavoriteKeys().map { it.isNotEmpty() }.distinctUntilChanged()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FavoritesUiState> =
        combine(query.debounce(SEARCH_DEBOUNCE_MILLIS), category, ::FavoritesFilters)
            .flatMapLatest { filters ->
                combine(searchFavorites(filters.query, filters.category), hasAnyFavorite) { favorites, hasAny ->
                    buildState(filters, favorites, hasAny)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), FavoritesUiState.Loading)

    fun onQueryChange(value: String) {
        savedStateHandle[KEY_QUERY] = value
    }

    fun onCategorySelect(value: PokeCategory?) {
        savedStateHandle[KEY_CATEGORY] = value
    }

    fun onClearFilters() {
        onQueryChange("")
        onCategorySelect(null)
    }

    fun onRemoveFavorite(item: FavoriteItemUi) {
        viewModelScope.launch {
            val result = toggleFavorite(item.key, FavoriteSnapshot(name = item.rawName, imageUrl = item.imageUrl))
            if (result is ToggleFavoriteResult.Removed) effectChannel.send(FavoritesEffect.ShowUndo(result.favorite))
        }
    }

    fun onUndoRemove(favorite: Favorite) {
        viewModelScope.launch { restoreFavorite(favorite) }
    }

    private fun buildState(filters: FavoritesFilters, favorites: List<Favorite>, hasAnyFavorite: Boolean): FavoritesUiState =
        when {
            !hasAnyFavorite -> FavoritesUiState.Empty
            favorites.isEmpty() -> FavoritesUiState.NoMatches(filters.query.trim(), filters.category)
            else -> FavoritesUiState.Content(favorites.map { it.toItemUi() })
        }

    private companion object {
        const val KEY_QUERY = "favorites_query"
        const val KEY_CATEGORY = "favorites_category"
        const val SEARCH_DEBOUNCE_MILLIS = 200L
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
