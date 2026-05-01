package dev.icerock.gitviewer.presentation.ui.repository

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.data.repository.RepoRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.mapper.toRepoItemModel
import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.ui.repository.model.RepositoryInfoAction
import dev.icerock.gitviewer.presentation.ui.repository.model.RepositoryInfoEvent
import dev.icerock.gitviewer.presentation.ui.repository.model.RepositoryInfoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
internal class RepositoryInfoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repoRepository: RepoRepository
) : BaseViewModel<RepositoryInfoUiState, RepositoryInfoAction, RepositoryInfoEvent>(
    initialState = RepositoryInfoUiState()
) {
    override fun onEvent(uiEvent: RepositoryInfoEvent) {
        when (uiEvent) {
            is RepositoryInfoEvent.FetchRepository -> {
                fetchRepository(owner = uiEvent.owner, name = uiEvent.name)
            }

            RepositoryInfoEvent.FetchRepositoryReadme -> fetchRepositoryReadme()

            RepositoryInfoEvent.ViewIssues -> uiAction = RepositoryInfoAction.OpenIssuesListScreen

            RepositoryInfoEvent.SignOut -> signOut()

            RepositoryInfoEvent.Back -> uiAction = RepositoryInfoAction.OpenPreviousScreen
        }
    }

    private fun fetchRepository(owner: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isRepoLoading = true)

            repoRepository.getRepository(owner = owner, name = name)
                .onSuccess { repo ->
                    uiState = uiState.copy(repo = repo.toRepoItemModel())
                    fetchRepositoryReadme()
                }
                .onFailure { throwable ->
                    val errorType = when (throwable) {
                        is IOException -> ErrorTypeModel.NoInternet

                        else -> ErrorTypeModel.Unknown(message = throwable.message ?: "")
                    }

                    uiState = uiState.copy(repoError = errorType)
                }

            uiState = uiState.copy(isRepoLoading = false)
        }
    }

    private fun fetchRepositoryReadme() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isRepoReadmeLoading = true)

            repoRepository.getRepositoryReadme(owner = uiState.repo!!.owner, name = uiState.repo!!.name)
                .onSuccess { repoReadme ->
                    uiState = uiState.copy(repoReadme = repoReadme)
                }
                .onFailure { throwable ->
                    val errorType = when (throwable) {
                        is IOException -> ErrorTypeModel.NoInternet

                        else -> ErrorTypeModel.Unknown(message = throwable.message ?: "")
                    }

                    uiState = uiState.copy(repoReadmeError = errorType)
                }

            uiState = uiState.copy(isRepoReadmeLoading = false)
        }
    }

    private fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signOut()
            uiAction = RepositoryInfoAction.OpenAuthScreen
        }
    }
}