package dev.icerock.gitviewer.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.google.common.truth.Truth.assertThat
import dev.icerock.gitviewer.data.GitHubDatabase
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
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
internal class RepoRepositoryIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private val apiService = mockk<GitHubApiService>()
    private lateinit var repository: RepoRepositoryImpl
    private lateinit var sqlDriver: JdbcSqliteDriver

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        sqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        GitHubDatabase.Schema.create(sqlDriver)

        repository = RepoRepositoryImpl(
            gitHubApiService = apiService,
            sqlDriver = sqlDriver
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        sqlDriver.close()
    }

    @Test
    fun `getRepository should get data from database`() = runTest {
        // Given
        val futureTtl = System.currentTimeMillis() + 3_600_000L
        GitHubDatabase(sqlDriver).repositoryQueries.insertRepository(
            id = 99,
            owner = "owner",
            name = "cached_repo",
            url = "",
            description = "From DB",
            language = "Swift",
            license = null,
            stars_count = 0,
            forks_count = 0,
            watchers_count = 0,
            issues_count = 0,
            cache_ttl = futureTtl
        )

        // When
        val result = repository.getRepository(99)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow().id).isEqualTo(99)
    }
}