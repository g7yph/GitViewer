package dev.icerock.gitviewer.presentation.ui.auth.model

internal sealed interface AuthAction {
    data object ShowAuthFailedDialog : AuthAction

    data object OpenRepositoriesListScreen : AuthAction
}