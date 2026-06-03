package dev.icerock.gitviewer.data.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.google.common.truth.Truth.assertThat
import dev.icerock.gitviewer.data.GitHubDatabase
import dev.icerock.gitviewer.data.Issue
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.datasource.remote.model.IssueDto
import dev.icerock.gitviewer.data.datasource.remote.model.IssueInputDto
import io.mockk.coEvery
import io.mockk.every
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
internal class IssueRepositoryIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private val gitHubApiService = mockk<GitHubApiService>()
    private lateinit var repository: IssueRepositoryImpl
    private lateinit var sqlDriver: SqlDriver

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        sqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        GitHubDatabase.Schema.create(sqlDriver)

        repository = IssueRepositoryImpl(
            gitHubApiService = gitHubApiService,
            sqlDriver = sqlDriver
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        sqlDriver.close()
    }

    @Test
    fun `createIssue should save to database on success`() = runTest {
        // Given
        val issueDto = IssueDto(
            id = 0,
            number = 0,
            name = "",
            state = "open",
            date = mockk(),
            description = ""
        )
        val issueEntity = mockk<Issue>()

        every { issueEntity.id } returns 1
        every { issueEntity.repository_id } returns 100

        coEvery {
            gitHubApiService.createIssue(any(), any(), any())
        } returns Result.success(issueDto)

        // When
        val result = repository.createIssue(
            repoId = 0,
            repoOwner = "test-owner",
            repoName = "test-repo",
            issueInput = IssueInputDto(title = "", description = "")
        )

        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `createIssue should return error on api failure`() = runTest {
        // Given
        coEvery {
            gitHubApiService.createIssue(any(), any(), any())
        } returns Result.failure(mockk())

        // When
        val result = repository.createIssue(
            repoId = 0,
            repoOwner = "test-owner",
            repoName = "test-repo",
            issueInput = IssueInputDto(title = "", description = "")
        )

        // Then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getIssue should get data from database`() = runTest {
        // Given
        val futureTtl = System.currentTimeMillis() + 3_600_000L
        GitHubDatabase(sqlDriver).issueQueries.insertIssue(
            id = 999,
            repository_id = 1,
            number = 10,
            title = "Sample issue",
            description = "Sample description",
            state = "closed",
            created_at = "2026-01-01T00:00:00Z",
            cache_ttl = futureTtl
        )

        // When
        val result = repository.getIssue(number = 10)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow().id).isEqualTo(999)
    }
}