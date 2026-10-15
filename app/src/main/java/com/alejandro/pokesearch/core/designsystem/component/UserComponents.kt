package com.alejandro.pokesearch.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.designsystem.theme.Spacing

@Composable
fun favoriteCountLabel(count: Int): String =
    if (count == 0) {
        stringResource(R.string.favorites_none)
    } else {
        pluralStringResource(R.plurals.favorites_count, count, count)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeTopBar(
    userInitials: String,
    userName: String,
    favoriteCount: Int,
    menuExpanded: Boolean,
    onAvatarClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onResetData: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = { Wordmark(style = MaterialTheme.typography.titleLarge) },
        actions = {
            Box {
                val avatarDescription = stringResource(R.string.user_menu_open)
                IconButton(
                    onClick = onAvatarClick,
                    modifier = Modifier.semantics { contentDescription = avatarDescription },
                ) {
                    UserAvatar(
                        initials = userInitials,
                        size = 32.dp,
                        textStyle = MaterialTheme.typography.labelLarge,
                    )
                }
                UserMenu(
                    expanded = menuExpanded,
                    userInitials = userInitials,
                    userName = userName,
                    favoriteCount = favoriteCount,
                    onDismiss = onDismissMenu,
                    onResetData = onResetData,
                    onLogout = onLogout,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}
@Composable
fun UserMenu(
    expanded: Boolean,
    userInitials: String,
    userName: String,
    favoriteCount: Int,
    onDismiss: () -> Unit,
    onResetData: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.width(248.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = Spacing.sm),
        ) {
            UserAvatar(initials = userInitials, size = 32.dp, textStyle = MaterialTheme.typography.labelLarge)
            Column {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = favoriteCountLabel(favoriteCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(bottom = Spacing.xs))
        DropdownMenuItem(
            text = { Text(stringResource(R.string.user_menu_reset)) },
            leadingIcon = { Icon(imageVector = Icons.Rounded.RestartAlt, contentDescription = null) },
            onClick = onResetData,
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.user_menu_logout)) },
            leadingIcon = { Icon(imageVector = Icons.AutoMirrored.Rounded.Logout, contentDescription = null) },
            onClick = onLogout,
        )
    }
}

@Composable
fun KnownUserItem(
    initials: String,
    userName: String,
    supportingText: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = { Text(userName) },
        supportingContent = { Text(supportingText) },
        leadingContent = { UserAvatar(initials = initials) },
        trailingContent = {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = modifier.clickable(enabled = !isLoading, onClick = onClick),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PokeTopBarPreview() {
    PokeSearchTheme(dynamicColor = false) {
        PokeTopBar(
            userInitials = "AS",
            userName = "ashketchum",
            favoriteCount = 12,
            menuExpanded = false,
            onAvatarClick = {},
            onDismissMenu = {},
            onResetData = {},
            onLogout = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun KnownUserItemPreview() {
    PokeSearchTheme(dynamicColor = false) {
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column {
                KnownUserItem(
                    initials = "AS",
                    userName = "ashketchum",
                    supportingText = "12 favoritos · último acceso hace 2 días",
                    isLoading = false,
                    onClick = {},
                )
                KnownUserItem(
                    initials = "MI",
                    userName = "misty",
                    supportingText = "5 favoritos · último acceso hace 1 semana",
                    isLoading = true,
                    onClick = {},
                )
            }
        }
    }
}
