package dev.icerock.gitviewer.data.repository

import dev.icerock.gitviewer.data.datasource.local.KeyValueStorage
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import kotlinx.coroutines.flow.first

internal class AuthRepositoryImpl(
    private val gitHubApiService: GitHubApiService,
    private val keyValueStorage: KeyValueStorage
) : AuthRepository {
    override suspend fun signIn(token: String): Result<Unit> {
        keyValueStorage.updateToken(newToken = token)

        return gitHubApiService.checkAuth().onFailure { signOut() }
    }

    override suspend fun signOut() {
        keyValueStorage.invalidateToken()
    }

    override suspend fun isAuthorized(): Boolean {
        val token = keyValueStorage.tokenFlow().first()
        return !token.isNullOrEmpty()

        // TODO: добавить проверку с checkAuth()
    }
}