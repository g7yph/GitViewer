package dev.icerock.gitviewer.data.util.network

import dev.icerock.gitviewer.data.datasource.local.SecureStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(private val secureStorage: SecureStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request().newBuilder()
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .build()

        val accessToken = runBlocking { secureStorage.tokenFlow().first() }

        if (!accessToken.isNullOrEmpty()) {
            val authorizedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()

            return chain.proceed(request = authorizedRequest)
        }

        return chain.proceed(request = originalRequest)
    }
}