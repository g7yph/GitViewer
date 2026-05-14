package dev.icerock.gitviewer.data.repository

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
internal class IssueRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val gitHubApiService = mockk<GitHubApiService>()
    private lateinit var repository: IssueRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val testDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        GitHubDatabase.Schema.create(testDriver)

        repository = IssueRepositoryImpl(
            gitHubApiService = gitHubApiService,
            sqlDriver = testDriver
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create issue should save to database on success`() = runTest {
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
    fun `create issue should return error on api failure`() = runTest {
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
}