package dev.icerock.gitviewer.presentation.ui.repositories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDirections
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.component.MainTopAppBar
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.designsystem.theme.Gray30
import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.model.RepoItemModel
import dev.icerock.gitviewer.presentation.ui.common.EmptyContent
import dev.icerock.gitviewer.presentation.ui.common.ErrorContent
import dev.icerock.gitviewer.presentation.ui.repositories.component.RepoItem
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListAction
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListEvent
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun RepositoriesListRoute(
    viewModel: RepositoriesListViewModel,
    onNavigate: (NavDirections) -> Unit
) {
    val repositories = viewModel.allRepositories.collectAsLazyPagingItems()
    val action by viewModel.uiActions().collectAsStateWithLifecycle(initialValue = null)

    RepositoriesListScreen(
        repositories = repositories,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize()
    )

    when (action) {
        RepositoriesListAction.OpenAuthScreen -> {
            onNavigate(RepositoriesListFragmentDirections.authFragmentAction())
        }

        is RepositoriesListAction.OpenRepositoryInfoScreen -> {
            val id = (action as RepositoriesListAction.OpenRepositoryInfoScreen).id
            val owner = (action as RepositoriesListAction.OpenRepositoryInfoScreen).owner
            val name = (action as RepositoriesListAction.OpenRepositoryInfoScreen).name

            onNavigate(
                RepositoriesListFragmentDirections.repositoryInfoFragmentAction(
                    repoId = id,
                    repoOwner = owner,
                    repoName = name
                )
            )
        }

        null -> {}
    }

    if (action != null) viewModel.clearAction()
}

@Composable
private fun RepositoriesListScreen(
    repositories: LazyPagingItems<RepoItemModel>,
    onEvent: (RepositoriesListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MainTopAppBar(
            title = { Text(text = stringResource(id = R.string.repositories)) },
            onSignOutClick = { onEvent(RepositoriesListEvent.SignOut) }
        )

        PullToRefreshBox(
            isRefreshing = repositories.loadState.refresh is LoadState.Loading,
            onRefresh = { repositories.refresh() }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(repositories.itemCount, key = repositories.itemKey { it.id }) { index ->
                    repositories[index]?.let { repo ->
                        val repositoryEvent = RepositoriesListEvent.Repository(
                            id = repo.id,
                            owner = repo.owner,
                            name = repo.name
                        )

                        RepoItem(
                            model = repo,
                            onClick = { onEvent(repositoryEvent) }
                        )

                        HorizontalDivider(color = Gray30)
                    }
                }

                repositories.apply {
                    when {
                        loadState.append is LoadState.Loading -> {
                            item { CircularProgressIndicator() }
                        }

                        loadState.refresh is LoadState.Error -> {
                            val errorMessage = (loadState.refresh as LoadState.Error).error.message ?: "Error"

                            item {
                                ErrorContent(model = ErrorTypeModel.Unknown(errorMessage))
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            val errorMessage = (loadState.append as LoadState.Error).error.message ?: "Error"

                            item {
                                ErrorContent(
                                    model = ErrorTypeModel.Unknown(errorMessage),
                                    onRetryClick = { retry() }
                                )
                            }
                        }

                        itemCount == 0 && loadState.append.endOfPaginationReached -> {
                            item { EmptyContent(text = stringResource(R.string.no_repositories) ) }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RepositoriesListScreenPreview() {
    GVTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RepositoriesListScreen(
                repositories = flowOf(
                    PagingData.from(
                        listOf(
                            RepoItemModel(
                                id = 1,
                                owner = "Sample owner",
                                name = "Sample name",
                                description = "Sample description ".repeat(5),
                                primaryLanguage = "Kotlin",
                                link = "",
                                license = "MIT",
                                stars = 10,
                                forks = 3,
                                watchers = 10,
                                issues = 3
                            ),
                            RepoItemModel(
                                id = 2,
                                owner = "Sample owner",
                                name = "Sample name",
                                description = "Sample description ".repeat(5),
                                primaryLanguage = "Kotlin",
                                link = "",
                                license = "MIT",
                                stars = 10,
                                forks = 3,
                                watchers = 10,
                                issues = 30
                            ),
                            RepoItemModel(
                                id = 3,
                                owner = "Sample owner",
                                name = "Sample name",
                                description = "Sample description ".repeat(5),
                                primaryLanguage = "Kotlin",
                                link = "",
                                license = "MIT",
                                stars = 10,
                                forks = 3,
                                watchers = 10,
                                issues = 12
                            )
                        )
                    )
                ).collectAsLazyPagingItems(),
                onEvent = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}