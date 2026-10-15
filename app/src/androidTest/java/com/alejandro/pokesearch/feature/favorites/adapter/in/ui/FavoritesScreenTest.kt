package com.alejandro.pokesearch.feature.favorites.adapter.`in`.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun cuando_no_hay_favoritos_entonces_explorar_invoca_el_callback() {
        var exploreClicks = 0
        composeRule.setContent {
            PokeSearchTheme(dynamicColor = false) {
                FavoritesScreen(
                    filters = FavoritesFilters(),
                    state = FavoritesUiState.Empty,
                    onQueryChange = {},
                    onCategorySelect = {},
                    onClearFilters = {},
                    onItemClick = {},
                    onRemoveFavorite = {},
                    onExploreClick = { exploreClicks++ },
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Aún no tienes favoritos").assertExists()
        composeRule.onNodeWithText("Buscar en favoritos").assertDoesNotExist()
        composeRule.onNodeWithText("Explorar").performClick()

        assertEquals(1, exploreClicks)
    }
}
