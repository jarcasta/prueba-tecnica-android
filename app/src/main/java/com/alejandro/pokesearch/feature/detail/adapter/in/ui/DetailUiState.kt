package com.alejandro.pokesearch.feature.detail.adapter.`in`.ui

import androidx.compose.runtime.Immutable
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.feature.detail.domain.EntryDetail

@Immutable
sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Error(val error: DomainError) : DetailUiState

    data class Content(
        val detail: EntryDetail,
        val isFavorite: Boolean,
        val isStale: Boolean,
        val isOffline: Boolean = false,
    ) : DetailUiState
}

sealed interface DetailEffect {
    data object ShowStaleData : DetailEffect
}
