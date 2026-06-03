package dev.icerock.gitviewer.data.repository

import dev.icerock.gitviewer.data.datasource.local.SecureStorage
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import kotlinx.coroutines.flow.first

internal class AuthRepositoryImpl(
    private val gitHubApiService: GitHubApiService,
    private val secureStorage: SecureStorage
) : AuthRepository {
    override suspend fun signIn(token: String): Result<Unit> {
        secureStorage.updateToken(newToken = token)

        return gitHubApiService.checkAuth().onFailure { signOut() }
    }

    override suspend fun signOut() {
        secureStorage.invalidateToken()
    }

    override suspend fun isAuthorized(): Boolean {
        val token = secureStorage.tokenFlow().first()
        return !token.isNullOrEmpty()
    }
}