package dev.icerock.gitviewer.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.icerock.gitviewer.data.datasource.local.KeyValueStorage
import dev.icerock.gitviewer.data.util.AuthInterceptor
import dev.icerock.gitviewer.data.util.ResultCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(ResultCallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor = httpLoggingInterceptor)
            .addInterceptor(interceptor = authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(storage: KeyValueStorage): AuthInterceptor {
        return AuthInterceptor(keyValueStorage = storage)
    }
}