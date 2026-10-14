package com.alejandro.pokesearch.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alejandro.pokesearch.R
import com.alejandro.pokesearch.core.designsystem.component.EmptyState

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LoginRoute, modifier = modifier) {
        composable<LoginRoute> { UnderConstruction() }
        composable<DashboardRoute> { UnderConstruction() }
        composable<DetailRoute> { UnderConstruction() }
    }
}

@Composable
private fun UnderConstruction() {
    Scaffold { padding ->
        EmptyState(
            icon = Icons.Rounded.Construction,
            title = stringResource(R.string.placeholder_title),
            message = stringResource(R.string.placeholder_message),
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
        )
    }
}
