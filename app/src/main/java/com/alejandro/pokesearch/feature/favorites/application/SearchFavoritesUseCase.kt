package com.alejandro.pokesearch.feature.favorites.application

import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.feature.auth.application.ObserveSessionUseCase
import com.alejandro.pokesearch.feature.favorites.domain.Favorite
import com.alejandro.pokesearch.feature.favorites.domain.FavoritesRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class SearchFavoritesUseCase @Inject constructor(
    private val observeSession: ObserveSessionUseCase,
    private val favoritesRepository: FavoritesRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(query: String, category: PokeCategory?): Flow<List<Favorite>> =
        observeSession().flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else favoritesRepository.search(user.id, query.trim(), category)
        }
}
