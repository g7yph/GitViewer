package dev.icerock.gitviewer.presentation.ui.issue.info

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.icerock.gitviewer.presentation.model.IssueItemModel
import dev.icerock.gitviewer.presentation.model.IssueStateModel
import dev.icerock.gitviewer.presentation.ui.issue.info.model.IssueInfoUiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

internal class IssueInfoScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayInfo_whenStateIsSuccess() {
        // Given
        val vm = mockk<IssueInfoViewModel>(relaxed = true) {
            every { uiStates() } returns MutableStateFlow(
                IssueInfoUiState(
                    isIssueLoading = false,
                    issue = IssueItemModel(
                        id = 1,
                        number = 1,
                        name = "Auth bug",
                        state = IssueStateModel.Open,
                        date = "",
                        description = "Sample desc"
                    )
                )
            )
            every { uiActions() } returns MutableSharedFlow(extraBufferCapacity = 1)
        }

        // When
        composeTestRule.setContent {
            IssueInfoRoute(
                vm,
                number = 1,
                onNavigateUp = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Auth bug").assertExists()
        composeTestRule.onNodeWithText("Description").assertExists()
    }
}