package dev.icerock.gitviewer.presentation.ui.issue.create

import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateAction
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateUiState
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

internal class IssueCreateScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisableSubmitIssueAndShowSpinner_whenStateIsLoading() {
        val vm = mockk<IssueCreateViewModel>(relaxed = true) {
            every { uiStates() } returns MutableStateFlow(IssueCreateUiState(isLoading = true))
            every { uiActions() } returns MutableSharedFlow(extraBufferCapacity = 1)

            every { titleField.data } returns CMutableStateFlow(MutableStateFlow(""))
            every { titleField.error } returns CStateFlow(MutableStateFlow(null))

            every { descriptionField.data } returns CMutableStateFlow(MutableStateFlow(""))
            every { descriptionField.error } returns CStateFlow(MutableStateFlow(null))
        }

        composeTestRule.setContent {
            IssueCreateRoute(
                vm,
                repoId = 0,
                repoOwner = "",
                repoName = "",
                onNavigateUp = {}
            )
        }

        composeTestRule.onNodeWithText("SUBMIT NEW ISSUE").assertIsNotDisplayed()
        verify(inverse = true) { vm.onEvent(any()) }
    }

    @Test
    fun shouldEmitSubmitAndNavigate_whenFormValid() {
        val actionFlow = MutableSharedFlow<IssueCreateAction>(extraBufferCapacity = 1)
        val vm = mockk<IssueCreateViewModel>(relaxed = true) {
            every { uiStates() } returns MutableStateFlow(IssueCreateUiState())
            every { uiActions() } returns actionFlow

            every { titleField.data } returns CMutableStateFlow(MutableStateFlow("Valid Title"))
            every { titleField.error } returns CStateFlow(MutableStateFlow(null))

            every { descriptionField.data } returns CMutableStateFlow(MutableStateFlow("Valid Desc"))
            every { descriptionField.error } returns CStateFlow(MutableStateFlow(null))
        }

        composeTestRule.setContent {
            IssueCreateRoute(
                vm,
                repoId = 0,
                repoOwner = "",
                repoName = "",
                onNavigateUp = {}
            )
        }

        composeTestRule.onNodeWithText("SUBMIT NEW ISSUE").performClick()
        actionFlow.tryEmit(IssueCreateAction.OpenPreviousScreen)
        verify { vm.onEvent(any()) }
    }
}