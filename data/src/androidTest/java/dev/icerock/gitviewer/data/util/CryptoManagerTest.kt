package dev.icerock.gitviewer.data.util

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class CryptoManagerTest {

    private val cryptoManager = CryptoManager()
    private val cipherKey = "testKey"

    @Before
    fun setUp() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        if (keyStore.containsAlias(cipherKey)) {
            keyStore.deleteEntry(cipherKey)
        }
    }

    @Test
    fun decrypt_afterEncrypt_returnSameData() {
        val originalData = "Some data".toByteArray()

        val encryptedData = cryptoManager.encrypt(keyAlias = cipherKey, bytes = originalData)
        val decryptedData = cryptoManager.decrypt(keyAlias = cipherKey, bytes = encryptedData)

        assertThat(decryptedData).isEqualTo(originalData)
    }

    @Test
    fun encrypt_returnDifferentData() {
        val originalData = "Some data".toByteArray()

        val encryptedData = cryptoManager.encrypt(keyAlias = cipherKey, bytes = originalData)

        assertThat(encryptedData).isNotEqualTo(originalData)
    }

    @Test
    fun decrypt_withEmptyData_returnNull() {
        val emptyData = ByteArray(0)

        val decryptedData = cryptoManager.decrypt(keyAlias = cipherKey, bytes = emptyData)

        assertThat(decryptedData).isNull()
    }
}