package com.alejandro.pokesearch.feature.favorites.adapter.out.room

import com.alejandro.pokesearch.feature.auth.domain.FavoriteCountReader
import com.alejandro.pokesearch.feature.auth.domain.UserId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFavoriteCountReader @Inject constructor(
    private val dao: FavoriteDao,
) : FavoriteCountReader {
    override fun observeCountsByUser(): Flow<Map<UserId, Int>> =
        dao.observeCountsByUser().map { rows -> rows.associate { UserId(it.userId) to it.favoriteCount } }
}
