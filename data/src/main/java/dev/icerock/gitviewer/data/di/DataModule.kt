package dev.icerock.gitviewer.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.datasource.local.KeyValueStorage
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.data.repository.AuthRepositoryImpl
import dev.icerock.gitviewer.data.repository.RepoRepository
import dev.icerock.gitviewer.data.repository.RepoRepositoryImpl
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataModule {

    @Provides
    @Singleton
    fun provideKeyValueStorage(@ApplicationContext context: Context): KeyValueStorage {
        return KeyValueStorage(context = context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(retrofit: Retrofit, keyValueStorage: KeyValueStorage): AuthRepository {
        return AuthRepositoryImpl(
            gitHubApiService = retrofit.create(GitHubApiService::class.java),
            keyValueStorage = keyValueStorage
        )
    }

    @Provides
    @Singleton
    fun provideRepoRepository(retrofit: Retrofit): RepoRepository {
        return RepoRepositoryImpl(gitHubApiService = retrofit.create(GitHubApiService::class.java))
    }
}