package dev.icerock.gitviewer.presentation.ui.repositories.component

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTypography
import dev.icerock.gitviewer.presentation.model.RepoItemModel

@Composable
internal fun RepoItem(
    model: RepoItemModel,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = model.name,
                    color = MaterialTheme.colorScheme.primary,
                    style = GVTypography.bodyLarge
                )

                model.primaryLanguage?.let {
                    Text(
                        text = it,
                        style = GVTypography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            model.description?.let {
                Text(
                    text = it,
                    style = GVTypography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
private fun RepoItemPreview() {
    GVTheme {
        Surface {
            RepoItem(
                model = RepoItemModel(
                    id = 1,
                    owner = "Sample owner",
                    name = "Sample name",
                    description = "Sample description ".repeat(5),
                    primaryLanguage = "Kotlin",
                    link = "",
                    license = "MIT",
                    stars = 10,
                    forks = 3,
                    watchers = 10
                ),
                onClick = {}
            )
        }
    }
}