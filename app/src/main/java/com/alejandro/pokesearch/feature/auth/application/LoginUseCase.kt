package com.alejandro.pokesearch.feature.auth.application

import com.alejandro.pokesearch.core.domain.Clock
import com.alejandro.pokesearch.feature.auth.domain.SessionRepository
import com.alejandro.pokesearch.feature.auth.domain.User
import com.alejandro.pokesearch.feature.auth.domain.UserRepository
import com.alejandro.pokesearch.feature.auth.domain.Username
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(username: Username): User {
        val now = clock.now()
        val user = userRepository.findByUsername(username)
            ?.recordLogin(now)
            ?.also { userRepository.update(it) }
            ?: userRepository.create(username, now)
        sessionRepository.open(user.id)
        return user
    }
}
