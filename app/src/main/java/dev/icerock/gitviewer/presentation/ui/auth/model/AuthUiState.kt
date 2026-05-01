package dev.icerock.gitviewer.presentation.ui.auth.model

internal data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val tokenIsValid: Boolean = true
)