package dev.icerock.gitviewer.presentation.ui.issues

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import dev.icerock.gitviewer.presentation.designsystem.component.PrimaryButton
import dev.icerock.gitviewer.presentation.designsystem.component.SecondaryButton
import dev.icerock.gitviewer.presentation.designsystem.icon.GVIcons
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.designsystem.theme.Gray30
import dev.icerock.gitviewer.presentation.ui.common.EmptyContent
import dev.icerock.gitviewer.presentation.ui.common.ErrorContent
import dev.icerock.gitviewer.presentation.ui.issues.component.IssueItem
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListAction
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListEvent
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListUiState

@Composable
internal fun IssuesListRoute(
    viewModel: IssuesListViewModel,
    repoOwner: String,
    repoName: String,
    onNavigate: (NavDirections) -> Unit,
    onNavigateUp: () -> Unit
) {
    val state by viewModel.uiStates().collectAsStateWithLifecycle()
    val action by viewModel.uiActions().collectAsStateWithLifecycle(initialValue = null)

    IssuesListScreen(
        issuesListUiState = state,
        repoOwner = repoOwner,
        repoName = repoName,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize()
    )

    when (action) {
        IssuesListAction.OpenIssueCreateScreen -> {
            onNavigate(
                IssuesListFragmentDirections.issueCreateFragmentAction(
                    repoOwner = repoOwner,
                    repoName = repoName
                )
            )
        }

        is IssuesListAction.OpenIssueInfoScreen -> {
            val number = (action as IssuesListAction.OpenIssueInfoScreen).number

            onNavigate(
                IssuesListFragmentDirections.issueInfoFragmentAction(
                    repoOwner = repoOwner,
                    repoName = repoName,
                    issueNumber = number
                )
            )
        }

        IssuesListAction.OpenPreviousScreen -> onNavigateUp()

        null -> {}
    }

    if (action != null) viewModel.clearAction()
}

@Composable
private fun IssuesListScreen(
    issuesListUiState: IssuesListUiState,
    repoOwner: String,
    repoName: String,
    onEvent: (IssuesListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onEvent(IssuesListEvent.FetchIssues(repoOwner = repoOwner, repoName = repoName))
    }

    Column(modifier = modifier) {
        MainTopAppBar(
            title = { Text(text = stringResource(id = R.string.issues)) },
            navigationIcon = {
                IconButton(onClick = { onEvent(IssuesListEvent.Back) }) {
                    Icon(
                        painter = painterResource(id = GVIcons.ArrowLeft),
                        contentDescription = stringResource(id = R.string.back_icon_description)
                    )
                }
            }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                issuesListUiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 7.dp
                    )
                }

                issuesListUiState.issues == null -> {
                    ErrorContent(
                        model = issuesListUiState.error,
                        onRetryClick = {
                            val fetchIssuesEvent = IssuesListEvent.FetchIssues(
                                repoOwner = repoOwner,
                                repoName = repoName
                            )

                            onEvent(fetchIssuesEvent)
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    if (issuesListUiState.issues.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(issuesListUiState.issues, key = { key -> key.id }) { issue ->
                                IssueItem(
                                    model = issue,
                                    onClick = {
                                        onEvent(IssuesListEvent.Issue(number = issue.number))
                                    }
                                )

                                HorizontalDivider(color = Gray30)
                            }
                        }
                    } else {
                        EmptyContent(
                            text = stringResource(id = R.string.no_issues),
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrimaryButton(
                            onClick = { onEvent(IssuesListEvent.CreateIssue) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(text = stringResource(id = R.string.create_issue).uppercase())
                        }

                        if (issuesListUiState.issues.isEmpty()) {
                            SecondaryButton(
                                onClick = {
                                    val fetchIssuesEvent = IssuesListEvent.FetchIssues(
                                        repoOwner = repoOwner,
                                        repoName = repoName
                                    )

                                    onEvent(fetchIssuesEvent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(text = stringResource(id = R.string.refresh).uppercase())
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun IssuesListScreenPreview() {
    GVTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            IssuesListScreen(
                issuesListUiState = IssuesListUiState(
                    isLoading = false,
                    issues = emptyList()
//                    issues = listOf(
//                        IssueItemModel(
//                            id = 1,
//                            number = 12,
//                            name = "Sample name",
//                            state = IssueStateModel.Open,
//                            date = "12 Nov",
//                            description = "Sample description ".repeat(5),
//                        ),
//                        IssueItemModel(
//                            id = 2,
//                            number = 1,
//                            name = "Sample name",
//                            state = IssueStateModel.Open,
//                            date = "12 Nov",
//                            description = "Sample description ".repeat(5),
//                        ),
//                        IssueItemModel(
//                            id = 3,
//                            number = 3,
//                            name = "Sample name",
//                            state = IssueStateModel.Closed,
//                            date = "12 Nov",
//                            description = "Sample description ".repeat(5),
//                        )
//                    )
                ),
                repoOwner = "",
                repoName = "",
                onEvent = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}