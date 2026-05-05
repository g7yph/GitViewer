package dev.icerock.gitviewer.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.icerock.gitviewer.data.datasource.local.SecureStorage
import dev.icerock.gitviewer.data.util.network.AuthInterceptor
import dev.icerock.gitviewer.data.util.network.HttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(okHttpClient: OkHttpClient): HttpClient {
        return HttpClient(okHttpClient = okHttpClient)
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
    fun provideAuthInterceptor(storage: SecureStorage): AuthInterceptor {
        return AuthInterceptor(secureStorage = storage)
    }
}