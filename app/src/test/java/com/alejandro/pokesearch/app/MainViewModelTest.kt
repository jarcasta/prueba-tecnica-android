package com.alejandro.pokesearch.app

import app.cash.turbine.test
import com.alejandro.pokesearch.core.testing.MainDispatcherRule
import com.alejandro.pokesearch.feature.auth.application.ObserveSessionUseCase
import com.alejandro.pokesearch.feature.auth.testing.FakeSessionRepository
import com.alejandro.pokesearch.feature.auth.testing.FakeUserRepository
import java.time.Instant
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val users = FakeUserRepository()
    private val sessions = FakeSessionRepository(users)

    @Test
    fun `cuando no hay sesion activa, entonces arranca en Login tras Loading`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = MainViewModel(ObserveSessionUseCase(sessions))

        viewModel.startDestination.test {
            assertEquals(StartDestination.Loading, awaitItem())
            assertEquals(StartDestination.Login, awaitItem())
        }
    }

    @Test
    fun `cuando hay sesion activa, entonces arranca en Dashboard sin pasar por Login`() = runTest(mainDispatcherRule.testDispatcher) {
        val user = users.givenUser("misty", createdAt = Instant.parse("2026-10-01T00:00:00Z"))
        sessions.open(user.id)
        val viewModel = MainViewModel(ObserveSessionUseCase(sessions))

        viewModel.startDestination.test {
            assertEquals(StartDestination.Loading, awaitItem())
            assertEquals(StartDestination.Dashboard, awaitItem())
        }
    }

    @Test
    fun `cuando la sesion cambia despues de arrancar, entonces el destino inicial no cambia`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = MainViewModel(ObserveSessionUseCase(sessions))

        viewModel.startDestination.test {
            skipItems(1)
            assertEquals(StartDestination.Login, awaitItem())

            val user = users.givenUser("brock", createdAt = Instant.parse("2026-10-01T00:00:00Z"))
            sessions.open(user.id)
            expectNoEvents()
        }
    }
}
