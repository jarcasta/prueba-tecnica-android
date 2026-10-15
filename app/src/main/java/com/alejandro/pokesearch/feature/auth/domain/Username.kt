package com.alejandro.pokesearch.feature.auth.domain

import com.alejandro.pokesearch.core.domain.Outcome

@JvmInline
value class Username private constructor(val value: String) {
    companion object {
        const val MIN_LENGTH = 3
        const val MAX_LENGTH = 20

        private val allowedCharacter = Regex("[a-z0-9]")

        fun of(raw: String): Outcome<Username, UsernameError> {
            val normalized = raw.lowercase()
            val invalidCharacter = normalized.firstOrNull { !allowedCharacter.matches(it.toString()) }
            return when {
                invalidCharacter != null -> Outcome.Failure(UsernameError.InvalidCharacter(invalidCharacter))
                normalized.length < MIN_LENGTH -> Outcome.Failure(UsernameError.TooShort)
                normalized.length > MAX_LENGTH -> Outcome.Failure(UsernameError.TooLong)
                else -> Outcome.Success(Username(normalized))
            }
        }
    }
}

sealed interface UsernameError {
    data object TooShort : UsernameError

    data object TooLong : UsernameError

    data class InvalidCharacter(val character: Char) : UsernameError
}
