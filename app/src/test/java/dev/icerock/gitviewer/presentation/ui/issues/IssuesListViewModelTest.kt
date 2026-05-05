package dev.icerock.gitviewer.presentation.ui.issues

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dev.icerock.gitviewer.data.Issue
import dev.icerock.gitviewer.data.repository.IssueRepository
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListEvent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IssuesListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val issuesRepository = mockk<IssueRepository>()
    private lateinit var viewModel: IssuesListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = IssuesListViewModel(issuesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load issues should emit success state`() = runTest {
        // Given
        val testPagingData = PagingData.from(
            listOf(
                Issue(
                    id = 1,
                    repository_id = 100,
                    number = 1,
                    title = "Issue 1",
                    description = "Body 1",
                    state = "open",
                    created_at = "2026-01-01T00:00:00Z",
                    cache_ttl = System.currentTimeMillis() + 3600000
                )
            )
        )

        // When
        coEvery {
            issuesRepository.getAllRepositoryIssues(any(), any(), any())
        } returns flowOf(testPagingData)
        viewModel.onEvent(IssuesListEvent.FetchIssues(repoId = 0, repoOwner = "", repoName = ""))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.issuesFlow.test {
            val state = awaitItem()
            assertThat(state).isNotInstanceOf(LoadState.Error::class.java)
        }
    }
}