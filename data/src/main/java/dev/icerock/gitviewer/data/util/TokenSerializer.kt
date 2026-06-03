package dev.icerock.gitviewer.data.util

import androidx.datastore.core.Serializer
import dev.icerock.gitviewer.data.BuildConfig
import dev.icerock.gitviewer.data.datasource.local.model.TokenDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

internal class TokenSerializer(private val cryptoManager: CryptoManager) : Serializer<TokenDto> {
    override val defaultValue: TokenDto
        get() = TokenDto()

    override suspend fun readFrom(input: InputStream): TokenDto {
        return try {
            val decryptedBytes = cryptoManager.decrypt(
                keyAlias = BuildConfig.CIPHER_KEY_ALIAS,
                bytes = input.use { it.readBytes() }
            )

            val tokensByteArray = decryptedBytes ?: return defaultValue

            Json.decodeFromString(tokensByteArray.decodeToString())
        } catch (_: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: TokenDto, output: OutputStream) {
        val tokensByteArray = Json.encodeToString(t).encodeToByteArray()

        val encryptedBytes = cryptoManager.encrypt(
            keyAlias = BuildConfig.CIPHER_KEY_ALIAS,
            bytes = tokensByteArray
        )

        withContext(Dispatchers.IO) {
            output.use { it.write(encryptedBytes) }
        }
    }
}