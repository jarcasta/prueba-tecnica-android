package com.alejandro.pokesearch.feature.auth.adapter.`in`.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.KnownUserItem
import com.alejandro.pokesearch.core.designsystem.component.Wordmark
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.PokeTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing
import com.alejandro.pokesearch.feature.auth.domain.Username
import com.alejandro.pokesearch.feature.auth.domain.UsernameError
import java.time.Duration
import java.time.Instant

private const val VISIBLE_KNOWN_USERS = 3

@Composable
fun LoginScreen(
    state: LoginUiState,
    onInputChange: (String) -> Unit,
    onInputBlur: () -> Unit,
    onSubmit: () -> Unit,
    onKnownUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { padding ->
        if (state.isLoading) return@Scaffold
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding(),
        ) {
            val form: @Composable () -> Unit = {
                UsernameForm(
                    state = state,
                    onInputChange = onInputChange,
                    onInputBlur = onInputBlur,
                    onSubmit = onSubmit,
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = Spacing.xl),
            ) {
                if (state.knownUsers.isEmpty()) {
                    FirstUseContent(form)
                } else {
                    ReturningContent(state = state, onKnownUserClick = onKnownUserClick, form = form)
                    Footer(Modifier.padding(vertical = Spacing.md))
                }
            }
        }
    }
}


@Composable
private fun FirstUseContent(form: @Composable () -> Unit) {
    Spacer(Modifier.height(0.dp))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(64.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Rounded.TravelExplore, contentDescription = null, modifier = Modifier.size(32.dp))
            }
        }
        Wordmark(style = MaterialTheme.typography.displaySmall)
        Text(
            text = stringResource(R.string.login_subtitle_first_use),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        modifier = Modifier.padding(top = Spacing.xxl, bottom = Spacing.xl),
    ) {
        form()
        Footer()
    }
}

@Composable
private fun ReturningContent(
    state: LoginUiState,
    onKnownUserClick: (String) -> Unit,
    form: @Composable () -> Unit,
) {
    var showAll by rememberSaveable { mutableStateOf(false) }
    val visibleUsers = if (showAll) state.knownUsers else state.knownUsers.take(VISIBLE_KNOWN_USERS)

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        modifier = Modifier.padding(top = Spacing.xxl),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Wordmark(style = MaterialTheme.typography.displaySmall)
            Text(
                text = stringResource(R.string.login_subtitle_returning),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = stringResource(R.string.login_continue_as),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Column {
                    visibleUsers.forEach { user ->
                        key(user.username) {
                            KnownUserItem(
                                initials = initialsOf(user.username),
                                userName = user.username,
                                supportingText = knownUserSupportingText(user, state.now),
                                isLoading = state.restoringUsername == user.username,
                                onClick = { onKnownUserClick(user.username) },
                            )
                        }
                    }
                }
            }
            if (state.knownUsers.size > VISIBLE_KNOWN_USERS) {
                TextButton(onClick = { showAll = !showAll }, modifier = Modifier.align(Alignment.End)) {
                    Text(stringResource(if (showAll) R.string.login_see_less else R.string.login_see_all))
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            HorizontalDivider(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.login_divider),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider(Modifier.weight(1f))
        }
        form()
    }
}

@Composable
private fun UsernameForm(
    state: LoginUiState,
    onInputChange: (String) -> Unit,
    onInputBlur: () -> Unit,
    onSubmit: () -> Unit,
) {
    var hadFocus by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        OutlinedTextField(
            value = state.input,
            onValueChange = onInputChange,
            label = { Text(stringResource(R.string.login_username_label)) },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Person, contentDescription = null) },
            trailingIcon = when {
                state.inputError != null -> {
                    { Icon(imageVector = Icons.Rounded.Error, contentDescription = null) }
                }
                state.matchedUser != null -> {
                    {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = PokeTheme.extendedColors.success,
                        )
                    }
                }
                else -> null
            },
            supportingText = {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        if (state.matchedUser != null && state.inputError == null) {
                            Icon(imageVector = Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(Spacing.xxs))
                        }
                        Text(usernameSupportingText(state))
                    }
                    Text(stringResource(R.string.login_username_counter, state.input.length, Username.MAX_LENGTH))
                }
            },
            isError = state.inputError != null,
            enabled = !state.isBusy,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focus ->
                    if (hadFocus && !focus.isFocused) onInputBlur()
                    hadFocus = focus.isFocused
                },
        )
        SubmitButton(state = state, onSubmit = onSubmit)
    }
}

@Composable
private fun SubmitButton(state: LoginUiState, onSubmit: () -> Unit) {
    Button(
        onClick = { if (!state.isBusy) onSubmit() },
        enabled = state.canSubmit || state.isSubmitting,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        AnimatedContent(
            targetState = state.isSubmitting,
            transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
            label = "submitContent",
        ) { submitting ->
            if (submitting) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(stringResource(R.string.login_submitting))
                }
            } else {
                Text(stringResource(R.string.login_submit))
            }
        }
    }
}

@Composable
private fun Footer(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Rounded.LockOpen,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(R.string.login_footer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val previewNow: Instant = Instant.parse("2026-10-14T12:00:00Z")

private val previewUsers = listOf(
    KnownUserUi("ashketchum", favoriteCount = 12, lastLoginAt = previewNow - Duration.ofDays(2)),
    KnownUserUi("misty", favoriteCount = 5, lastLoginAt = previewNow - Duration.ofDays(7)),
    KnownUserUi("brock", favoriteCount = 0, lastLoginAt = previewNow - Duration.ofDays(21)),
    KnownUserUi("garyoak", favoriteCount = 1, lastLoginAt = previewNow - Duration.ofDays(40)),
)

@Composable
private fun LoginPreview(state: LoginUiState) {
    PokeSearchTheme(dynamicColor = false) {
        LoginScreen(state = state, onInputChange = {}, onInputBlur = {}, onSubmit = {}, onKnownUserClick = {})
    }
}

@Preview(name = "L1 · Primer uso", heightDp = 915, widthDp = 412)
@Composable
private fun LoginFirstUsePreview() = LoginPreview(LoginUiState(isLoading = false, now = previewNow))

@PreviewLightDark
@Composable
private fun LoginReturningPreview() = LoginPreview(
    LoginUiState(
        isLoading = false,
        input = "misty",
        isInputValid = true,
        matchedUser = previewUsers[1],
        knownUsers = previewUsers,
        now = previewNow,
    ),
)

@Preview(name = "L3 · Error de validación", heightDp = 915, widthDp = 412)
@Composable
private fun LoginValidationErrorPreview() = LoginPreview(
    LoginUiState(
        isLoading = false,
        input = "ash!",
        inputError = UsernameError.InvalidCharacter('!'),
        now = previewNow,
    ),
)

@Preview(name = "L4 · Cargando", heightDp = 915, widthDp = 412)
@Composable
private fun LoginSubmittingPreview() = LoginPreview(
    LoginUiState(isLoading = false, input = "garyoak", isInputValid = true, isSubmitting = true, now = previewNow),
)

@Preview(name = "Restaurando usuario conocido", heightDp = 915, widthDp = 412)
@Composable
private fun LoginRestoringPreview() = LoginPreview(
    LoginUiState(isLoading = false, knownUsers = previewUsers, restoringUsername = "misty", now = previewNow),
)
