package dev.icerock.gitviewer.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.icerock.gitviewer.data.GitHubDatabase
import dev.icerock.gitviewer.data.Repository
import dev.icerock.gitviewer.data.datasource.remote.GitHubApiService
import dev.icerock.gitviewer.data.util.CacheConstants
import dev.icerock.gitviewer.data.util.CacheType

@OptIn(ExperimentalPagingApi::class)
internal class RepoRemoteMediator(
    private val database: GitHubDatabase,
    private val apiService: GitHubApiService
) : RemoteMediator<Int, Repository>() {

    private val repositoryQueries = database.repositoryQueries

    override suspend fun initialize(): InitializeAction {
        return when {
            !hasValidCache() -> InitializeAction.LAUNCH_INITIAL_REFRESH

            else -> InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Repository>
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

            val response = apiService.getAllRepositories(
                page = page,
                perPage = state.config.pageSize
            )

            response.onFailure {
                return@load MediatorResult.Error(Throwable("API Error: ${it.message}"))
            }

            val repositories = response.getOrNull() ?: emptyList()
            val endOfPaginationReached = page > 1 && repositories.size < state.config.pageSize

            database.transaction {
                repositories.forEach { dto ->
                    repositoryQueries.insertRepository(
                        id = dto.id,
                        owner = dto.owner.login,
                        name = dto.name,
                        url = dto.htmlUrl,
                        description = dto.description,
                        language = dto.language,
                        license = dto.license?.name,
                        stars_count = dto.stargazersCount.toLong(),
                        forks_count = dto.forksCount.toLong(),
                        watchers_count = dto.watchersCount.toLong(),
                        issues_count = dto.openIssuesCount.toLong(),
                        cache_ttl = CacheConstants.getCacheTtl(CacheType.REPOSITORIES)
                    )
                }
            }

            deleteExpiredCache()
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    /**
     * Проверяет наличие актуального кэша
     */
    private fun hasValidCache(): Boolean {
        val count = repositoryQueries
            .countRepositories(CacheConstants.getCurrentTimestamp())
            .executeAsOne()
        return count > 0
    }

    /**
     * Удаляет устаревшие записи
     */
    private fun deleteExpiredCache() {
        repositoryQueries.deleteExpiredRepositories(
            CacheConstants.getCurrentTimestamp()
        )
    }
}