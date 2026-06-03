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
import dev.icerock.gitviewer.data.datasource.remote.model.IssueDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.SocketTimeoutException
import kotlin.time.Clock

internal class IssuesRemoteMediatorIntegrationTest {
    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: GitHubDatabase
    private lateinit var apiService: GitHubApiService
    private lateinit var mediator: IssuesRemoteMediator

    @Before
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        GitHubDatabase.Schema.create(driver)
        database = GitHubDatabase(driver)

        apiService = mockk()

        mediator = IssuesRemoteMediator(
            database = database,
            apiService = apiService,
            repositoryId = 1L,
            owner = "owner",
            repo = "repo"
        )
    }

    @After
    fun tearDown() {
        driver.close()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `load should return data from database when network success`() = runTest {
        // Given
        val mockIssues = listOf(
            IssueDto(
                id = 1001,
                number = 1,
                name = "Bug: Login fails",
                description = "Users cannot login with valid credentials",
                state = "open",
                date = Clock.System.now()
            ),
            IssueDto(
                id = 1002,
                number = 2,
                name = "Feature: Dark mode",
                description = "Add dark theme support",
                state = "closed",
                date = Clock.System.now()
            )
        )
        coEvery {
            apiService.getRepositoryIssues("owner", "repo")
        } returns Result.success(mockIssues)

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

        val savedIssues = database.issueQueries
            .selectIssuesByRepository(
                repository_id = 1L,
                current_timestamp = 0L,
                limit = 10,
                offset = 0
            )
            .executeAsList()

        // Then
        assertThat(result).isInstanceOf(RemoteMediator.MediatorResult.Success::class.java)
        assertThat(savedIssues.size).isEqualTo(2)
        assertThat(savedIssues.first().id).isEqualTo(1001)
        assertThat(savedIssues.first().cache_ttl).isGreaterThan(System.currentTimeMillis())
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `load should return error when network fails`() = runTest {
        // Given
        coEvery {
            apiService.getRepositoryIssues(any(), any(), any())
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
        database.issueQueries.insertIssue(
            id = 999,
            repository_id = 1L,
            number = 99L,
            title = "Cached Issue",
            description = "This issue is cached",
            state = "open",
            created_at = "2026-01-01T00:00:00Z",
            cache_ttl = futureTtl
        )

        // When
        val initResult = mediator.initialize()

        // Then
        assertThat(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
            .isEqualTo(initResult)

        coVerify(exactly = 0) {
            apiService.getRepositoryIssues(any(), any(), any())
        }

        val cached = database.issueQueries
            .selectIssuesByRepository(
                repository_id = 1L,
                current_timestamp = System.currentTimeMillis(),
                limit = 10,
                offset = 0
            )
            .executeAsList()

        assertThat(cached.size).isEqualTo(1)
        assertThat(cached.first().id).isEqualTo(999)
    }
}