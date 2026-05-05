package dev.icerock.gitviewer.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dev.icerock.gitviewer.data.BuildConfig
import dev.icerock.gitviewer.data.util.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userCredentials")

internal class SecureStorage(private val context: Context) {
    private val cryptoManager = CryptoManager()
    private val tokenKey = byteArrayPreferencesKey(name = "token")

    fun tokenFlow(): Flow<String?> = context.dataStore.data
        .map {
            it[tokenKey]?.let { tokenBytes ->
                cryptoManager.decrypt(
                    keyAlias = BuildConfig.CIPHER_KEY_ALIAS,
                    bytes = tokenBytes
                )?.decodeToString()
            }
        }

    suspend fun updateToken(newToken: String) {
        context.dataStore.edit {
            it[tokenKey] = cryptoManager.encrypt(
                keyAlias = BuildConfig.CIPHER_KEY_ALIAS,
                bytes = newToken.encodeToByteArray()
            )
        }
    }

    suspend fun invalidateToken() {
        context.dataStore.edit { it.clear() }
    }
}