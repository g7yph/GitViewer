package dev.icerock.gitviewer.presentation.ui.issue.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.component.MainTopAppBar
import dev.icerock.gitviewer.presentation.designsystem.icon.GVIcons
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTypography
import dev.icerock.gitviewer.presentation.designsystem.theme.Gray70
import dev.icerock.gitviewer.presentation.designsystem.theme.Green70
import dev.icerock.gitviewer.presentation.designsystem.theme.Red50
import dev.icerock.gitviewer.presentation.model.IssueItemModel
import dev.icerock.gitviewer.presentation.model.IssueStateModel
import dev.icerock.gitviewer.presentation.ui.common.ErrorContent
import dev.icerock.gitviewer.presentation.ui.issue.info.model.IssueInfoAction
import dev.icerock.gitviewer.presentation.ui.issue.info.model.IssueInfoEvent
import dev.icerock.gitviewer.presentation.ui.issue.info.model.IssueInfoUiState

@Composable
internal fun IssueInfoRoute(
    viewModel: IssueInfoViewModel,
    number: Int,
    onNavigateUp: () -> Unit
) {
    val state by viewModel.uiStates().collectAsStateWithLifecycle()
    val action by viewModel.uiActions().collectAsStateWithLifecycle(initialValue = null)

    IssueInfoScreen(
        issueInfoUiState = state,
        number = number,
        onEvent = viewModel::onEvent
    )

    when (action) {
        IssueInfoAction.OpenPreviousScreen -> onNavigateUp()

        null -> {}
    }

    if (action != null) viewModel.clearAction()
}

@Composable
private fun IssueInfoScreen(
    issueInfoUiState: IssueInfoUiState,
    number: Int,
    onEvent: (IssueInfoEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        val fetchIssueEvent = IssueInfoEvent.FetchIssue(number = number)
        onEvent(fetchIssueEvent)
    }

    Column(modifier = modifier) {
        MainTopAppBar(
            title = { Text(text = stringResource(id = R.string.issue_number, number)) },
            navigationIcon = {
                IconButton(onClick = { onEvent(IssueInfoEvent.Back) }) {
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
                issueInfoUiState.isIssueLoading -> {
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

                issueInfoUiState.issue == null -> {
                    ErrorContent(
                        model = issueInfoUiState.issueError,
                        onRetryClick = {
                            val fetchIssueEvent = IssueInfoEvent.FetchIssue(number = number)
                            onEvent(fetchIssueEvent)
                        }
                    )
                }

                else -> IssueInfoContent(model = issueInfoUiState.issue)
            }
        }
    }
}

@Composable
private fun IssueInfoContent(
    model: IssueItemModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxHeight(),
                colors = when (model.state) {
                    IssueStateModel.Open -> CardDefaults.cardColors(
                        containerColor = Green70.copy(alpha = 0.2f),
                        contentColor = Green70
                    )

                    IssueStateModel.Closed -> CardDefaults.cardColors(
                        containerColor = Red50.copy(alpha = 0.2f),
                        contentColor = Red50
                    )
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (model.state) {
                            IssueStateModel.Open -> stringResource(id = R.string.opened_issue)
                            IssueStateModel.Closed -> stringResource(id = R.string.closed_issue)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = GVTypography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = Gray70.copy(alpha = 0.2f),
                    contentColor = Gray70
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.created_at_date, model.date),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = GVTypography.bodySmall
                    )
                }
            }
        }

        Text(
            text = model.name,
            fontWeight = FontWeight.Medium,
            style = GVTypography.titleMedium
        )

        model.description?.let {
            Text(
                text = stringResource(id = R.string.description),
                color = Gray70,
                fontWeight = FontWeight.Medium,
                style = GVTypography.bodyLarge
            )

            Text(
                text = it,
                style = GVTypography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
private fun IssueInfoScreenPreview() {
    GVTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            IssueInfoScreen(
                issueInfoUiState = IssueInfoUiState(
                    isIssueLoading = false,
                    issue = IssueItemModel(
                        id = 1,
                        number = 12,
                        name = "Sample name",
                        state = IssueStateModel.Open,
                        date = "12 Nov",
                        description = "Sample description ".repeat(5),
                    )
                ),
                number = 98,
                onEvent = {}
            )
        }
    }
}