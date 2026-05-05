package dev.icerock.gitviewer.data.repository

import android.util.Base64
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.paging3.QueryPagingSource
import dev.icerock.gitviewer.data.GitHubDatabase
import dev.icerock.gitviewer.data.Repository
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.paging.RepoRemoteMediator
import dev.icerock.gitviewer.data.util.CacheConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

internal class RepoRepositoryImpl(
    private val gitHubApiService: GitHubApiService,
    sqlDriver: SqlDriver
) : RepoRepository {

    private val gitHubDatabase = GitHubDatabase(sqlDriver)

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllRepositories(): Flow<PagingData<Repository>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 15,
                prefetchDistance = 10
            ),
            remoteMediator = RepoRemoteMediator(
                database = gitHubDatabase,
                apiService = gitHubApiService
            ),
            pagingSourceFactory = {
                val queries = gitHubDatabase.repositoryQueries

                QueryPagingSource(
                    countQuery = queries.countRepositories(CacheConstants.getCurrentTimestamp()),
                    transacter = queries,
                    context = Dispatchers.IO,
                    queryProvider = { limit, offset ->
                        queries.selectAllRepositories(
                            current_timestamp = CacheConstants.getCurrentTimestamp(),
                            limit = limit,
                            offset = offset
                        )
                    }
                )
            }
        ).flow
    }

    override suspend fun getRepository(id: Long): Result<Repository> {
        return try {
            val repository = gitHubDatabase.repositoryQueries
                .getRepositoryById(id = id)
                .executeAsOne()
            Result.success(repository)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    override suspend fun getRepositoryReadme(owner: String, name: String): Result<String> {
        return gitHubApiService.getRepositoryReadme(owner = owner, name = name).map {
            val bytes = Base64.decode(it.content, Base64.DEFAULT)
            String(bytes, Charsets.UTF_8)
        }
    }
}