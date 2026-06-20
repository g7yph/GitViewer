package dev.icerock.gitviewer.presentation.ui.issue.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.presentation.designsystem.component.InformationDialog
import dev.icerock.gitviewer.presentation.designsystem.component.MainTopAppBar
import dev.icerock.gitviewer.presentation.designsystem.component.PrimaryButton
import dev.icerock.gitviewer.presentation.designsystem.component.PrimaryTextField
import dev.icerock.gitviewer.presentation.designsystem.icon.GVIcons
import dev.icerock.gitviewer.presentation.designsystem.modifier.flipScale
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTheme
import dev.icerock.gitviewer.presentation.designsystem.theme.GVTypography
import dev.icerock.gitviewer.presentation.designsystem.theme.Gray70
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateAction
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateEvent
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateUiState
import java.io.File

@Composable
internal fun IssueCreateRoute(
    viewModel: IssueCreateViewModel,
    repoId: Long,
    repoOwner: String,
    repoName: String,
    onNavigateUp: () -> Unit
) {
    val state by viewModel.uiStates().collectAsStateWithLifecycle()

    val title by viewModel.titleField.data.collectAsStateWithLifecycle()
    val titleError by viewModel.titleField.error.collectAsStateWithLifecycle()

    val description by viewModel.descriptionField.data.collectAsStateWithLifecycle()
    val descriptionError by viewModel.descriptionField.error.collectAsStateWithLifecycle()

    val action by viewModel.uiActions().collectAsStateWithLifecycle(initialValue = null)
    val isIssueCreateFailedDialogShow = rememberSaveable { mutableStateOf(false) }

    IssueCreateScreen(
        issueCreateUiState = state,
        repoId = repoId,
        repoOwner = repoOwner,
        repoName = repoName,
        title = title,
        titleError = titleError?.toString(LocalContext.current),
        description = description,
        descriptionError = descriptionError?.toString(LocalContext.current),
        onEvent = viewModel::onEvent
    )

    InformationDialog(
        showDialog = isIssueCreateFailedDialogShow.value,
        onShowDialogChange = { isIssueCreateFailedDialogShow.value = it },
        titleText = stringResource(id = R.string.error),
        dialogText = state.errorMessage
    )

    when (action) {
        IssueCreateAction.OpenPreviousScreen -> onNavigateUp()

        IssueCreateAction.ShowActionFailedDialog -> isIssueCreateFailedDialogShow.value = true

        null -> {}
    }

    if (action != null) viewModel.clearAction()
}

@Composable
private fun IssueCreateScreen(
    issueCreateUiState: IssueCreateUiState,
    repoId: Long,
    repoOwner: String,
    repoName: String,
    title: String,
    titleError: String?,
    description: String,
    descriptionError: String?,
    onEvent: (IssueCreateEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MainTopAppBar(
            title = { Text(text = stringResource(id = R.string.new_issue)) },
            navigationIcon = {
                IconButton(onClick = { onEvent(IssueCreateEvent.Back) }) {
                    Icon(
                        painter = painterResource(id = GVIcons.ArrowLeft),
                        contentDescription = stringResource(id = R.string.back_icon_description)
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 16.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
                PrimaryTextField(
                    value = title,
                    onValueChange = { onEvent(IssueCreateEvent.TitleChanged(title = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.title)) },
                    supportingText = { Text(text = titleError ?: "") },
                    isError = titleError != null
                )

                PrimaryTextField(
                    value = description,
                    onValueChange = { onEvent(IssueCreateEvent.DescriptionChanged(description = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.description)) },
                    supportingText = { Text(text = descriptionError ?: "") },
                    isError = descriptionError != null,
                    singleLine = false
                )

                AttachmentsSection(
                    images = issueCreateUiState.attachedImages,
                    imageIsAttaching = issueCreateUiState.imageIsAttaching,
                    onAttachImage = { onEvent(IssueCreateEvent.AttachImage(imageFile = it)) }
                )
            }

            PrimaryButton(
                onClick = {
                    val submitIssueEvent = IssueCreateEvent.SubmitIssue(
                        repoId = repoId,
                        repoOwner = repoOwner,
                        repoName = repoName
                    )

                    onEvent(submitIssueEvent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomEnd),
                enabled = !issueCreateUiState.isLoading
            ) {
                if (issueCreateUiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(text = stringResource(id = R.string.submit_issue).uppercase())
                }
            }
        }
    }
}

@Composable
private fun AttachmentsSection(
    images: List<String>,
    imageIsAttaching: Boolean,
    onAttachImage: (File) -> Unit
) {
    val context = LocalContext.current
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val file = context.contentResolver.openInputStream(uri)?.use { input ->
            val tempFile = File.createTempFile(
                "upload_",
                ".jpg",
                context.cacheDir
            )

            tempFile.outputStream().use { input.copyTo(it) }
            tempFile
        }

        if (file == null) return@rememberLauncherForActivityResult

        onAttachImage(file)
    }

    Column {
        TextButton(
            onClick = {
                val pickVisualMediaRequest = PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )

                pickMediaLauncher.launch(pickVisualMediaRequest)
            },
            colors = ButtonDefaults.textButtonColors(contentColor = Gray70)
        ) {
            Icon(
                painter = painterResource(id = GVIcons.Clip),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(
                    id = if (imageIsAttaching) R.string.uploading_files
                    else R.string.attach_files
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = GVTypography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            val isExpanded = remember { mutableStateOf(false) }

            TextButton(
                onClick = { isExpanded.value = !isExpanded.value },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    painter = painterResource(id = GVIcons.Gallery),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = images.count().toString(),
                    color = MaterialTheme.colorScheme.primary,
                    style = GVTypography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(id = R.string.attach_photos),
                    style = GVTypography.bodyLarge
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = GVIcons.ArrowUp),
                    contentDescription = null,
                    modifier = Modifier.flipScale(state = !isExpanded.value)
                )
            }

            if (isExpanded.value) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 3
                ) {
                    images.forEach { image ->
                        AsyncImage(
                            model = image,
                            contentDescription = null,
                            modifier = Modifier.weight(1f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }


}

@Preview
@Composable
private fun IssueCreateScreenPreview() {
    GVTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            IssueCreateScreen(
                issueCreateUiState = IssueCreateUiState(
                    isLoading = false,
                    errorMessage = "",
                    imageIsAttaching = true
                ),
                repoId = 0,
                repoOwner = "",
                repoName = "",
                title = "Sample title",
                titleError = null,
                description = "Sample description",
                descriptionError = null,
                onEvent = {}
            )
        }
    }
}