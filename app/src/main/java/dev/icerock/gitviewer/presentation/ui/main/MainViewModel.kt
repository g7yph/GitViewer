package dev.icerock.gitviewer.presentation.ui.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.ui.main.model.MainEvent
import dev.icerock.gitviewer.presentation.ui.main.model.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<MainUiState, Unit, MainEvent>(initialState = MainUiState()) {

    override fun onEvent(uiEvent: MainEvent) {
        when (uiEvent) {
            MainEvent.CheckAuth -> checkAuth()
        }
    }

    private fun checkAuth() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isAuthorized = authRepository.isAuthorized())
        }
    }
}