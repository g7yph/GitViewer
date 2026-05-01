package dev.icerock.gitviewer.presentation.ui.repositories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDirections
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.component.MainTopAppBar
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.designsystem.theme.Gray30
import dev.icerock.gitviewer.presentation.model.RepoItemModel
import dev.icerock.gitviewer.presentation.ui.common.EmptyContent
import dev.icerock.gitviewer.presentation.ui.common.ErrorContent
import dev.icerock.gitviewer.presentation.ui.repositories.component.RepoItem
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListAction
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListEvent
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListUiState

@Composable
internal fun RepositoriesListRoute(
    viewModel: RepositoriesListViewModel,
    onNavigate: (NavDirections) -> Unit
) {
    val state by viewModel.uiStates().collectAsStateWithLifecycle()
    val action by viewModel.uiActions().collectAsStateWithLifecycle(initialValue = null)

    RepositoriesListScreen(
        repositoriesListUiState = state,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize()
    )

    when (action) {
        RepositoriesListAction.OpenAuthScreen -> {
            onNavigate(RepositoriesListFragmentDirections.authFragmentAction())
        }

        is RepositoriesListAction.OpenRepositoryInfoScreen -> {
            val owner = (action as RepositoriesListAction.OpenRepositoryInfoScreen).owner
            val name = (action as RepositoriesListAction.OpenRepositoryInfoScreen).name

            onNavigate(
                RepositoriesListFragmentDirections.repositoryInfoFragmentAction(
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
    repositoriesListUiState: RepositoriesListUiState,
    onEvent: (RepositoriesListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onEvent(RepositoriesListEvent.FetchRepositories)
    }

    Column(modifier = modifier) {
        MainTopAppBar(
            title = { Text(text = stringResource(id = R.string.repositories)) },
            onSignOutClick = { onEvent(RepositoriesListEvent.SignOut) }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                repositoriesListUiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 7.dp
                    )
                }

                repositoriesListUiState.repos == null -> {
                    ErrorContent(
                        model = repositoriesListUiState.error,
                        onRetryClick = { onEvent(RepositoriesListEvent.FetchRepositories) },
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    if (repositoriesListUiState.repos.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(repositoriesListUiState.repos, key = { key -> key.id }) { repo ->
                                val repositoryEvent = RepositoriesListEvent.Repository(
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
                    } else {
                        EmptyContent(
                            text = stringResource(id = R.string.no_repositories),
                            onRefreshClick = { onEvent(RepositoriesListEvent.FetchRepositories) },
                            modifier = Modifier.padding(16.dp)
                        )
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
                repositoriesListUiState = RepositoriesListUiState(
                    isLoading = false,
                    repos = listOf(
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
                ),
                onEvent = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}