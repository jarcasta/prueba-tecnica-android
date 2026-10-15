package com.alejandro.pokesearch.feature.auth.adapter.out

import com.alejandro.pokesearch.feature.auth.domain.FavoriteCountReader
import com.alejandro.pokesearch.feature.auth.domain.UserId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NoFavoritesCountReader @Inject constructor() : FavoriteCountReader {
    override fun observeCountsByUser(): Flow<Map<UserId, Int>> = flowOf(emptyMap())
}
