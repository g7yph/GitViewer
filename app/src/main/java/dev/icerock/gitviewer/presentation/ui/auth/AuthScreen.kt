package dev.icerock.gitviewer.presentation.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDirections
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.component.InformationDialog
import dev.icerock.gitviewer.presentation.designsystem.component.PrimaryButton
import dev.icerock.gitviewer.presentation.designsystem.component.PrimaryTextField
import dev.icerock.gitviewer.presentation.designsystem.icon.GVIcons
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthAction
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthEvent
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthUiState

@Composable
internal fun AuthRoute(
    viewModel: AuthViewModel,
    onNavigate: (NavDirections) -> Unit
) {
    val state by viewModel.uiStates().collectAsStateWithLifecycle()

    val token by viewModel.tokenField.data.collectAsStateWithLifecycle()
    val tokenError by viewModel.tokenField.error.collectAsStateWithLifecycle()

    val action by viewModel.uiActions().collectAsStateWithLifecycle(initialValue = null)
    val isAuthFailedDialogShow = rememberSaveable { mutableStateOf(false) }

    AuthScreen(
        authUiState = state,
        token = token,
        tokenError = tokenError?.toString(LocalContext.current),
        onEvent = viewModel::onEvent,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 16.dp)
            .padding(horizontal = 16.dp)
    )

    InformationDialog(
        showDialog = isAuthFailedDialogShow.value,
        onShowDialogChange = { isAuthFailedDialogShow.value = it },
        titleText = stringResource(id = R.string.error),
        dialogText = state.errorMessage
    )

    when (action) {
        AuthAction.OpenRepositoriesListScreen -> {
            onNavigate(AuthFragmentDirections.repositoriesListFragmentAction())
        }

        AuthAction.ShowAuthFailedDialog -> isAuthFailedDialogShow.value = true

        null -> {}
    }

    if (action != null) viewModel.clearAction()
}

@Composable
private fun AuthScreen(
    authUiState: AuthUiState,
    token: String,
    tokenError: String?,
    onEvent: (AuthEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = GVIcons.LauncherForeground),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )
        
        Spacer(modifier = Modifier.height(96.dp))

        PrimaryTextField(
            value = token,
            onValueChange = { onEvent(AuthEvent.TokenChanged(token = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.personal_access_token)) },
            supportingText = {
                Text(
                    text = if (!authUiState.tokenIsValid)
                        stringResource(id = R.string.invalid_token)
                    else
                        tokenError ?: ""
                )
            },
            isError = !authUiState.tokenIsValid || tokenError != null
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            onClick = { onEvent(AuthEvent.SignIn) },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            enabled = !authUiState.isLoading
        ) {
            if (authUiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Text(text = stringResource(id = R.string.sign_in).uppercase())
            }
        }
    }
}

@Preview
@Composable
private fun AuthScreenPreview() {
    GVTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AuthScreen(
                authUiState = AuthUiState(
                    isLoading = false,
                    tokenIsValid = false
                ),
                token = "",
                tokenError = "",
                onEvent = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, bottom = 16.dp)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}