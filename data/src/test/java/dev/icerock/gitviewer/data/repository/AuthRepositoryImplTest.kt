package dev.icerock.gitviewer.data.repository

import com.google.common.truth.Truth.assertThat
import dev.icerock.gitviewer.data.datasource.local.SecureStorage
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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
internal class AuthRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val secureStorage = mockk<SecureStorage>()
    private val gitHubApiService = mockk<GitHubApiService>()
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = AuthRepositoryImpl(gitHubApiService, secureStorage)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signIn saves token and returns success`() = runTest {
        // Given
        val token = "ghp_test_token"
        coEvery { secureStorage.updateToken(token) } just runs
        coEvery { gitHubApiService.checkAuth() } returns Result.success(Unit)

        // When
        val result = repository.signIn(token)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify { secureStorage.updateToken(token) }
        coVerify { gitHubApiService.checkAuth() }
    }

    @Test
    fun `signOut clears token`() = runTest {
        // Given
        coEvery { secureStorage.invalidateToken() } just runs

        // When
        repository.signOut()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { secureStorage.invalidateToken() }
    }

    @Test
    fun `isAuthorized returns true when token exists`() = runTest {
        // Given
        coEvery { secureStorage.tokenFlow() } returns flowOf("ghp_token")

        // When
        val result = repository.isAuthorized()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isAuthorized returns false when token is null`() = runTest {
        // Given
        coEvery { secureStorage.tokenFlow() } returns flowOf(null)

        // When
        val result = repository.isAuthorized()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(result).isFalse()
    }
}