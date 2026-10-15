package com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.alejandro.pokesearch.core.domain.NetworkMonitor
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.reconnections
import com.alejandro.pokesearch.feature.catalog.application.ObserveCatalogUseCase
import com.alejandro.pokesearch.feature.catalog.domain.CatalogEntry
import com.alejandro.pokesearch.feature.favorites.application.ObserveFavoriteKeysUseCase
import com.alejandro.pokesearch.feature.favorites.application.ToggleFavoriteUseCase
import com.alejandro.pokesearch.feature.favorites.domain.FavoriteSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
data class ExploreUiState(
    val category: PokeCategory,
    val isOffline: Boolean = false,
)

sealed interface ExploreEffect {
    data object RetryFailedLoads : ExploreEffect
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    observeCatalog: ObserveCatalogUseCase,
    observeFavoriteKeys: ObserveFavoriteKeysUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val category: StateFlow<PokeCategory> = savedStateHandle.getStateFlow(KEY_CATEGORY, PokeCategory.POKEMON)

    private val isOnline: StateFlow<Boolean> = networkMonitor.isOnline.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val hasLoadError = MutableStateFlow(false)

    private val effectChannel = Channel<ExploreEffect>(Channel.BUFFERED)
    val effects: Flow<ExploreEffect> = effectChannel.receiveAsFlow()

    val uiState: StateFlow<ExploreUiState> =
        combine(category, isOnline) { category, online -> ExploreUiState(category, isOffline = !online) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, ExploreUiState(category.value))

    init {
        viewModelScope.launch {
            isOnline.reconnections().collect {
                if (hasLoadError.value) effectChannel.send(ExploreEffect.RetryFailedLoads)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val entries: Flow<PagingData<CatalogEntry>> = category
        .flatMapLatest { observeCatalog(it) }
        .cachedIn(viewModelScope)

    val catalog: Flow<PagingData<CatalogItemUi>> =
        combine(entries, observeFavoriteKeys()) { page, favoriteKeys ->
            page.map { entry -> entry.toItemUi(isFavorite = entry.key in favoriteKeys) }
        }

    fun onCategorySelect(category: PokeCategory) {
        savedStateHandle[KEY_CATEGORY] = category
    }

    fun onLoadErrorChange(hasError: Boolean) {
        hasLoadError.value = hasError
    }

    fun onToggleFavorite(item: CatalogItemUi) {
        viewModelScope.launch {
            toggleFavorite(item.key, FavoriteSnapshot(name = item.rawName, imageUrl = item.imageUrl))
        }
    }

    private companion object {
        const val KEY_CATEGORY = "explore_category"
    }
}
