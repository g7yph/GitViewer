package dev.icerock.gitviewer.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import dev.icerock.gitviewer.data.datasource.local.model.TokenDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class SecureStorage(private val tokenDataStore: DataStore<TokenDto>) {

    fun tokenFlow(): Flow<String?> = tokenDataStore.data
        .distinctUntilChanged()
        .catch { e ->
            if (e is IOException) emit(TokenDto()) else throw e
        }
        .map { it.token }

    suspend fun updateToken(newToken: String) {
        tokenDataStore.updateData { TokenDto(token = newToken) }
    }

    suspend fun invalidateToken() {
        tokenDataStore.updateData { TokenDto() }
    }
}