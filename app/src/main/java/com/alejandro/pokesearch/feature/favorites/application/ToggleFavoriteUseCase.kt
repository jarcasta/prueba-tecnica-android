package com.alejandro.pokesearch.feature.favorites.application

import com.alejandro.pokesearch.core.domain.Clock
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.feature.auth.application.ObserveSessionUseCase
import com.alejandro.pokesearch.feature.favorites.domain.Favorite
import com.alejandro.pokesearch.feature.favorites.domain.FavoriteSnapshot
import com.alejandro.pokesearch.feature.favorites.domain.FavoritesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

sealed interface ToggleFavoriteResult {
    data object Added : ToggleFavoriteResult

    data class Removed(val favorite: Favorite) : ToggleFavoriteResult

    data object NoActiveSession : ToggleFavoriteResult
}

class ToggleFavoriteUseCase @Inject constructor(
    private val observeSession: ObserveSessionUseCase,
    private val favoritesRepository: FavoritesRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(key: EntryKey, snapshot: FavoriteSnapshot): ToggleFavoriteResult {
        val user = observeSession().first() ?: return ToggleFavoriteResult.NoActiveSession
        val existing = favoritesRepository.find(user.id, key)
        return if (existing != null) {
            favoritesRepository.remove(user.id, key)
            ToggleFavoriteResult.Removed(existing)
        } else {
            favoritesRepository.add(Favorite(userId = user.id, key = key, snapshot = snapshot, addedAt = clock.now()))
            ToggleFavoriteResult.Added
        }
    }
}
