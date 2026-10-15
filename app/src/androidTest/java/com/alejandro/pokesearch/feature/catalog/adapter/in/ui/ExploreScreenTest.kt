package com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.PokeCategory
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExploreScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun cuando_falla_sin_datos_entonces_reintentar_invoca_el_callback() {
        var retries = 0
        composeRule.setContent {
            PokeSearchTheme(dynamicColor = false) {
                ExploreScreen(
                    category = PokeCategory.REGION,
                    items = flowOf(PagingData.empty<CatalogItemUi>()).collectAsLazyPagingItems(),
                    listState = ExploreListState.Error(DomainError.NoConnection),
                    onCategorySelect = {},
                    onItemClick = {},
                    onToggleFavorite = {},
                    onRetry = { retries++ },
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("No pudimos cargar las regiones").assertExists()
        composeRule.onNodeWithText("Reintentar").performClick()

        assertEquals(1, retries)
    }
}
