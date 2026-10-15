package com.alejandro.pokesearch.feature.catalog.adapter.`in`.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.catalog.domain.CatalogEntry
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

    @Test
    fun cuando_no_hay_conexion_y_hay_items_entonces_muestra_el_banner_offline() {
        val pikachu = CatalogEntry(EntryKey(PokeCategory.POKEMON, requireNotNull(EntryId.of(25).getOrNull())), "pikachu", null)
            .toItemUi(isFavorite = false)
        val notLoading = LoadState.NotLoading(endOfPaginationReached = true)
        composeRule.setContent {
            PokeSearchTheme(dynamicColor = false) {
                ExploreScreen(
                    category = PokeCategory.POKEMON,
                    items = flowOf(
                        PagingData.from(listOf(pikachu), sourceLoadStates = LoadStates(notLoading, notLoading, notLoading)),
                    ).collectAsLazyPagingItems(),
                    listState = ExploreListState.Content(totalCount = 1, footer = PagingFooterState.None, staleDataError = null),
                    isOffline = true,
                    onCategorySelect = {},
                    onItemClick = {},
                    onToggleFavorite = {},
                    onRetry = {},
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("Pikachu").assertIsDisplayed()
        composeRule.onNodeWithText("Sin conexión · mostrando datos guardados").assertIsDisplayed()
    }
}
