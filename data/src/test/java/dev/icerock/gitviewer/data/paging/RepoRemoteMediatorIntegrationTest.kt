package dev.icerock.gitviewer.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.google.common.truth.Truth.assertThat
import dev.icerock.gitviewer.data.GitHubDatabase
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.datasource.remote.model.OwnerDto
import dev.icerock.gitviewer.data.datasource.remote.model.RepoDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.SocketTimeoutException

internal class RepoRemoteMediatorIntegrationTest {
    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: GitHubDatabase
    private lateinit var apiService: GitHubApiService
    private lateinit var mediator: RepoRemoteMediator

    @Before
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        GitHubDatabase.Schema.create(driver)
        database = GitHubDatabase(driver)

        apiService = mockk()

        mediator = RepoRemoteMediator(database, apiService)
    }

    @After
    fun tearDown() {
        driver.close()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `load should return data from database when network success`() = runTest {
        // Given
        val mockRepos = listOf(
            RepoDto(
                id = 100,
                name = "api_repo",
                private = true,
                owner = OwnerDto(login = "owner"),
                htmlUrl = "",
                description = "From Network",
                stargazersCount = 0,
                watchersCount = 0,
                openIssuesCount = 0,
                forksCount = 0,
                license = null,
                language = "Kotlin"
            )
        )
        coEvery { apiService.getAllRepositories() } returns Result.success(mockRepos)

        // When
        val result = mediator.load(
            loadType = LoadType.REFRESH,
            state = PagingState(
                pages = emptyList(),
                anchorPosition = null,
                config = PagingConfig(pageSize = 10),
                leadingPlaceholderCount = 0
            )
        )
        val saved = database.repositoryQueries
            .selectAllRepositories(
                current_timestamp = 0L,
                limit = 10,
                offset = 0
            )
            .executeAsList()

        // Then
        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
        assertThat(saved.size).isEqualTo(1)
        assertThat(saved.first().id).isEqualTo(100)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `load should return error when network fails`() = runTest {
        // Given
        coEvery {
            apiService.getAllRepositories()
        } returns Result.failure(SocketTimeoutException("Connection timed out"))

        // When
        val result = mediator.load(
            loadType = LoadType.REFRESH,
            state = PagingState(
                pages = emptyList(),
                anchorPosition = null,
                config = PagingConfig(pageSize = 10),
                leadingPlaceholderCount = 0
            )
        )

        // Then
        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Error::class.java)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `initialize should skip initial refresh when cache is valid`() = runTest {
        // Given
        val futureTtl = System.currentTimeMillis() + 3_600_000L // + 1 час
        database.repositoryQueries.insertRepository(
            id = 200,
            owner = "owner",
            name = "cached_repo",
            url = "",
            description = "Local",
            language = "Java",
            license = null,
            stars_count = 0,
            forks_count = 0,
            watchers_count = 0,
            issues_count = 0,
            cache_ttl = futureTtl
        )

        // When
        val initResult = mediator.initialize()

        // Then
        assertThat(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
            .isEqualTo(initResult)

        coVerify(exactly = 0) {
            apiService.getAllRepositories()
        }

        val cached = database.repositoryQueries
            .selectAllRepositories(
                current_timestamp = System.currentTimeMillis(),
                limit = 10,
                offset = 0
            )
            .executeAsList()

        assertThat(cached.size).isEqualTo(1)
        assertThat(cached.first().id).isEqualTo(200)
    }
}