package com.alejandro.pokesearch.feature.dashboard.adapter.`in`.ui

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
sealed interface DashboardUiState {
    data object Loading : DashboardUiState

    data class Content(
        val username: String,
        val favoriteCount: Int,
    ) : DashboardUiState
}

sealed interface DashboardEffect {
    data object NavigateToLogin : DashboardEffect

    data class ShowResetDone(val removedCount: Int) : DashboardEffect
}

enum class DashboardDialog { None, Logout, ResetData }

@Serializable
data object ExploreTab

@Serializable
data object FavoritesTab

enum class DashboardTab(val route: Any) {
    Explore(ExploreTab),
    Favorites(FavoritesTab),
}
