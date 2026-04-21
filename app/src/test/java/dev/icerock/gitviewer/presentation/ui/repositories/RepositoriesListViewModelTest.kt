package dev.icerock.gitviewer.presentation.ui.repositories

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dev.icerock.gitviewer.data.datasource.remote.model.LicenseDto
import dev.icerock.gitviewer.data.datasource.remote.model.OwnerDto
import dev.icerock.gitviewer.data.datasource.remote.model.RepoDto
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.data.repository.RepoRepository
import dev.icerock.gitviewer.presentation.mapper.toRepoModel
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListEvent
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListUiState
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
class RepositoriesListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository = mockk<AuthRepository>()
    private val repoRepository = mockk<RepoRepository>()
    private lateinit var viewModel: RepositoriesListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
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
        // Given
        val testRepos = listOf(
            RepoDto(
                id = 1,
                name = "GitViewer",
                private = true,
                owner = OwnerDto(login = "user"),
                htmlUrl = "sample url",
                description = "GitHub client",
                stargazersCount = 10,
                watchersCount = 2,
                language = "ru",
                forksCount = 9,
                license = LicenseDto(name = "MIT")
            )
        )
        coEvery { repoRepository.getAllRepositories() } returns Result.success(testRepos)

        // When
        viewModel.onEvent(RepositoriesListEvent.FetchRepositories)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repoRepository.getAllRepositories() }
        viewModel.uiStates().test {
            val state = awaitItem()
            assertThat(state.repos).isNotEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetch repositories with error shows error state`() = runTest {
        // Given
        coEvery { repoRepository.getAllRepositories() } returns Result.failure(Exception())

        // When
        viewModel.onEvent(RepositoriesListEvent.FetchRepositories)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repoRepository.getAllRepositories() }
        viewModel.uiStates().test {
            val state = awaitItem()
            assertThat(state.repos).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}