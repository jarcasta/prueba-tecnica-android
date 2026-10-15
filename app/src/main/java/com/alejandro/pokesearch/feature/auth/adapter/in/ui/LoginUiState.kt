package com.alejandro.pokesearch.feature.auth.adapter.`in`.ui

import androidx.compose.runtime.Immutable
import com.alejandro.pokesearch.feature.auth.domain.UsernameError
import java.time.Instant

@Immutable
data class LoginUiState(
    val isLoading: Boolean = true,
    val input: String = "",
    val inputError: UsernameError? = null,
    val isInputValid: Boolean = false,
    val matchedUser: KnownUserUi? = null,
    val isSubmitting: Boolean = false,
    val restoringUsername: String? = null,
    val knownUsers: List<KnownUserUi> = emptyList(),
    val now: Instant = Instant.EPOCH,
) {
    val isBusy: Boolean get() = isSubmitting || restoringUsername != null

    val canSubmit: Boolean get() = isInputValid && !isBusy
}

@Immutable
data class KnownUserUi(
    val username: String,
    val favoriteCount: Int,
    val lastLoginAt: Instant,
)

sealed interface LoginEffect {
    data object NavigateToDashboard : LoginEffect

    data object ShowLoginError : LoginEffect
}
