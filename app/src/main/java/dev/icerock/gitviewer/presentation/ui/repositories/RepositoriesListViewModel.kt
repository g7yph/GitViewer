package dev.icerock.gitviewer.presentation.ui.repositories

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.data.repository.RepoRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.mapper.toRepoModel
import dev.icerock.gitviewer.presentation.model.ErrorTypeModel
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListAction
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListEvent
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
internal class RepositoriesListViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repoRepository: RepoRepository
) : BaseViewModel<RepositoriesListUiState, RepositoriesListAction, RepositoriesListEvent>(
    initialState = RepositoriesListUiState()
) {
    override fun onEvent(uiEvent: RepositoriesListEvent) {
        when (uiEvent) {
            RepositoriesListEvent.FetchRepositories -> fetchRepositories()

            is RepositoriesListEvent.Repository -> {
                uiAction = RepositoriesListAction.OpenRepositoryInfoScreen(
                    owner = uiEvent.owner,
                    name = uiEvent.name
                )
            }

            RepositoriesListEvent.SignOut -> signOut()
        }
    }

    private fun fetchRepositories() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)

            repoRepository.getAllRepositories()
                .onSuccess { repos ->
                    uiState = uiState.copy(repos = repos.map { it.toRepoModel() })
                }
                .onFailure { throwable ->
                    when (throwable) {
                        is HttpException if throwable.code() == 401 -> {
                            authRepository.signOut()
                            uiAction = RepositoriesListAction.OpenAuthScreen
                        }

                        is IOException -> {
                            uiState = uiState.copy(error = ErrorTypeModel.NoInternet)
                        }

                        else -> {
                            uiState = uiState.copy(error = ErrorTypeModel.Unknown(message = throwable.message ?: ""))
                        }
                    }
                }

            uiState = uiState.copy(isLoading = false)
        }
    }

    private fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signOut()
            uiAction = RepositoriesListAction.OpenAuthScreen
        }
    }
}