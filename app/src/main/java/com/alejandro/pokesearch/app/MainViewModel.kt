package com.alejandro.pokesearch.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandro.pokesearch.feature.auth.application.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

enum class StartDestination { Loading, Login, Dashboard }

/**
 * Se determina el estado de la sesión para saber que pantalla se
 * debe de presentar al usuario.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    observeSession: ObserveSessionUseCase,
) : ViewModel() {
    val startDestination: StateFlow<StartDestination> = flow {
        val activeUser = observeSession().first()
        emit(if (activeUser != null) StartDestination.Dashboard else StartDestination.Login)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, StartDestination.Loading)
}
