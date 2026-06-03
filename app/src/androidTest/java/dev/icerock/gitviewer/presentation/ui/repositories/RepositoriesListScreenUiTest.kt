package dev.icerock.gitviewer.presentation.ui.repositories

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import dev.icerock.gitviewer.presentation.model.RepoItemModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

internal class RepositoriesListScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val repos = listOf(
        RepoItemModel(
            id = 1,
            owner = "Sample owner",
            name = "test-repo",
            description = "Sample description ".repeat(5),
            primaryLanguage = "Kotlin",
            link = "",
            license = "MIT",
            stars = 10,
            forks = 3,
            watchers = 10,
            issues = 3
        ),
        RepoItemModel(
            id = 2,
            owner = "Sample owner",
            name = "Sample name",
            description = "Sample description ".repeat(5),
            primaryLanguage = "Kotlin",
            link = "",
            license = "MIT",
            stars = 10,
            forks = 3,
            watchers = 10,
            issues = 30
        ),
        RepoItemModel(
            id = 3,
            owner = "Sample owner",
            name = "Sample name",
            description = "Sample description ".repeat(5),
            primaryLanguage = "Kotlin",
            link = "",
            license = "MIT",
            stars = 10,
            forks = 3,
            watchers = 10,
            issues = 12
        )
    )

    @Test
    fun shouldShowList_whenStateIsSuccess() {
        // Given
        val vm = mockk<RepositoriesListViewModel>(relaxed = true) {
            every { allRepositories } returns MutableStateFlow(PagingData.from(data = repos))
            every { uiActions() } returns MutableSharedFlow(extraBufferCapacity = 1)
        }

        // When
        composeTestRule.setContent { RepositoriesListRoute(vm, onNavigate = {}) }

        // Then
        composeTestRule.onNodeWithText("test-repo").assertExists()
    }

    @Test
    fun shouldEmitNavigation_whenRepoClicked() {
        // Given
        val vm = mockk<RepositoriesListViewModel>(relaxed = true) {
            every { allRepositories } returns MutableStateFlow(PagingData.from(data = repos))
            every { uiActions() } returns MutableSharedFlow(extraBufferCapacity = 1)
        }

        // When
        composeTestRule.setContent { RepositoriesListRoute(vm, onNavigate = {}) }
        composeTestRule.onNodeWithText("test-repo").performClick()

        // Then
        verify { vm.onEvent(any()) }
    }
}