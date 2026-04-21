package dev.icerock.gitviewer.presentation.ui.repositories.model

internal sealed interface RepositoriesListEvent {
    data object FetchRepositories : RepositoriesListEvent

    class Repository(val owner: String, val name: String) : RepositoriesListEvent

    data object SignOut : RepositoriesListEvent
}