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

    companion object {
        private const val STARTING_PAGE_INDEX = 0
    }

    private val issuesQueries = database.issueQueries

    override suspend fun initialize(): InitializeAction {
        return when {
            // Кэша нет или он устарел — обязательно загружаем из сети
            !hasValidCache() -> InitializeAction.LAUNCH_INITIAL_REFRESH

            // Кэш актуален — показываем его сразу, синхронизацию откладываем
            // (обновление по pull-to-refresh или по таймеру)
            else -> InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Issue>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> STARTING_PAGE_INDEX

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    // Берём ключ следующей страницы из состояния Paging
                    val lastPage = state.pages.lastOrNull { it.data.isNotEmpty() }
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    lastPage.nextKey ?: STARTING_PAGE_INDEX
                }
            }

            val response = apiService.getRepositoryIssues(
                owner = owner,
                name = repo,
                page = page + 1,
                perPage = state.config.pageSize
            )

            response.onFailure {
                return MediatorResult.Error(
                    Throwable("API Error: ${it.message}")
                )
            }

            val issues = response.getOrNull() ?: emptyList()
            val endOfPaginationReached = issues.size < state.config.pageSize

            database.transaction {
                if (loadType == LoadType.REFRESH) {
                    issuesQueries.deleteIssuesByRepository(repositoryId)
                }

                issues.forEach { dto ->
                    issuesQueries.insertIssue(
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
            }

            issuesQueries.deleteExpiredIssues(CacheConstants.getCurrentTimestamp())

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private fun hasValidCache(): Boolean {
        val count = issuesQueries
            .countIssuesByRepository(
                repositoryId,
                CacheConstants.getCurrentTimestamp()
            )
            .executeAsOne()
        return count > 0
    }
}