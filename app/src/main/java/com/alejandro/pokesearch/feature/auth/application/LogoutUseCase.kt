package com.alejandro.pokesearch.feature.auth.application

import com.alejandro.pokesearch.feature.auth.domain.SessionRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke() = sessionRepository.close()
}
