package dev.icerock.gitviewer.data.repository

interface AuthRepository {
    suspend fun signIn(token: String): Result<Unit>

    suspend fun signOut()

    suspend fun isAuthorized(): Boolean
}