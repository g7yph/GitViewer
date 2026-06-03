package dev.icerock.gitviewer.presentation.ui.repository

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.icerock.gitviewer.presentation.model.RepoItemModel
import dev.icerock.gitviewer.presentation.ui.repository.model.RepositoryInfoUiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

internal class RepositoryInfoScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayInfo_whenStateIsSuccess() {
        // Given
        val vm = mockk<RepositoryInfoViewModel>(relaxed = true) {
            val uiState = RepositoryInfoUiState(
                isRepoLoading = false,
                repo = RepoItemModel(
                    id = 1,
                    owner = "Sample owner",
                    name = "test-repo",
                    description = "Sample description ".repeat(5),
                    primaryLanguage = "Kotlin",
                    link = "sample link",
                    license = "MIT",
                    stars = 10,
                    forks = 3,
                    watchers = 10,
                    issues = 3
                ),
                isRepoReadmeLoading = false,
                repoReadme = "# README\nProject docs"
            )
            every { uiStates() } returns MutableStateFlow(value = uiState)
            every { uiActions() } returns MutableSharedFlow(extraBufferCapacity = 1)
        }

        // When
        composeTestRule.setContent {
            RepositoryInfoRoute(
                viewModel = vm,
                id = 0,
                name = "",
                owner = "",
                onNavigate = {},
                onNavigateUp = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("sample link").assertExists()
        composeTestRule.onNodeWithText("MIT").assertExists()
    }
}