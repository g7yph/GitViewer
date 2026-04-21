package dev.icerock.gitviewer.presentation.ui.auth

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthAction
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository = mockk<AuthRepository>()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `authorize with valid token returns success`() = runTest {
        // Given
        val validToken = "ghp_1234567890"
        coEvery { authRepository.signIn(validToken) } returns Result.success(Unit)

        // When
        viewModel.onEvent(AuthEvent.TokenChanged(token = validToken))
        viewModel.onEvent(AuthEvent.SignIn)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { authRepository.signIn(validToken) }
        viewModel.uiActions().test {
            val action = awaitItem()
            assertThat(action).isInstanceOf(AuthAction.OpenRepositoriesListScreen::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `authorize with invalid token returns error`() = runTest {
        // Given
        val invalidToken = "invalid_token"
        coEvery { authRepository.signIn(invalidToken) } returns Result.failure(Exception())

        // When
        viewModel.onEvent(AuthEvent.TokenChanged(token = invalidToken))
        viewModel.onEvent(AuthEvent.SignIn)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { authRepository.signIn(invalidToken) }
        viewModel.uiActions().test {
            val action = awaitItem()
            assertThat(action).isNotInstanceOf(AuthAction.OpenRepositoriesListScreen::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }
}