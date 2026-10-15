package com.alejandro.pokesearch.feature.auth.adapter.`in`.ui

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.favoriteCountLabel
import com.alejandro.pokesearch.feature.auth.domain.Username
import com.alejandro.pokesearch.feature.auth.domain.UsernameError
import java.time.Duration
import java.time.Instant


private const val INITIALS_LENGTH = 2

fun initialsOf(username: String): String = username.take(INITIALS_LENGTH).uppercase()

@Composable
fun knownUserSupportingText(user: KnownUserUi, now: Instant): String =
    stringResource(
        R.string.login_known_user_supporting,
        favoriteCountLabel(user.favoriteCount),
        lastAccessLabel(user.lastLoginAt, now),
    )

@Composable
private fun lastAccessLabel(lastLoginAt: Instant, now: Instant): String =
    if (Duration.between(lastLoginAt, now) < Duration.ofMinutes(1)) {
        stringResource(R.string.login_last_access_just_now)
    } else {
        DateUtils.getRelativeTimeSpanString(
            lastLoginAt.toEpochMilli(),
            now.toEpochMilli(),
            DateUtils.MINUTE_IN_MILLIS,
        ).toString()
    }

@Composable
fun usernameErrorText(error: UsernameError): String = when (error) {
    UsernameError.TooShort -> pluralStringResource(R.plurals.login_error_too_short, Username.MIN_LENGTH, Username.MIN_LENGTH)
    UsernameError.TooLong -> pluralStringResource(R.plurals.login_error_too_long, Username.MAX_LENGTH, Username.MAX_LENGTH)
    is UsernameError.InvalidCharacter ->
        stringResource(R.string.login_error_invalid_character, error.character.toString())
}

@Composable
fun usernameSupportingText(state: LoginUiState): String = when {
    state.inputError != null -> usernameErrorText(state.inputError)
    state.matchedUser != null && state.matchedUser.favoriteCount > 0 -> pluralStringResource(
        R.plurals.login_username_existing_count,
        state.matchedUser.favoriteCount,
        state.matchedUser.favoriteCount,
    )
    state.matchedUser != null -> stringResource(R.string.login_username_existing)
    state.isInputValid -> stringResource(R.string.login_username_new)
    else -> pluralStringResource(R.plurals.login_username_hint, Username.MAX_LENGTH, Username.MIN_LENGTH, Username.MAX_LENGTH)
}
