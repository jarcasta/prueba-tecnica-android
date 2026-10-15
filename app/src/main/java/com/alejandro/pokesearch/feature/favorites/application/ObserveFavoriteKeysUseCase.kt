package com.alejandro.pokesearch.feature.favorites.application

import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.feature.auth.application.ObserveSessionUseCase
import com.alejandro.pokesearch.feature.favorites.domain.FavoritesRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveFavoriteKeysUseCase @Inject constructor(
    private val observeSession: ObserveSessionUseCase,
    private val favoritesRepository: FavoritesRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Set<EntryKey>> = observeSession().flatMapLatest { user ->
        if (user == null) flowOf(emptySet()) else favoritesRepository.observeKeys(user.id)
    }
}
