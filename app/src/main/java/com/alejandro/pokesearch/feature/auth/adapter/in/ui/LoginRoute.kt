package com.alejandro.pokesearch.feature.auth.adapter.`in`.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alejandro.pokesearch.R

@Composable
fun LoginRoute(
    onLoggedIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loginErrorMessage = stringResource(R.string.login_error_generic)
    val currentOnLoggedIn by rememberUpdatedState(onLoggedIn)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToDashboard -> currentOnLoggedIn()
                LoginEffect.ShowLoginError -> snackbarHostState.showSnackbar(loginErrorMessage)
            }
        }
    }

    LoginScreen(
        state = state,
        onInputChange = viewModel::onInputChange,
        onInputBlur = viewModel::onInputBlur,
        onSubmit = viewModel::onSubmit,
        onKnownUserClick = viewModel::onKnownUserClick,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}
