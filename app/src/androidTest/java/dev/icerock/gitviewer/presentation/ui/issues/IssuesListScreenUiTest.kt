package dev.icerock.gitviewer.presentation.ui.issues

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import dev.icerock.gitviewer.presentation.model.IssueItemModel
import dev.icerock.gitviewer.presentation.model.IssueStateModel
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListAction
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

internal class IssuesListScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val issues = listOf(
        IssueItemModel(
            id = 0,
            number = 1,
            name = "Bug",
            state = IssueStateModel.Open,
            date = "",
            description = ""
        ),
        IssueItemModel(
            id = 1,
            number = 3,
            name = "Test",
            state = IssueStateModel.Closed,
            date = "",
            description = ""
        ),
        IssueItemModel(
            id = 2,
            number = 7,
            name = "Sample",
            state = IssueStateModel.Open,
            date = "",
            description = ""
        )
    )

    @Test
    fun shouldShowList_whenStateIsSuccess() {
        // Given
        val vm = mockk<IssuesListViewModel>(relaxed = true) {
            every { issuesFlow } returns MutableStateFlow(PagingData.from(data = issues))
            every { uiActions() } returns MutableSharedFlow(extraBufferCapacity = 1)
        }

        // When
        composeTestRule.setContent {
            IssuesListRoute(
                vm,
                repoId = 99,
                repoOwner = "",
                repoName = "",
                onNavigate = {},
                onNavigateUp = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Bug").assertExists()
    }

    @Test
    fun shouldEmitNavigation_whenIssueClicked() {
        // Given
        val vm = mockk<IssuesListViewModel>(relaxed = true) {
            every { issuesFlow } returns MutableStateFlow(PagingData.from(data = issues))
            every { uiActions() } returns MutableSharedFlow(extraBufferCapacity = 1)
        }

        // When
        composeTestRule.setContent {
            IssuesListRoute(
                vm,
                repoId = 99,
                repoOwner = "",
                repoName = "",
                onNavigate = {},
                onNavigateUp = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Bug").performClick()
        verify { vm.onEvent(any()) }
    }

    @Test
    fun shouldTriggerCreateIssue_whenFabClicked() {
        // Given
        val vm = mockk<IssuesListViewModel>(relaxed = true) {
            every { issuesFlow } returns MutableStateFlow(PagingData.from(emptyList()))
            every { uiActions() } returns MutableSharedFlow<IssuesListAction>(extraBufferCapacity = 1)
        }

        // When
        composeTestRule.setContent {
            IssuesListRoute(
                vm,
                repoId = 0,
                repoOwner = "",
                repoName = "",
                onNavigate = {},
                onNavigateUp = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Create new issue").performClick()
        verify { vm.onEvent(any()) }
    }
}