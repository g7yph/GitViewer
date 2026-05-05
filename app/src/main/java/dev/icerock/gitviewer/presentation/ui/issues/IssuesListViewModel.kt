package dev.icerock.gitviewer.presentation.ui.issues

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.data.repository.IssueRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.mapper.toIssueItemModel
import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.model.IssueItemModel
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListAction
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListEvent
import dev.icerock.gitviewer.presentation.ui.issues.model.IssuesListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
internal class IssuesListViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : BaseViewModel<Unit, IssuesListAction, IssuesListEvent>(initialState = Unit) {
    override fun onEvent(uiEvent: IssuesListEvent) {
        when (uiEvent) {
            is IssuesListEvent.FetchIssues -> {
                fetchIssues(
                    repoId = uiEvent.repoId,
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

    private val _issuesFlow = MutableStateFlow<PagingData<IssueItemModel>>(PagingData.empty())
    val issuesFlow: StateFlow<PagingData<IssueItemModel>> = _issuesFlow.asStateFlow()

    private fun fetchIssues(repoId: Long, repoOwner: String, repoName: String) {
        issueRepository.getAllRepositoryIssues(repoId = repoId, repoOwner = repoOwner, repoName = repoName)
            .cachedIn(viewModelScope)
            .onEach {
                _issuesFlow.value = it.map { issues -> issues.toIssueItemModel() }
            }
            .launchIn(viewModelScope)
    }
}