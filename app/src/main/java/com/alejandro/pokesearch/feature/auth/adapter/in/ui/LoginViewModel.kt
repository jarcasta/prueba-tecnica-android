package com.alejandro.pokesearch.feature.auth.adapter.`in`.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandro.pokesearch.core.domain.Outcome
import com.alejandro.pokesearch.feature.auth.application.LoginUseCase
import com.alejandro.pokesearch.feature.auth.application.ObserveKnownUsersUseCase
import com.alejandro.pokesearch.feature.auth.domain.Clock
import com.alejandro.pokesearch.feature.auth.domain.Username
import com.alejandro.pokesearch.feature.auth.domain.UsernameError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    observeKnownUsers: ObserveKnownUsersUseCase,
    private val login: LoginUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val input = savedStateHandle.getStateFlow(KEY_INPUT, "")
    private val inputBlurred = MutableStateFlow(false)
    private val submission = MutableStateFlow<Submission>(Submission.Idle)

    private val knownUsers: Flow<List<KnownUserUi>> = observeKnownUsers().map { users ->
        users.map { KnownUserUi(it.user.username.value, it.favoriteCount, it.user.lastLoginAt) }
    }

    private val effectChannel = Channel<LoginEffect>(Channel.BUFFERED)
    val effects: Flow<LoginEffect> = effectChannel.receiveAsFlow()

    val uiState: StateFlow<LoginUiState> =
        combine(input, inputBlurred, submission, knownUsers) { input, blurred, submission, users ->
            buildState(input, blurred, submission, users)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), LoginUiState())

    fun onInputChange(value: String) {
        savedStateHandle[KEY_INPUT] = value
        inputBlurred.value = false
    }

    fun onInputBlur() {
        inputBlurred.value = true
    }

    fun onSubmit() {
        when (val username = Username.of(input.value)) {
            is Outcome.Success -> startLogin(username.value, Submission.Form)
            is Outcome.Failure -> inputBlurred.value = true
        }
    }

    fun onKnownUserClick(username: String) {
        val validUsername = (Username.of(username) as? Outcome.Success)?.value ?: return
        startLogin(validUsername, Submission.KnownUser(username))
    }

    private fun startLogin(username: Username, kind: Submission) {
        if (!submission.compareAndSet(Submission.Idle, kind)) return
        viewModelScope.launch {
            try {
                login(username)
                effectChannel.send(LoginEffect.NavigateToDashboard)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                submission.value = Submission.Idle
                effectChannel.send(LoginEffect.ShowLoginError)
            }
        }
    }

    private fun buildState(
        input: String,
        blurred: Boolean,
        submission: Submission,
        users: List<KnownUserUi>,
    ): LoginUiState {
        val validation = Username.of(input)
        val username = (validation as? Outcome.Success)?.value
        val error = (validation as? Outcome.Failure)?.error
        return LoginUiState(
            isLoading = false,
            input = input,
            inputError = error?.takeIf { input.isNotEmpty() && (it !is UsernameError.TooShort || blurred) },
            isInputValid = username != null,
            matchedUser = username?.let { valid -> users.firstOrNull { it.username == valid.value } },
            isSubmitting = submission is Submission.Form,
            restoringUsername = (submission as? Submission.KnownUser)?.username,
            knownUsers = users,
            now = clock.now(),
        )
    }

    private sealed interface Submission {
        data object Idle : Submission

        data object Form : Submission

        data class KnownUser(val username: String) : Submission
    }

    private companion object {
        const val KEY_INPUT = "login_input"
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
