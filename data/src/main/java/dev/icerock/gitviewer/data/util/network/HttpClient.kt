package dev.icerock.gitviewer.data.util.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

internal class HttpClient(private val okHttpClient: OkHttpClient) {
    private val json = Json { ignoreUnknownKeys = true }

    fun getApiClient(api: BackendApi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(api.value)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ResultCallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }
}