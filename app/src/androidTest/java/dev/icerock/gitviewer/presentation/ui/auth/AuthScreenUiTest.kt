package dev.icerock.gitviewer.presentation.ui.auth

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthAction
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthUiState
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class AuthScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisableSignInAndShowSpinner_whenStateIsLoading() {
        // Given
        val viewModel = mockk<AuthViewModel>(relaxed = true) {
            val uiState = AuthUiState(isLoading = true, tokenIsValid = true, errorMessage = "Error")
            every { uiStates() } returns MutableStateFlow(uiState)
            every { uiActions() } returns MutableSharedFlow(extraBufferCapacity = 1)

            every { tokenField.data } returns CMutableStateFlow(MutableStateFlow("ghp_test"))
            every { tokenField.error } returns CStateFlow(MutableStateFlow(null))
        }
        composeTestRule.setContent { AuthRoute(viewModel = viewModel, onNavigate = {}) }

        // Then
        composeTestRule.onNodeWithText("SIGN IN").assertIsNotDisplayed()
        verify(exactly = 0) { viewModel.onEvent(any()) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shouldSignInAndNavigate_whenValidTokenEntered() {
        // Given
        val viewModel = mockk<AuthViewModel>(relaxed = true) {
            val uiState = AuthUiState(isLoading = false, tokenIsValid = true, errorMessage = "")
            every { uiStates() } returns MutableStateFlow(uiState)
            every { uiActions() } returns MutableSharedFlow<AuthAction>(extraBufferCapacity = 1)

            every { tokenField.data } returns CMutableStateFlow(MutableStateFlow("ghp_valid"))
            every { tokenField.error } returns CStateFlow(MutableStateFlow(null))
        }
        composeTestRule.setContent { AuthRoute(viewModel = viewModel, onNavigate = {}) }

        // When
        composeTestRule.onNodeWithText("SIGN IN").performClick()
        composeTestRule.waitForIdle()

        // Then
        verify { viewModel.onEvent(any()) }
    }
}