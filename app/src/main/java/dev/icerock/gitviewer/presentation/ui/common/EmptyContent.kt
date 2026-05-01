package dev.icerock.gitviewer.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.component.PrimaryButton
import dev.icerock.gitviewer.presentation.designsystem.icon.GVIcons
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTypography

@Composable
internal fun EmptyContent(
    text: String,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val reason = stringResource(id = R.string.empty)

            Image(
                painter = painterResource(id = GVIcons.Empty),
                contentDescription = reason
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = reason,
                color = MaterialTheme.colorScheme.primary,
                style = GVTypography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground,
                style = GVTypography.bodyMedium
            )
        }

        PrimaryButton(
            onClick = onRefreshClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.refresh).uppercase())
        }
    }
}

@Composable
internal fun EmptyContent(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val reason = stringResource(id = R.string.empty)

        Image(
            painter = painterResource(id = GVIcons.Empty),
            contentDescription = reason
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = reason,
            color = MaterialTheme.colorScheme.primary,
            style = GVTypography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            style = GVTypography.bodyMedium
        )
    }
}