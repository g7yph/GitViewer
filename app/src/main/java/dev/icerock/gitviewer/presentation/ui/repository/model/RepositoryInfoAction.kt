package dev.icerock.gitviewer.presentation.ui.repository.model

internal sealed interface RepositoryInfoAction {
    data object OpenPreviousScreen : RepositoryInfoAction

    data object OpenAuthScreen : RepositoryInfoAction
}