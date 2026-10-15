package com.alejandro.pokesearch.feature.dashboard.adapter.`in`.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alejandro.pokesearch.R


@Composable
fun DashboardRoute(
    onLoggedOut: () -> Unit,
    exploreTab: @Composable (contentPadding: PaddingValues) -> Unit,
    favoritesTab: @Composable (contentPadding: PaddingValues, onExploreClick: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    var dialog by rememberSaveable { mutableStateOf(DashboardDialog.None) }
    val tabsNavController = rememberNavController()
    val backStackEntry by tabsNavController.currentBackStackEntryAsState()
    val selectedTab = DashboardTab.entries.firstOrNull { tab ->
        backStackEntry?.destination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
    } ?: DashboardTab.Explore
    val resources = LocalResources.current
    val currentOnLoggedOut by rememberUpdatedState(onLoggedOut)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                DashboardEffect.NavigateToLogin -> currentOnLoggedOut()
                is DashboardEffect.ShowResetDone -> snackbarHostState.showSnackbar(
                    if (effect.removedCount == 0) {
                        resources.getString(R.string.reset_done_none)
                    } else {
                        resources.getQuantityString(R.plurals.reset_done, effect.removedCount, effect.removedCount)
                    },
                )
            }
        }
    }

    DashboardScreen(
        state = state,
        selectedTab = selectedTab,
        menuExpanded = menuExpanded,
        dialog = dialog,
        onTabSelect = { tab -> tabsNavController.navigateToTab(tab) },
        onAvatarClick = { menuExpanded = true },
        onDismissMenu = { menuExpanded = false },
        onResetDataClick = {
            menuExpanded = false
            dialog = DashboardDialog.ResetData
        },
        onLogoutClick = {
            menuExpanded = false
            dialog = DashboardDialog.Logout
        },
        onDismissDialog = { dialog = DashboardDialog.None },
        onLogoutConfirm = {
            dialog = DashboardDialog.None
            viewModel.onLogoutConfirm()
        },
        onResetDataConfirm = {
            dialog = DashboardDialog.None
            viewModel.onResetDataConfirm()
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { contentPadding ->
        NavHost(navController = tabsNavController, startDestination = ExploreTab) {
            composable<ExploreTab> { exploreTab(contentPadding) }
            composable<FavoritesTab> {
                favoritesTab(contentPadding) { tabsNavController.navigateToTab(DashboardTab.Explore) }
            }
        }
    }
}

private fun NavController.navigateToTab(tab: DashboardTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
