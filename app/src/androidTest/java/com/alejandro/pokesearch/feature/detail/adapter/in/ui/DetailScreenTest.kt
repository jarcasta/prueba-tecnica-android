package com.alejandro.pokesearch.feature.detail.adapter.`in`.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.domain.DomainError
import com.alejandro.pokesearch.core.domain.EntryId
import com.alejandro.pokesearch.core.domain.EntryKey
import com.alejandro.pokesearch.core.domain.PokeCategory
import com.alejandro.pokesearch.core.domain.getOrNull
import com.alejandro.pokesearch.feature.detail.domain.GenerationDetail
import com.alejandro.pokesearch.feature.detail.domain.SpeciesSummary
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun cuando_falla_sin_cache_entonces_reintentar_invoca_el_callback() {
        var retries = 0
        composeRule.setContent {
            PokeSearchTheme(dynamicColor = false) {
                DetailScreen(
                    state = DetailUiState.Error(DomainError.NoConnection),
                    onBack = {},
                    onToggleFavorite = {},
                    onRetry = { retries++ },
                    snackbarHostState = SnackbarHostState(),
                )
            }
        }

        composeRule.onNodeWithText("No pudimos cargar el detalle").assertExists()
        composeRule.onNodeWithText("Reintentar").performClick()

        assertEquals(1, retries)
    }

    @Test
    fun cuando_se_toca_una_especie_de_la_generacion_entonces_abre_su_pokemon() {
        val opened = mutableListOf<EntryKey>()
        val generation = GenerationDetail(
            key = key(PokeCategory.GENERATION, 1),
            name = "generation-i",
            mainRegion = "kanto",
            newSpeciesCount = 151,
            versionGroups = listOf("red-blue"),
            speciesSample = listOf("bulbasaur", "ivysaur").mapIndexed { index, name ->
                SpeciesSummary(key(PokeCategory.POKEMON, index + 1), name, artworkUrl = "")
            },
        )
        composeRule.setContent {
            PokeSearchTheme(dynamicColor = false) {
                DetailScreen(
                    state = DetailUiState.Content(generation, isFavorite = false, isStale = false),
                    onBack = {},
                    onToggleFavorite = {},
                    onRetry = {},
                    snackbarHostState = SnackbarHostState(),
                    onOpenEntry = { opened += it },
                )
            }
        }

        composeRule.onNodeWithText("Generation I").assertExists()
        composeRule.onNodeWithText("Ivysaur").performScrollTo().performClick()

        assertEquals(listOf(key(PokeCategory.POKEMON, 2)), opened)
    }

    private fun key(category: PokeCategory, id: Int) = EntryKey(category, requireNotNull(EntryId.of(id).getOrNull()))
}
