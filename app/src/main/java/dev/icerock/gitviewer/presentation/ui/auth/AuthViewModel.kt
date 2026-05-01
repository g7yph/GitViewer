package dev.icerock.gitviewer.presentation.ui.auth

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.gitviewer.R
import dev.icerock.gitviewer.data.repository.AuthRepository
import dev.icerock.gitviewer.presentation.base.BaseViewModel
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthAction
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthEvent
import dev.icerock.gitviewer.presentation.ui.auth.model.AuthUiState
import dev.icerock.moko.fields.core.validations.ValidationResult
import dev.icerock.moko.fields.core.validations.notBlank
import dev.icerock.moko.fields.flow.FormField
import dev.icerock.moko.fields.flow.flowBlock
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
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
            is AuthEvent.TokenChanged -> tokenField.data.value = uiEvent.token

            AuthEvent.SignIn -> signIn()
        }
    }

    val tokenField: FormField<String, StringDesc> = FormField(
        scope = viewModelScope,
        initialValue = "",
        validation = flowBlock { token ->
            ValidationResult.of(token) {
                notBlank(R.string.empty_token.strResDesc())
            }
        }
    )

    private fun signIn() {
        if (!tokenField.validate()) return

        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)

            authRepository.signIn(token = tokenField.value())
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