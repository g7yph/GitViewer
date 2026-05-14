package dev.icerock.gitviewer.presentation.ui.issue.create

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dev.icerock.gitviewer.data.repository.ImageRepository
import dev.icerock.gitviewer.data.repository.IssueRepository
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateAction
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateEvent
import io.mockk.coEvery
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
class IssueCreateViewModelTest {

    private val issueRepository = mockk<IssueRepository>()
    private val imageRepository = mockk<ImageRepository>()

    private lateinit var viewModel: IssueCreateViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = IssueCreateViewModel(
            issueRepository = issueRepository,
            imageRepository = imageRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create issue with valid data should navigate to issues list`() = runTest {
        // Given
        coEvery {
            issueRepository.createIssue(
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(Unit)

        // When
        viewModel.onEvent(IssueCreateEvent.SubmitIssue(repoId = 0, repoOwner = "", repoName = ""))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiActions().test {
            val action = awaitItem()
            assertThat(action).isInstanceOf(IssueCreateAction.OpenPreviousScreen::class.java)
        }
    }

    @Test
    fun `create issue with empty title should show error`() = runTest {
        // Given
        coEvery {
            issueRepository.createIssue(
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.failure(Exception("Title is required"))

        // When
        viewModel.uiActions().test {
            viewModel.onEvent(
                IssueCreateEvent.SubmitIssue(repoId = 0, repoOwner = "", repoName = "")
            )
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val action = awaitItem()
            assertThat(action).isInstanceOf(IssueCreateAction.ShowActionFailedDialog::class.java)
        }
    }
}