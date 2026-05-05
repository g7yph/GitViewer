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

    companion object {
        private const val STARTING_PAGE_INDEX = 0
    }

    private val repositoriesQueries = database.repositoryQueries

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
        state: PagingState<Int, Repository>
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

            // Загружаем данные из сети
            val response = apiService.getAllRepositories(
                page = page + 1,
                perPage = state.config.pageSize
            )

            response.onFailure {
                return MediatorResult.Error(
                    Throwable("API Error: ${it.message}")
                )
            }

            val repositories = response.getOrNull() ?: emptyList()
            val endOfPaginationReached = repositories.size < state.config.pageSize

            // Сохраняем данные в локальную БД с новым TTL
            database.transaction {
                if (loadType == LoadType.REFRESH) {
                    // При обновлении очищаем старые данные
                    repositoriesQueries.deleteAllRepositories()
                }

                repositories.forEach { dto ->
                    repositoriesQueries.insertRepository(
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

            // Очищаем устаревший кэш после сохранения
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
        val count = repositoriesQueries
            .countRepositories(CacheConstants.getCurrentTimestamp())
            .executeAsOne()
        return count > 0
    }

    /**
     * Удаляет устаревшие записи
     */
    private fun deleteExpiredCache() {
        repositoriesQueries.deleteExpiredRepositories(
            CacheConstants.getCurrentTimestamp()
        )
    }
}