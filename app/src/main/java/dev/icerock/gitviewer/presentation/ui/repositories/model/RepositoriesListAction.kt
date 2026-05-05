package dev.icerock.gitviewer.presentation.ui.repositories.model

internal sealed interface RepositoriesListAction {
    data object OpenAuthScreen : RepositoriesListAction

    class OpenRepositoryInfoScreen(val id: Long, val owner: String, val name: String) : RepositoriesListAction
}