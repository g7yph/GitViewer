package dev.icerock.gitviewer.presentation.ui.issue.info

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.data.repository.IssueRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.mapper.toIssueItemModel
import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.ui.issue.info.model.IssueInfoAction
import dev.icerock.gitviewer.presentation.ui.issue.info.model.IssueInfoEvent
import dev.icerock.gitviewer.presentation.ui.issue.info.model.IssueInfoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
internal class IssueInfoViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : BaseViewModel<IssueInfoUiState, IssueInfoAction, IssueInfoEvent>(
    initialState = IssueInfoUiState()
) {
    override fun onEvent(uiEvent: IssueInfoEvent) {
        when (uiEvent) {
            is IssueInfoEvent.FetchIssue -> {
                fetchIssue(
                    repoOwner = uiEvent.repoOwner,
                    repoName = uiEvent.repoName,
                    number = uiEvent.number
                )
            }

            IssueInfoEvent.Back -> uiAction = IssueInfoAction.OpenPreviousScreen
        }
    }

    private fun fetchIssue(
        repoOwner: String,
        repoName: String,
        number: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isIssueLoading = true)

            issueRepository.getIssue(repoOwner = repoOwner, repoName = repoName, number = number)
                .onSuccess { result ->
                    uiState = uiState.copy(issue = result.toIssueItemModel())
                }.onFailure { throwable ->
                    val errorType = when (throwable) {
                        is IOException -> ErrorTypeModel.NoInternet

                        else -> ErrorTypeModel.Unknown(message = throwable.message ?: "")
                    }

                    uiState = uiState.copy(issueError = errorType)
                }

            uiState = uiState.copy(isIssueLoading = false)
        }
    }
}