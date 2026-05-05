package dev.icerock.gitviewer.data.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.icerock.gitviewer.data.GitHubDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {

    @Provides
    @Singleton
    fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver {
        return AndroidSqliteDriver(
            schema = GitHubDatabase.Schema,
            context = context,
            name = "github.db"
        )
    }
}