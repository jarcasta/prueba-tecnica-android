package com.alejandro.pokesearch.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.EmptyState
import com.alejandro.pokesearch.feature.auth.adapter.`in`.ui.LoginRoute
import com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui.ExploreRoute
import com.alejandro.pokesearch.feature.dashboard.adapter.`in`.ui.DashboardRoute
import com.alejandro.pokesearch.feature.favorites.adapter.`in`.ui.FavoritesRoute

/**
 * App root. Se debe determinar el estado de la pantalla antes de poder
 * mostrarle algo al usuario y no mostrar el dashboard cuando no tiene
 * sesion por ejemplo
 */
@Composable
fun PokeSearchApp(viewModel: MainViewModel = hiltViewModel()) {
    val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
    when (startDestination) {
        StartDestination.Loading -> Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        )
        StartDestination.Login -> AppNavHost(startDestination = Login)
        StartDestination.Dashboard -> AppNavHost(startDestination = Dashboard)
    }
}

@Composable
fun AppNavHost(startDestination: Any, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable<Login> {
            LoginRoute(
                onLoggedIn = {
                    navController.navigate(Dashboard) {
                        popUpTo<Login> { inclusive = true }
                    }
                },
            )
        }
        composable<Dashboard> {
            DashboardRoute(
                onLoggedOut = {
                    navController.navigate(Login) {
                        // Clears the whole back stack: back from Login leaves the app.
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                exploreTab = { contentPadding ->
                    ExploreRoute(
                        onOpenDetail = { key -> navController.navigate(Detail(key.category, key.id.value)) },
                        contentPadding = contentPadding,
                    )
                },
                favoritesTab = { contentPadding, onExploreClick ->
                    FavoritesRoute(
                        onOpenDetail = { key -> navController.navigate(Detail(key.category, key.id.value)) },
                        onExploreClick = onExploreClick,
                        contentPadding = contentPadding,
                    )
                },
            )
        }
        composable<Detail> {
            Scaffold { padding -> UnderConstruction(padding) }
        }
    }
}

@Composable
private fun UnderConstruction(contentPadding: PaddingValues) {
    EmptyState(
        icon = Icons.Rounded.Construction,
        title = stringResource(R.string.placeholder_title),
        message = stringResource(R.string.placeholder_message),
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
    )
}
