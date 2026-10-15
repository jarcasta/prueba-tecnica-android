package com.alejandro.pokesearch.feature.favorites.application

import com.alejandro.pokesearch.feature.auth.application.ObserveSessionUseCase
import com.alejandro.pokesearch.feature.favorites.domain.Favorite
import com.alejandro.pokesearch.feature.favorites.domain.FavoritesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class RestoreFavoriteUseCase @Inject constructor(
    private val observeSession: ObserveSessionUseCase,
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(favorite: Favorite) {
        val user = observeSession().first() ?: return
        if (user.id == favorite.userId) favoritesRepository.add(favorite)
    }
}
