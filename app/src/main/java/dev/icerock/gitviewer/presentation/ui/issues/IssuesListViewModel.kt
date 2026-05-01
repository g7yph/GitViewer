package dev.icerock.gitviewer.presentation.ui.issues

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.data.repository.IssueRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.mapper.toIssueItemModel
import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListAction
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListEvent
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
internal class IssuesListViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : BaseViewModel<IssuesListUiState, IssuesListAction, IssuesListEvent>(
    initialState = IssuesListUiState()
) {
    override fun onEvent(uiEvent: IssuesListEvent) {
        when (uiEvent) {
            is IssuesListEvent.FetchIssues -> {
                fetchIssues(
                    repoOwner = uiEvent.repoOwner,
                    repoName = uiEvent.repoName
                )
            }

            is IssuesListEvent.Issue -> {
                uiAction = IssuesListAction.OpenIssueInfoScreen(number = uiEvent.number)
            }

            IssuesListEvent.CreateIssue -> uiAction = IssuesListAction.OpenIssueCreateScreen

            IssuesListEvent.Back -> uiAction = IssuesListAction.OpenPreviousScreen
        }
    }

    private fun fetchIssues(repoOwner: String, repoName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)

            issueRepository.getAllRepositoryIssues(repoOwner = repoOwner, repoName = repoName)
                .onSuccess { repos ->
                    uiState = uiState.copy(issues = repos.map { it.toIssueItemModel() })
                }
                .onFailure { throwable ->
                    uiState = when (throwable) {
                        is IOException -> uiState.copy(error = ErrorTypeModel.NoInternet)

                        else -> uiState.copy(
                            error = ErrorTypeModel.Unknown(message = throwable.message ?: "")
                        )
                    }
                }

            uiState = uiState.copy(isLoading = false)
        }
    }
}