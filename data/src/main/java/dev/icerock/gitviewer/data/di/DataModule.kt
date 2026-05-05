package dev.icerock.gitviewer.data.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.icerock.gitviewer.data.datasource.local.SecureStorage
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.datasource.remote.ImageBBApiService
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.data.repository.AuthRepositoryImpl
import dev.icerock.gitviewer.data.repository.ImageRepository
import dev.icerock.gitviewer.data.repository.ImageRepositoryImpl
import dev.icerock.gitviewer.data.repository.IssueRepository
import dev.icerock.gitviewer.data.repository.IssueRepositoryImpl
import dev.icerock.gitviewer.data.repository.RepoRepository
import dev.icerock.gitviewer.data.repository.RepoRepositoryImpl
import dev.icerock.gitviewer.data.util.CryptoManager
import dev.icerock.gitviewer.data.util.network.BackendApi
import dev.icerock.gitviewer.data.util.network.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataModule {

    @Provides
    @Singleton
    fun provideKeyValueStorage(@ApplicationContext context: Context): SecureStorage {
        return SecureStorage(context = context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(httpClient: HttpClient, secureStorage: SecureStorage): AuthRepository {
        return AuthRepositoryImpl(
            gitHubApiService = httpClient.getApiClient(BackendApi.GitHub)
                .create(GitHubApiService::class.java),
            secureStorage = secureStorage
        )
    }

    @Provides
    @Singleton
    fun provideRepoRepository(httpClient: HttpClient, sqlDriver: SqlDriver): RepoRepository {
        return RepoRepositoryImpl(
            gitHubApiService = httpClient.getApiClient(BackendApi.GitHub)
                .create(GitHubApiService::class.java),
            sqlDriver = sqlDriver
        )
    }

    @Provides
    @Singleton
    fun provideIssueRepository(httpClient: HttpClient, sqlDriver: SqlDriver): IssueRepository {
        return IssueRepositoryImpl(
            gitHubApiService = httpClient.getApiClient(BackendApi.GitHub)
                .create(GitHubApiService::class.java),
            sqlDriver = sqlDriver
        )
    }

    @Provides
    @Singleton
    fun provideImageRepository(httpClient: HttpClient): ImageRepository {
        return ImageRepositoryImpl(
            imageBBApiService = httpClient.getApiClient(BackendApi.Attachments)
                .create(ImageBBApiService::class.java)
        )
    }
}