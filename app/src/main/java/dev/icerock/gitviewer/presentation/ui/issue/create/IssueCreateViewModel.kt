package dev.icerock.gitviewer.presentation.ui.issue.create

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.data.datasource.remote.model.IssueInputDto
import dev.icerock.gitviewer.data.repository.IssueRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateAction
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateEvent
import dev.icerock.gitviewer.presentation.ui.issue.create.model.IssueCreateUiState
import dev.icerock.moko.fields.core.validate
import dev.icerock.moko.fields.core.validations.ValidationResult
import dev.icerock.moko.fields.core.validations.matchRegex
import dev.icerock.moko.fields.core.validations.notBlank
import dev.icerock.moko.fields.flow.FormField
import dev.icerock.moko.fields.flow.flowBlock
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class IssueCreateViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : BaseViewModel<IssueCreateUiState, IssueCreateAction, IssueCreateEvent>(
    initialState = IssueCreateUiState()
) {
    override fun onEvent(uiEvent: IssueCreateEvent) {
        when (uiEvent) {
            is IssueCreateEvent.TitleChanged -> titleField.data.value = uiEvent.title

            is IssueCreateEvent.DescriptionChanged -> descriptionField.data.value = uiEvent.description

            is IssueCreateEvent.SubmitIssue -> {
                submitIssue(repoOwner = uiEvent.repoOwner, repoName = uiEvent.repoName)
            }

            IssueCreateEvent.Back -> uiAction = IssueCreateAction.OpenPreviousScreen
        }
    }

    val validFieldPattern = "^[a-zA-Z0-9\\s.,!?\\-_()]+$".toRegex()

    val titleField: FormField<String, StringDesc> = FormField(
        scope = viewModelScope,
        initialValue = "",
        validation = flowBlock { title ->
            ValidationResult.of(title) {
                notBlank(errorText = R.string.empty_title.strResDesc())
                matchRegex(
                    errorText = R.string.invalid_title.strResDesc(),
                    regex = validFieldPattern
                )
            }
        }
    )

    val descriptionField: FormField<String, StringDesc> = FormField(
        scope = viewModelScope,
        initialValue = "",
        validation = flowBlock { description ->
            if (description.isEmpty()) return@flowBlock null

            ValidationResult.of(description) {
                matchRegex(
                    errorText = R.string.invalid_description.strResDesc(),
                    regex = validFieldPattern
                )
            }
        }
    )

    private val allFields = listOf(titleField, descriptionField)

    private fun submitIssue(repoOwner: String, repoName: String) {
        if (!allFields.validate()) return

        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)

            issueRepository.createIssue(
                repoOwner = repoOwner,
                repoName = repoName,
                issueInput = IssueInputDto(
                    title = titleField.value(),
                    description = descriptionField.value().ifEmpty { null }
                )
            ).onSuccess {
                uiAction = IssueCreateAction.OpenPreviousScreen
            }.onFailure { throwable ->
                uiState = uiState.copy(errorMessage = throwable.message ?: "")
                uiAction = IssueCreateAction.ShowIssueCreateFailedDialog
            }

            uiState = uiState.copy(isLoading = false)
        }
    }
}