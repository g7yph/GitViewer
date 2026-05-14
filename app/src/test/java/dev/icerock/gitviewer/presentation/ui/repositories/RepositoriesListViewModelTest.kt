package dev.icerock.gitviewer.presentation.ui.repositories

import androidx.paging.PagingData
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dev.icerock.gitviewer.data.Repository
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.data.repository.RepoRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoriesListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository = mockk<AuthRepository>()
    private val repoRepository = mockk<RepoRepository>()
    private lateinit var viewModel: RepositoriesListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Given
        val testRepos = listOf(
            Repository(
                id = 1,
                name = "GitViewer",
                owner = "user",
                url = "sample url",
                description = "GitHub client",
                stars_count = 10,
                watchers_count = 2,
                issues_count = 3,
                language = "ru",
                forks_count = 9,
                license = "MIT",
                cache_ttl = 0
            )
        )
        every { repoRepository.getAllRepositories() } returns flowOf(PagingData.from(testRepos))

        viewModel = RepositoriesListViewModel(
            authRepository = authRepository,
            repoRepository = repoRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetch repositories returns success with list`() = runTest {
        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repoRepository.getAllRepositories() }
        viewModel.allRepositories.test {
            val item = awaitItem()
            assertThat(item).isNotNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}