package dev.icerock.gitviewer.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.paging3.QueryPagingSource
import dev.icerock.gitviewer.data.GitHubDatabase
import dev.icerock.gitviewer.data.Issue
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.datasource.remote.model.IssueInputDto
import dev.icerock.gitviewer.data.paging.IssuesRemoteMediator
import dev.icerock.gitviewer.data.util.CacheConstants
import dev.icerock.gitviewer.data.util.CacheType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

internal class IssueRepositoryImpl(
    private val gitHubApiService: GitHubApiService,
    sqlDriver: SqlDriver
) : IssueRepository {
    private val gitHubDatabase = GitHubDatabase(sqlDriver)

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllRepositoryIssues(repoId: Long, repoOwner: String, repoName: String): Flow<PagingData<Issue>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 15,
                prefetchDistance = 10
            ),
            remoteMediator = IssuesRemoteMediator(
                database = gitHubDatabase,
                apiService = gitHubApiService,
                repositoryId = repoId,
                owner = repoOwner,
                repo = repoName
            ),
            pagingSourceFactory = {
                val queries = gitHubDatabase.issueQueries

                QueryPagingSource(
                    countQuery = queries.countIssuesByRepository(
                        repoId,
                        CacheConstants.getCurrentTimestamp()
                    ),
                    transacter = queries,
                    context = Dispatchers.IO,
                    queryProvider = { limit, offset ->
                        queries.selectIssuesByRepository(
                            repoId,
                            current_timestamp = CacheConstants.getCurrentTimestamp(),
                            limit = limit,
                            offset = offset
                        )
                    }
                )
            }
        ).flow
    }

    override suspend fun createIssue(
        repoId: Long,
        repoOwner: String,
        repoName: String,
        issueInput: IssueInputDto
    ): Result<Unit> {
        return gitHubApiService.createIssue(
            owner = repoOwner,
            name = repoName,
            input = issueInput
        ).map { result ->
            gitHubDatabase.issueQueries.insertIssue(
                result.id,
                repoId,
                number = result.number.toLong(),
                title = result.name,
                description = result.description,
                state = result.state,
                created_at = result.date.toString(),
                cache_ttl = CacheConstants.getCacheTtl(CacheType.ISSUES)
            )
        }
    }

    override suspend fun getIssue(number: Long): Result<Issue> {
        return try {
            val issue = gitHubDatabase.issueQueries
                .getIssueByNumber(number = number)
                .executeAsOne()
            Result.success(issue)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }
}