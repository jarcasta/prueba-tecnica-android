package com.alejandro.pokesearch.feature.detail.adapter.`in`.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.core.domain.DomainError
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
}
