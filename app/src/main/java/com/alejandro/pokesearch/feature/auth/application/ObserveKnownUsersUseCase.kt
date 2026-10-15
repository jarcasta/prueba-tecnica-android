package com.alejandro.pokesearch.feature.auth.application

import com.alejandro.pokesearch.feature.auth.domain.FavoriteCountReader
import com.alejandro.pokesearch.feature.auth.domain.KnownUser
import com.alejandro.pokesearch.feature.auth.domain.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveKnownUsersUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val favoriteCountReader: FavoriteCountReader,
) {
    operator fun invoke(): Flow<List<KnownUser>> =
        combine(userRepository.observeAll(), favoriteCountReader.observeCountsByUser()) { users, counts ->
            users
                .sortedByDescending { it.lastLoginAt }
                .map { KnownUser(user = it, favoriteCount = counts[it.id] ?: 0) }
        }
}
