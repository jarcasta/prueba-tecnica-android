package com.alejandro.pokesearch.feature.dashboard.adapter.`in`.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.LogoutDialog
import com.alejandro.pokesearch.core.designsystem.component.PokeTopBar
import com.alejandro.pokesearch.core.designsystem.component.ResetDataDialog
import com.alejandro.pokesearch.core.designsystem.component.initialsOf
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardUiState,
    selectedTab: DashboardTab,
    menuExpanded: Boolean,
    dialog: DashboardDialog,
    onTabSelect: (DashboardTab) -> Unit,
    onAvatarClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onResetDataClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onLogoutConfirm: () -> Unit,
    onResetDataConfirm: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    content: @Composable (contentPadding: PaddingValues) -> Unit,
) {
    val user = state as? DashboardUiState.Content
    if (user == null) {
        Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface))
        return
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            PokeTopBar(
                userInitials = initialsOf(user.username),
                userName = user.username,
                favoriteCount = user.favoriteCount,
                menuExpanded = menuExpanded,
                onAvatarClick = onAvatarClick,
                onDismissMenu = onDismissMenu,
                onResetData = onResetDataClick,
                onLogout = onLogoutClick,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            DashboardNavigationBar(selectedTab = selectedTab, onTabSelect = onTabSelect)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        content(padding)
    }

    when (dialog) {
        DashboardDialog.None -> Unit
        DashboardDialog.Logout -> LogoutDialog(
            userName = user.username,
            favoriteCount = user.favoriteCount,
            onConfirm = onLogoutConfirm,
            onDismiss = onDismissDialog,
        )
        DashboardDialog.ResetData -> ResetDataDialog(
            userName = user.username,
            favoriteCount = user.favoriteCount,
            onConfirm = onResetDataConfirm,
            onDismiss = onDismissDialog,
        )
    }
}

@Composable
private fun DashboardNavigationBar(selectedTab: DashboardTab, onTabSelect: (DashboardTab) -> Unit) {
    NavigationBar {
        DashboardTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelect(tab) },
                icon = { Icon(imageVector = tab.icon(selected), contentDescription = null) },
                label = { Text(stringResource(tab.labelRes)) },
            )
        }
    }
}

private val DashboardTab.labelRes: Int
    get() = when (this) {
        DashboardTab.Explore -> R.string.nav_explore
        DashboardTab.Favorites -> R.string.nav_favorites
    }

private fun DashboardTab.icon(selected: Boolean): ImageVector = when (this) {
    DashboardTab.Explore -> if (selected) Icons.Rounded.Explore else Icons.Outlined.Explore
    DashboardTab.Favorites -> if (selected) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder
}

private val previewState = DashboardUiState.Content(username = "ashketchum", favoriteCount = 12)

@Composable
private fun DashboardPreview(
    state: DashboardUiState = previewState,
    selectedTab: DashboardTab = DashboardTab.Explore,
    dialog: DashboardDialog = DashboardDialog.None,
) {
    PokeSearchTheme(dynamicColor = false) {
        DashboardScreen(
            state = state,
            selectedTab = selectedTab,
            menuExpanded = false,
            dialog = dialog,
            onTabSelect = {},
            onAvatarClick = {},
            onDismissMenu = {},
            onResetDataClick = {},
            onLogoutClick = {},
            onDismissDialog = {},
            onLogoutConfirm = {},
            onResetDataConfirm = {},
            snackbarHostState = SnackbarHostState(),
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(selectedTab.labelRes))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DashboardExplorePreview() = DashboardPreview()

@PreviewLightDark
@Composable
private fun DashboardFavoritesPreview() = DashboardPreview(selectedTab = DashboardTab.Favorites)

@PreviewLightDark
@Composable
private fun DashboardLogoutDialogPreview() = DashboardPreview(dialog = DashboardDialog.Logout)

@PreviewLightDark
@Composable
private fun DashboardResetDialogPreview() = DashboardPreview(dialog = DashboardDialog.ResetData)

@PreviewLightDark
@Composable
private fun DashboardLoadingPreview() = DashboardPreview(state = DashboardUiState.Loading)
