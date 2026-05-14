package dev.icerock.gitviewer.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.icerock.gitviewer.data.GitHubDatabase
import dev.icerock.gitviewer.data.Issue
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.util.CacheConstants
import dev.icerock.gitviewer.data.util.CacheType

@OptIn(ExperimentalPagingApi::class)
internal class IssuesRemoteMediator(
    private val database: GitHubDatabase,
    private val apiService: GitHubApiService,
    private val repositoryId: Long,
    private val owner: String,
    private val repo: String
) : RemoteMediator<Int, Issue>() {

    private val issueQueries = database.issueQueries
    private val repositoryQueries = database.repositoryQueries

    override suspend fun initialize(): InitializeAction {
        return when {
            !hasValidCache() -> InitializeAction.LAUNCH_INITIAL_REFRESH

            else -> InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Issue>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 0

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val lastKey = state.pages.lastOrNull { it.data.isNotEmpty() }?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    (lastKey / state.config.pageSize) + 1
                }
            }

            val response = apiService.getRepositoryIssues(
                owner = owner,
                name = repo,
                page = page + 1,
                perPage = state.config.pageSize
            )

            response.onFailure {
                return@load MediatorResult.Error(
                    Throwable("API Error: ${it.message}")
                )
            }

            val issues = response.getOrNull() ?: emptyList()
            val endOfPaginationReached = page > 1 && issues.size < state.config.pageSize

            database.transaction {
                issues.forEach { dto ->
                    issueQueries.insertIssue(
                        id = dto.id,
                        repository_id = repositoryId,
                        number = dto.number.toLong(),
                        title = dto.name,
                        description = dto.description,
                        state = dto.state,
                        created_at = dto.date.toString(),
                        cache_ttl = CacheConstants.getCacheTtl(CacheType.ISSUES)
                    )
                }

                repositoryQueries.updateIssuesCountAtRepository(
                    issues_count = issues.size.toLong(),
                    repository_id = repositoryId
                )
            }

            issueQueries.deleteExpiredIssues(CacheConstants.getCurrentTimestamp())
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private fun hasValidCache(): Boolean {
        val count = issueQueries
            .countIssuesByRepository(
                repositoryId,
                CacheConstants.getCurrentTimestamp()
            )
            .executeAsOne()
        return count > 0
    }
}