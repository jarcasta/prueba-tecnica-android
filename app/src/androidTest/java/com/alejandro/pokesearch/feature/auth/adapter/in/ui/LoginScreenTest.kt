package com.alejandro.pokesearch.feature.auth.adapter.`in`.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alejandro.pokesearch.core.designsystem.theme.PokeSearchTheme
import com.alejandro.pokesearch.feature.auth.domain.UsernameError
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun cuando_el_input_es_invalido_entonces_entrar_esta_deshabilitado_y_se_muestra_el_error() {
        var state by mutableStateOf(
            LoginUiState(
                isLoading = false,
                input = "ash!",
                inputError = UsernameError.InvalidCharacter('!'),
                isInputValid = false,
            ),
        )
        var submits = 0
        composeRule.setContent {
            PokeSearchTheme(dynamicColor = false) {
                LoginScreen(
                    state = state,
                    onInputChange = {},
                    onInputBlur = {},
                    onSubmit = { submits++ },
                    onKnownUserClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Entrar").assertIsNotEnabled()
        composeRule.onNodeWithText("Solo letras y números, sin \"!\"").assertExists()

        state = LoginUiState(isLoading = false, input = "ash", isInputValid = true)

        composeRule.onNodeWithText("Entrar").assertIsEnabled().performClick()
        assertEquals(1, submits)
    }
}
