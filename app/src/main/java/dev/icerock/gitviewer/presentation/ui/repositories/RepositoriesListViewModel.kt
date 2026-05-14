package dev.icerock.gitviewer.presentation.ui.repositories

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.data.repository.RepoRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.mapper.toRepoItemModel
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListAction
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListEvent
import dev.icerock.gitviewer.presentation.ui.repositories.model.RepositoriesListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RepositoriesListViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    repoRepository: RepoRepository
) : BaseViewModel<RepositoriesListUiState, RepositoriesListAction, RepositoriesListEvent>(
    initialState = RepositoriesListUiState()
) {
    override fun onEvent(uiEvent: RepositoriesListEvent) {
        when (uiEvent) {
            is RepositoriesListEvent.Repository -> {
                uiAction = RepositoriesListAction.OpenRepositoryInfoScreen(
                    id = uiEvent.id,
                    owner = uiEvent.owner,
                    name = uiEvent.name
                )
            }

            RepositoriesListEvent.SignOut -> signOut()
        }
    }

    val allRepositories = repoRepository.getAllRepositories()
        .map { it.map { model -> model.toRepoItemModel() } }
        .cachedIn(viewModelScope)

    private fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signOut()
            uiAction = RepositoriesListAction.OpenAuthScreen
        }
    }
}