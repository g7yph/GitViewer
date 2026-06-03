package dev.icerock.gitviewer.presentation.ui.issues

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDirections
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.component.MainTopAppBar
import dev.icerock.gitviewer.presentation.designsystem.icon.GVIcons
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.designsystem.theme.Gray30
import dev.icerock.gitviewer.presentation.designsystem.theme.Green50
import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.model.IssueItemModel
import dev.icerock.gitviewer.presentation.model.IssueStateModel
import dev.icerock.gitviewer.presentation.ui.common.EmptyContent
import dev.icerock.gitviewer.presentation.ui.common.ErrorContent
import dev.icerock.gitviewer.presentation.ui.issues.component.IssueItem
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListAction
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListEvent
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun IssuesListRoute(
    viewModel: IssuesListViewModel,
    repoId: Long,
    repoOwner: String,
    repoName: String,
    onNavigate: (NavDirections) -> Unit,
    onNavigateUp: () -> Unit
) {
    val issues = viewModel.issuesFlow.collectAsLazyPagingItems()
    val action by viewModel.uiActions().collectAsStateWithLifecycle(initialValue = null)

    IssuesListScreen(
        issues = issues,
        repoId = repoId,
        repoOwner = repoOwner,
        repoName = repoName,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize()
    )

    when (action) {
        IssuesListAction.OpenIssueCreateScreen -> {
            onNavigate(
                IssuesListFragmentDirections.issueCreateFragmentAction(
                    repoId = repoId,
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
    issues: LazyPagingItems<IssueItemModel>,
    repoId: Long,
    repoOwner: String,
    repoName: String,
    onEvent: (IssuesListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onEvent(IssuesListEvent.FetchIssues(repoId = repoId, repoOwner = repoOwner, repoName = repoName))
    }

    Scaffold(
        modifier = modifier,
        topBar = {
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(IssuesListEvent.CreateIssue) },
                containerColor = Green50,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = GVIcons.Plus),
                    contentDescription = stringResource(id = R.string.create_issue)
                )
            }
        }
    ) { scaffoldPadding ->
        PullToRefreshBox(
            isRefreshing = issues.loadState.refresh is LoadState.Loading,
            onRefresh = { issues.refresh() },
            modifier = Modifier.padding(paddingValues = scaffoldPadding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(count = issues.itemCount, key = issues.itemKey { it.id }) { index ->
                    issues[index]?.let { issue ->
                        IssueItem(
                            model = issue,
                            onClick = {
                                onEvent(IssuesListEvent.Issue(number = issue.number))
                            }
                        )

                        HorizontalDivider(color = Gray30)
                    }
                }

                issues.apply {
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
                            item {
                                EmptyContent(
                                    text = stringResource(R.string.no_issues),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 24.dp)
                                )
                            }
                        }
                    }
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
                issues = flowOf(
                    PagingData.from(
                        listOf(
                            IssueItemModel(
                                id = 1,
                                number = 12,
                                name = "Sample name",
                                state = IssueStateModel.Open,
                                date = "12 Nov",
                                description = "Sample description ".repeat(5),
                            ),
                            IssueItemModel(
                                id = 2,
                                number = 1,
                                name = "Sample name",
                                state = IssueStateModel.Open,
                                date = "12 Nov",
                                description = "Sample description ".repeat(5),
                            ),
                            IssueItemModel(
                                id = 3,
                                number = 3,
                                name = "Sample name",
                                state = IssueStateModel.Closed,
                                date = "12 Nov",
                                description = "Sample description ".repeat(5),
                            )
                        )
                    )
                ).collectAsLazyPagingItems(),
                repoId = 0,
                repoOwner = "",
                repoName = "",
                onEvent = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}