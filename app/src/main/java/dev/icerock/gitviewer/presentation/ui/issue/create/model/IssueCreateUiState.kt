package dev.icerock.gitviewer.presentation.ui.issue.create.model

internal data class IssueCreateUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)