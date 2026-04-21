package dev.icerock.gitviewer.data.util

import dev.icerock.gitviewer.data.datasource.local.KeyValueStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(private val keyValueStorage: KeyValueStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request().newBuilder()
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .build()

        val accessToken = runBlocking { keyValueStorage.tokenFlow().first() }

        if (!accessToken.isNullOrEmpty()) {
            val authorizedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()

            return chain.proceed(request = authorizedRequest)
        }

        return chain.proceed(request = originalRequest)
    }
}