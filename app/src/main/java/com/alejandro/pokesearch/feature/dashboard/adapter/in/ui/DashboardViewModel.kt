package com.alejandro.pokesearch.feature.dashboard.adapter.`in`.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandro.pokesearch.feature.auth.application.LogoutUseCase
import com.alejandro.pokesearch.feature.auth.application.ObserveSessionUseCase
import com.alejandro.pokesearch.feature.favorites.application.ObserveFavoriteKeysUseCase
import com.alejandro.pokesearch.feature.favorites.application.ResetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeSession: ObserveSessionUseCase,
    observeFavoriteKeys: ObserveFavoriteKeysUseCase,
    private val logout: LogoutUseCase,
    private val resetUserData: ResetUserDataUseCase,
) : ViewModel() {

    private val effectChannel = Channel<DashboardEffect>(Channel.BUFFERED)
    val effects: Flow<DashboardEffect> = effectChannel.receiveAsFlow()

    private val loggingOut = AtomicBoolean(false)


    val uiState: StateFlow<DashboardUiState> =
        combine(observeSession().filterNotNull(), observeFavoriteKeys()) { user, favoriteKeys ->
            DashboardUiState.Content(username = user.username.value, favoriteCount = favoriteKeys.size)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), DashboardUiState.Loading)

    fun onLogoutConfirm() {
        if (!loggingOut.compareAndSet(false, true)) return
        viewModelScope.launch {
            logout()
            effectChannel.send(DashboardEffect.NavigateToLogin)
        }
    }

    fun onResetDataConfirm() {
        viewModelScope.launch {
            val removedCount = resetUserData()
            effectChannel.send(DashboardEffect.ShowResetDone(removedCount))
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
