package com.alejandro.pokesearch.feature.detail.adapter.`in`.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.Outcome
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.detail.application.ObserveEntryDetailUseCase
import com.alejandro.pokesearch.feature.detail.application.RefreshEntryDetailUseCase
import com.alejandro.pokesearch.feature.detail.domain.EntryDetail
import com.alejandro.pokesearch.feature.favorites.application.ObserveFavoriteKeysUseCase
import com.alejandro.pokesearch.feature.favorites.application.ToggleFavoriteUseCase
import com.alejandro.pokesearch.feature.favorites.domain.FavoriteSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel internal constructor(
    route: Detail,
    observeEntryDetail: ObserveEntryDetailUseCase,
    private val refreshEntryDetail: RefreshEntryDetailUseCase,
    observeFavoriteKeys: ObserveFavoriteKeysUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        observeEntryDetail: ObserveEntryDetailUseCase,
        refreshEntryDetail: RefreshEntryDetailUseCase,
        observeFavoriteKeys: ObserveFavoriteKeysUseCase,
        toggleFavorite: ToggleFavoriteUseCase,
    ) : this(savedStateHandle.toRoute<Detail>(), observeEntryDetail, refreshEntryDetail, observeFavoriteKeys, toggleFavorite)

    private val key: EntryKey? = EntryId.of(route.id).getOrNull()?.let { EntryKey(route.category, it) }
    private val isSupported = route.category in SUPPORTED_CATEGORIES
    private val refreshState = MutableStateFlow<RefreshState>(RefreshState.Running)

    val uiState: StateFlow<DetailUiState> = when {
        key == null -> flowOf(DetailUiState.Error(DomainError.NotFound))
        !isSupported -> flowOf(DetailUiState.ComingSoon)
        else -> detailState(key, observeEntryDetail(key), observeFavoriteKeys())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), DetailUiState.Loading)

    init {
        refresh()
    }

    fun onRetry() = refresh()

    fun onToggleFavorite() {
        val detail = (uiState.value as? DetailUiState.Content)?.detail ?: return
        viewModelScope.launch {
            toggleFavorite(detail.key, FavoriteSnapshot(name = detail.name, imageUrl = detail.imageUrl))
        }
    }

    private fun refresh() {
        val key = key?.takeIf { isSupported } ?: return
        refreshState.value = RefreshState.Running
        viewModelScope.launch {
            refreshState.value = when (val result = refreshEntryDetail(key)) {
                is Outcome.Success -> RefreshState.Done
                is Outcome.Failure -> RefreshState.Failed(result.error)
            }
        }
    }

    private fun detailState(
        key: EntryKey,
        detail: Flow<EntryDetail?>,
        favoriteKeys: Flow<Set<EntryKey>>,
    ): Flow<DetailUiState> = combine(
        detail,
        refreshState,
        favoriteKeys.map { key in it }.distinctUntilChanged(),
    ) { cached, refresh, isFavorite ->
        when {
            cached != null -> DetailUiState.Content(cached, isFavorite, isStale = refresh is RefreshState.Failed)
            refresh is RefreshState.Failed -> DetailUiState.Error(refresh.error)
            else -> DetailUiState.Loading
        }
    }

    private sealed interface RefreshState {
        data object Running : RefreshState

        data object Done : RefreshState

        data class Failed(val error: DomainError) : RefreshState
    }

    private companion object {
        val SUPPORTED_CATEGORIES = setOf(PokeCategory.POKEMON)
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
