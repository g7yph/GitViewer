package dev.icerock.gitviewer.presentation.ui.issues.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTypography
import dev.icerock.gitviewer.presentation.designsystem.theme.Gray70
import dev.icerock.gitviewer.presentation.designsystem.theme.Green70
import dev.icerock.gitviewer.presentation.designsystem.theme.Red50
import dev.icerock.gitviewer.presentation.model.IssueItemModel
import dev.icerock.gitviewer.presentation.model.IssueStateModel

@Composable
internal fun IssueItem(
    model: IssueItemModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = model.name,
                color = MaterialTheme.colorScheme.primary,
                style = GVTypography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (model.state) {
                        IssueStateModel.Open -> stringResource(id = R.string.opened_issue)
                        IssueStateModel.Closed -> stringResource(id = R.string.closed_issue)
                    },
                    color = when (model.state) {
                        IssueStateModel.Open -> Green70
                        IssueStateModel.Closed -> Red50
                    },
                    style = GVTypography.bodyMedium
                )

                Text(
                    text = model.date,
                    color = Gray70,
                    style = GVTypography.bodySmall
                )
            }
        }
    }
}

@Preview
@Composable
private fun IssueItemPreview() {
    GVTheme {
        Surface {
            IssueItem(
                model = IssueItemModel(
                    id = 1,
                    number = 12,
                    name = "Sample name",
                    state = IssueStateModel.Open,
                    date = "12 Nov",
                    description = "Sample description ".repeat(5)
                ),
                onClick = {}
            )
        }
    }
}