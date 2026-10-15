package com.alejandro.pokesearch.feature.favorites.application

import com.alejandro.pokesearch.feature.auth.application.ObserveSessionUseCase
import com.alejandro.pokesearch.feature.favorites.domain.FavoritesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class ResetUserDataUseCase @Inject constructor(
    private val observeSession: ObserveSessionUseCase,
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(): Int {
        val user = observeSession().first() ?: return 0
        return favoritesRepository.removeAll(user.id)
    }
}
