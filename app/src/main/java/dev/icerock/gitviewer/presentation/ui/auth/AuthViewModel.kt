package dev.icerock.gitviewer.presentation.ui.auth

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthAction
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthEvent
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthUiState, AuthAction, AuthEvent>(initialState = AuthUiState()) {
    override fun onEvent(uiEvent: AuthEvent) {
        when (uiEvent) {
            is AuthEvent.TokenChanged -> {
                uiState = uiState.copy(token = uiEvent.token)
            }

            AuthEvent.SignIn -> signIn()
        }
    }

    private fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)

            authRepository.signIn(token = uiState.token)
                .onSuccess {
                    uiState = uiState.copy(tokenIsValid = true)

                    uiAction = AuthAction.OpenRepositoriesListScreen
                }
                .onFailure { throwable ->
                    when (throwable) {
                        is HttpException if throwable.code() == 401 -> {
                            uiState = uiState.copy(tokenIsValid = false)
                        }

                        else -> {
                            uiState = uiState.copy(errorMessage = throwable.message ?: "")
                            uiAction = AuthAction.ShowAuthFailedDialog
                        }
                    }
                }

            uiState = uiState.copy(isLoading = false)
        }
    }
}