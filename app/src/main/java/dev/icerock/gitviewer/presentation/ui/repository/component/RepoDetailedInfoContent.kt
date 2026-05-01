package dev.icerock.gitviewer.presentation.ui.repository.component

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.icon.GVIcons
import dev.icerock.gitviewer.presentation.designsystem.theme.Blue70
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTypography
import dev.icerock.gitviewer.presentation.designsystem.theme.Gray30
import dev.icerock.gitviewer.presentation.designsystem.theme.Gray70
import dev.icerock.gitviewer.presentation.designsystem.theme.Green70
import dev.icerock.gitviewer.presentation.designsystem.theme.Yellow70
import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.model.RepoItemModel
import dev.icerock.gitviewer.presentation.ui.common.ErrorContent
import io.noties.markwon.Markwon

@Composable
internal fun RepoDetailedInfoContent(
    model: RepoItemModel,
    readmeIsLoading: Boolean,
    readmeError: ErrorTypeModel,
    readme: String?,
    onAllIssuesClick: () -> Unit,
    onRetryReadmeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        RepoLink(model = model.link)

        Spacer(modifier = Modifier.height(24.dp))

        model.license?.let {
            RepoLicense(
                model = it,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RepoStatisticsItem(
                model = Pair(model.stars, stringResource(id = R.string.stars)),
                drawableIconId = GVIcons.Star,
                color = Yellow70
            )

            RepoStatisticsItem(
                model = Pair(model.forks, stringResource(id = R.string.forks)),
                drawableIconId = GVIcons.Fork,
                color = Green70
            )

            RepoStatisticsItem(
                model = Pair(model.watchers, stringResource(id = R.string.watchers)),
                drawableIconId = GVIcons.Eye,
                color = Blue70
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RepoStatisticsItem(
                model = Pair(model.issues, stringResource(id = R.string.issues)),
                drawableIconId = GVIcons.Question,
                color = Green70
            )

            TextButton(onClick = onAllIssuesClick) {
                Text(
                    text = stringResource(id = R.string.all_issues),
                    color = Gray70,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            readmeIsLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onBackground,
                    strokeWidth = 5.dp
                )
            }

            readme == null -> {
                ErrorContent(
                    model = readmeError,
                    onRetryClick = onRetryReadmeClick
                )
            }

            else -> {
                if (readme.isNotEmpty())
                    RepoMarkdownText(markdown = readme)
                else
                    Text(text = stringResource(id = R.string.without_readme))
            }
        }
    }
}

@Composable
private fun RepoLink(
    model: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Icon(
            painter = painterResource(id = GVIcons.Link),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = buildAnnotatedString {
                withLink(LinkAnnotation.Url(model)) { append(model) }
            },
            modifier = Modifier.fillMaxWidth(),
            style = GVTypography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
private fun RepoLicense(
    model: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = GVIcons.Scalepan),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(id = R.string.license),
                style = GVTypography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        }

        Text(
            text = model,
            style = GVTypography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
private fun RepoStatisticsItem(
    model: Pair<Int, String>,
    @DrawableRes drawableIconId: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = drawableIconId),
            contentDescription = null,
            tint = color
        )

        Text(
            text = model.first.toString(),
            color = color,
            style = GVTypography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )

        Text(
            text = model.second,
            style = GVTypography.bodyLarge
        )
    }
}

@Composable
private fun RepoMarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val markwon = remember { Markwon.create(context) }

    AndroidView(
        factory = {
            TextView(it).apply {
                movementMethod = LinkMovementMethod.getInstance()
                autoLinkMask = Linkify.WEB_URLS
                linksClickable = true
            }
        },
        modifier = modifier,
        update = { markwon.setMarkdown(it, markdown) }
    )
}