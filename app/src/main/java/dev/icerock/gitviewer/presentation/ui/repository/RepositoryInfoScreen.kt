package dev.icerock.gitviewer.presentation.ui.repository

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDirections
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.component.MainTopAppBar
import dev.icerock.gitviewer.presentation.designsystem.icon.GVIcons
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.model.RepoItemModel
import dev.icerock.gitviewer.presentation.ui.common.ErrorContent
import dev.icerock.gitviewer.presentation.ui.repository.component.RepoDetailedInfoContent
import dev.icerock.gitviewer.presentation.ui.repository.model.RepositoryInfoAction
import dev.icerock.gitviewer.presentation.ui.repository.model.RepositoryInfoEvent
import dev.icerock.gitviewer.presentation.ui.repository.model.RepositoryInfoUiState

@Composable
internal fun RepositoryInfoRoute(
    viewModel: RepositoryInfoViewModel,
    owner: String,
    name: String,
    onNavigate: (NavDirections) -> Unit,
    onNavigateUp: () -> Unit
) {
    val state by viewModel.uiStates().collectAsStateWithLifecycle()
    val action by viewModel.uiActions().collectAsStateWithLifecycle(initialValue = null)

    RepositoryInfoScreen(
        repositoryInfoUiState = state,
        owner = owner,
        name = name,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize()
    )

    when (action) {
        RepositoryInfoAction.OpenPreviousScreen -> onNavigateUp()

        RepositoryInfoAction.OpenIssuesListScreen -> {
            onNavigate(
                RepositoryInfoFragmentDirections.issuesListFragmentAction(
                    repoOwner = owner,
                    repoName = name
                )
            )
        }

        RepositoryInfoAction.OpenAuthScreen -> {
            onNavigate(RepositoryInfoFragmentDirections.authFragmentAction())
        }

        null -> {}
    }

    if (action != null) viewModel.clearAction()
}

@Composable
private fun RepositoryInfoScreen(
    repositoryInfoUiState: RepositoryInfoUiState,
    owner: String,
    name: String,
    onEvent: (RepositoryInfoEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onEvent(RepositoryInfoEvent.FetchRepository(owner = owner, name = name))
    }

    Column(modifier = modifier) {
        MainTopAppBar(
            title = { Text(text = name) },
            onSignOutClick = { onEvent(RepositoryInfoEvent.SignOut) },
            navigationIcon = {
                IconButton(onClick = { onEvent(RepositoryInfoEvent.Back) }) {
                    Icon(
                        painter = painterResource(id = GVIcons.ArrowLeft),
                        contentDescription = stringResource(id = R.string.back_icon_description)
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                repositoryInfoUiState.isRepoLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(56.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                            strokeWidth = 7.dp
                        )
                    }
                }

                repositoryInfoUiState.repo == null -> {
                    ErrorContent(
                        model = repositoryInfoUiState.repoError,
                        onRetryClick = {
                            onEvent(RepositoryInfoEvent.FetchRepository(owner = owner, name = name))
                        }
                    )
                }

                else -> RepoDetailedInfoContent(
                    model = repositoryInfoUiState.repo,
                    readmeIsLoading = repositoryInfoUiState.isRepoReadmeLoading,
                    readmeError = repositoryInfoUiState.repoReadmeError,
                    readme = repositoryInfoUiState.repoReadme,
                    onAllIssuesClick = { onEvent(RepositoryInfoEvent.ViewIssues) },
                    onRetryReadmeClick = { onEvent(RepositoryInfoEvent.FetchRepositoryReadme) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun RepositoryInfoScreenPreview() {
    GVTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RepositoryInfoScreen(
                repositoryInfoUiState = RepositoryInfoUiState(
                    isRepoLoading = false,
                    repo = RepoItemModel(
                        id = 1,
                        owner = "Sample owner",
                        name = "Sample name",
                        description = "Sample description ".repeat(5),
                        primaryLanguage = "Kotlin",
                        link = "Repo url",
                        license = "MIT",
                        stars = 10,
                        forks = 3,
                        watchers = 10,
                        issues = 24
                    )
                ),
                owner = "",
                name = "Sample repository",
                onEvent = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}