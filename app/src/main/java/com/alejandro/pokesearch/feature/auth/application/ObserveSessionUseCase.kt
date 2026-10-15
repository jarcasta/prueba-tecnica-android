package com.alejandro.pokesearch.feature.auth.application

import com.alejandro.pokesearch.feature.auth.domain.SessionRepository
import com.alejandro.pokesearch.feature.auth.domain.User
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class ObserveSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke(): Flow<User?> = sessionRepository.observeActiveUser().distinctUntilChanged()
}
