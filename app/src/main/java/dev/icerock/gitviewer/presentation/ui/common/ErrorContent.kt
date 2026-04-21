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
import dev.icerock.gitviewer.presentation.model.ErrorTypeModel

@Composable
internal fun ErrorContent(
    model: ErrorTypeModel,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (drawableId, reason, advice) = when (model) {
            ErrorTypeModel.NoInternet -> Triple(
                first = GVIcons.ConnectionError,
                second = stringResource(id = R.string.connection_error),
                third = stringResource(id = R.string.check_connection)
            )

            is ErrorTypeModel.Unknown -> Triple(
                first = GVIcons.Error,
                second = stringResource(id = R.string.unknown_error),
                third = model.message
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = reason
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = reason,
                color = MaterialTheme.colorScheme.error,
                style = GVTypography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = advice,
                color = MaterialTheme.colorScheme.onBackground,
                style = GVTypography.bodyMedium
            )
        }

        PrimaryButton(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.retry).uppercase())
        }
    }
}